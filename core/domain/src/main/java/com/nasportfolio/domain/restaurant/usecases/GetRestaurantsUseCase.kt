package com.nasportfolio.domain.restaurant.usecases

import com.nasportfolio.domain.comment.Comment
import com.nasportfolio.domain.comment.CommentRepository
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
    private val commentRepository: CommentRepository,
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
            .combine(getCommentsUseCase.all()) { restaurantList, comments ->
                if (restaurantList !is Resource.Success) {
                    return@combine when (restaurantList) {
                        is Resource.Failure -> Resource.Failure(restaurantList.error)
                        is Resource.Loading -> Resource.Loading(restaurantList.isLoading)
                        else -> throw IllegalStateException()
                    }
                }
                if (comments !is Resource.Success) {
                    return@combine when (comments) {
                        is Resource.Failure -> Resource.Failure(comments.error)
                        is Resource.Loading -> Resource.Loading(comments.isLoading)
                        else -> throw IllegalStateException()
                    }
                }
                val restaurants = transformedRestaurant(restaurantList.result, comments.result)
                    ?: throw Exception(
                        "Unable to fetch latest restaurant data. Please try again later"
                    )
                val result = restaurants.filter { restaurant ->
                    when (filter) {
                        is Filter.GetUsersFavoriteRestaurants -> {
                            restaurant.favoriteUsers
                                .map { user -> user.id }
                                .contains(filter.userId)
                        }
                        else -> true
                    }
                }
                return@combine Resource.Success(result)
            }
            .catch { e ->
                val error = ResourceError.DefaultError(e.message.toString())
                emit(Resource.Failure(error))
            }

    fun getById(restaurantId: String): Flow<Resource<TransformedRestaurant>> =
        restaurantRepository.getRestaurantById(restaurantId)
            .onStart { emit(Resource.Loading(isLoading = true)) }
            .combineTransform(getCommentsUseCase(restaurantId)) { restaurant, comments ->
                if (restaurant !is Resource.Success) {
                    return@combineTransform when (restaurant) {
                        is Resource.Failure -> emit(Resource.Failure(restaurant.error))
                        is Resource.Loading -> emit(Resource.Loading(restaurant.isLoading))
                        else -> throw IllegalStateException()
                    }
                }
                if (comments !is Resource.Success) {
                    return@combineTransform when (comments) {
                        is Resource.Failure -> emit(Resource.Failure(comments.error))
                        is Resource.Loading -> emit(Resource.Loading(comments.isLoading))
                        else -> throw IllegalStateException()
                    }
                }
                val result = transformedRestaurant(
                    restaurantList = listOf(restaurant.result),
                    comments = comments.result
                ) ?: return@combineTransform emit(
                    Resource.Failure(
                        ResourceError.DefaultError("Unable to get latest restaurants data. Please try again later.")
                    )
                )
                return@combineTransform emit(Resource.Success(result[0]))
            }
            .catch { e ->
                val error = ResourceError.DefaultError(e.message.toString())
                emit(Resource.Failure(error))
            }

    private suspend fun transformedRestaurant(
        restaurantList: List<Restaurant>,
        comments: List<Comment> = emptyList()
    ): List<TransformedRestaurant>? {
        return coroutineScope {
            val favoritesOfRestaurant = restaurantList.map {
                async(Dispatchers.IO) { favoriteRepository.getUsersWhoFavoriteRestaurant(it.id) }
            }.awaitAll()
            return@coroutineScope restaurantList.mapIndexed { i, restaurant ->
                TransformedRestaurant(
                    id = restaurant.id,
                    name = restaurant.name,
                    description = restaurant.description,
                    imageUrl = restaurant.image.url,
                    branches = restaurant.branches,
                    comments = comments.filter { it.restaurant.id == restaurant.id },
                    favoriteUsers = favoritesOfRestaurant[i].let {
                        if (it !is Resource.Success) return@coroutineScope null
                        it.result
                    }
                )
            }
        }
    }
}