package com.nasportfolio.domain.restaurant.usecases

import com.nasportfolio.domain.comment.usecases.GetCommentsUseCase
import com.nasportfolio.domain.favorites.FavoriteRepository
import com.nasportfolio.domain.restaurant.Restaurant
import com.nasportfolio.domain.restaurant.RestaurantRepository
import com.nasportfolio.domain.restaurant.TransformedRestaurant
import com.nasportfolio.domain.utils.Resource
import com.nasportfolio.domain.utils.ResourceError
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class GetRestaurantsUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
    private val restaurantRepository: RestaurantRepository,
    private val getCommentsUseCase: GetCommentsUseCase,
) {
    sealed class Filter {
        object None : Filter()
        class GetUsersFavoriteRestaurants(val userId: String) : Filter()
    }

    operator fun invoke(
        filter: Filter = Filter.None,
        fetchFromRemote: Boolean = false,
    ): Flow<Resource<List<TransformedRestaurant>>> =
        restaurantRepository.getAllRestaurants(fetchFromRemote = fetchFromRemote)
            .onStart { emit(Resource.Loading(isLoading = true)) }
            .map {
                if (it !is Resource.Success) {
                    return@map when (it) {
                        is Resource.Failure -> Resource.Failure(it.error)
                        is Resource.Loading -> Resource.Loading(it.isLoading)
                        else -> throw IllegalStateException()
                    }
                }
                val result = withContext(Dispatchers.IO) {
                    coroutineScope {
                        it.result
                            .map { restaurant ->
                                async {
                                    transformedRestaurant(restaurant) ?: throw Exception(
                                        "Unable to fetch latest restaurant data. Please try again later"
                                    )
                                }
                            }
                            .awaitAll()
                            .filter { restaurant ->
                                when (filter) {
                                    is Filter.GetUsersFavoriteRestaurants -> {
                                        restaurant.favoriteUsers
                                            .map { user -> user.id }
                                            .contains(filter.userId)
                                    }
                                    else -> true
                                }
                            }
                    }
                }
                return@map Resource.Success(result)
            }
            .catch { e ->
                val error = ResourceError.DefaultError(e.message.toString())
                emit(Resource.Failure(error))
            }

    fun getById(restaurantId: String): Flow<Resource<TransformedRestaurant>> =
        restaurantRepository.getRestaurantById(restaurantId)
            .onStart { emit(Resource.Loading(isLoading = true)) }
            .map {
                if (it !is Resource.Success) {
                    return@map when (it) {
                        is Resource.Failure -> Resource.Failure(it.error)
                        is Resource.Loading -> Resource.Loading(it.isLoading)
                        else -> throw IllegalStateException()
                    }
                }
                val result = transformedRestaurant(it.result) ?: return@map Resource.Failure(
                    ResourceError.DefaultError("Unable to get latest restaurants data. Please try again later.")
                )
                return@map Resource.Success(result)
            }
            .catch { e ->
                val error = ResourceError.DefaultError(e.message.toString())
                emit(Resource.Failure(error))
            }

    private suspend fun transformedRestaurant(restaurant: Restaurant): TransformedRestaurant? {
        return coroutineScope {
            withContext(Dispatchers.IO) {
                val comments = async {
                    getCommentsUseCase(restaurantId = restaurant.id).last()
                }
                val favoritesOfRestaurant = async {
                    favoriteRepository.getUsersWhoFavoriteRestaurant(
                        restaurant.id
                    )
                }
                return@withContext TransformedRestaurant(
                    id = restaurant.id,
                    name = restaurant.name,
                    description = restaurant.description,
                    imageUrl = restaurant.image.url,
                    branches = restaurant.branches,
                    comments = comments.await().let {
                        if (it !is Resource.Success) return@withContext null
                        it.result
                    },
                    favoriteUsers = favoritesOfRestaurant.await().let {
                        if (it !is Resource.Success) return@withContext null
                        it.result
                    }
                )
            }
        }
    }
}