package com.nasportfolio.domain.restaurant.usecases

import com.nasportfolio.domain.comment.Comment
import com.nasportfolio.domain.comment.CommentRepository
import com.nasportfolio.domain.favorites.FavoriteRepository
import com.nasportfolio.domain.restaurant.Restaurant
import com.nasportfolio.domain.restaurant.RestaurantRepository
import com.nasportfolio.domain.restaurant.TransformedRestaurant
import com.nasportfolio.domain.user.User
import com.nasportfolio.domain.user.usecases.GetCurrentLoggedInUser
import com.nasportfolio.domain.utils.Resource
import com.nasportfolio.domain.utils.ResourceError
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.last
import javax.inject.Inject

class GetRestaurantsUseCase @Inject constructor(
    private val getCurrentLoggedInUserUseCase: GetCurrentLoggedInUser,
    private val favoriteRepository: FavoriteRepository,
    private val commentRepository: CommentRepository,
    private val restaurantRepository: RestaurantRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke() = channelFlow<Resource<List<TransformedRestaurant>>> {
        send(Resource.Loading(isLoading = true))
        val transformedRestaurants = coroutineScope {
            val deferredOtherData = async {
                getOtherData()
            }
            val deferredRestaurants = async {
                val restaurants = restaurantRepository.getAllRestaurants()
                if (restaurants !is Resource.Success) {
                    send(Resource.Failure((restaurants as Resource.Failure).error))
                    return@async null
                }
                val deferredFavorites = restaurants.result.map {
                    async {
                        favoriteRepository.getUsersWhoFavoriteRestaurant(it.id)
                    }
                }
                val favorites = deferredFavorites.awaitAll().map {
                    if (it !is Resource.Success) {
                        send(Resource.Failure((it as Resource.Failure).error))
                        return@async null
                    }
                    it.result
                }
                RestaurantsWithFav(
                    restaurants = restaurants.result,
                    favoriteUsers = favorites
                )
            }
            transformMultipleData(
                otherData = when (val otherData = deferredOtherData.await()) {
                    is Resource.Success -> otherData.result
                    is Resource.Failure -> {
                        send(Resource.Failure(otherData.error))
                        return@coroutineScope null
                    }
                    else -> throw IllegalStateException()
                },
                restaurantsWithFav = deferredRestaurants.await() ?: return@coroutineScope null
            )
        } ?: return@channelFlow
        send(Resource.Success(transformedRestaurants))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getById(restaurantId: String) = channelFlow<Resource<TransformedRestaurant>> {
        send(Resource.Loading(isLoading = true))
        val transformedRestaurant = coroutineScope {
            val deferredOtherData = async {
                getOtherData()
            }
            val deferredRestaurant = async {
                restaurantRepository.getRestaurantById(id = restaurantId)
            }
            val deferredFavorites = async {
                favoriteRepository.getUsersWhoFavoriteRestaurant(restaurantId = restaurantId)
            }
            transformData(
                restaurant = when (val restaurant = deferredRestaurant.await()) {
                    is Resource.Success -> restaurant.result
                    is Resource.Failure -> {
                        send(Resource.Failure(restaurant.error))
                        return@coroutineScope null
                    }
                    else -> throw IllegalStateException()
                },
                otherData = when (val otherData = deferredOtherData.await()) {
                    is Resource.Success -> otherData.result
                    is Resource.Failure -> {
                        send(Resource.Failure(otherData.error))
                        return@coroutineScope null
                    }
                    else -> throw IllegalStateException()
                },
                usersWhoFavorite = when (val favorites = deferredFavorites.await()) {
                    is Resource.Success -> favorites.result
                    is Resource.Failure -> {
                        send(Resource.Failure(favorites.error))
                        return@coroutineScope null
                    }
                    else -> throw IllegalStateException()
                },
            )
        } ?: return@channelFlow
        send(Resource.Success(transformedRestaurant))
    }

    private suspend fun getOtherData(): Resource<OtherData> = coroutineScope {
        val deferredFavRestaurants = async {
            val userId = getUserId() ?: return@async Resource.Failure<List<Restaurant>>(
                ResourceError.DefaultError("Must be logged in to do this action")
            )
            favoriteRepository.getFavoriteRestaurantsOfUser(
                userId = userId
            )
        }
        val deferredComments = async {
            commentRepository.getAllComments()
        }
        return@coroutineScope Resource.Success(
            OtherData(
                comments = when (val comments = deferredComments.await()) {
                    is Resource.Success -> comments.result
                    is Resource.Failure -> return@coroutineScope Resource.Failure(
                        comments.error
                    )
                    else -> throw IllegalStateException()
                },
                favorites = when (val favorites = deferredFavRestaurants.await()) {
                    is Resource.Success -> favorites.result
                    is Resource.Failure -> return@coroutineScope Resource.Failure(
                        favorites.error
                    )
                    else -> throw IllegalStateException()
                }
            )
        )
    }

    private fun transformMultipleData(
        restaurantsWithFav: RestaurantsWithFav,
        otherData: OtherData
    ): List<TransformedRestaurant> =
        restaurantsWithFav.restaurants.mapIndexed { index, restaurant ->
            transformData(
                restaurant = restaurant,
                otherData = otherData,
                usersWhoFavorite = restaurantsWithFav.favoriteUsers[index]
            )
        }

    private fun transformData(
        restaurant: Restaurant,
        otherData: OtherData,
        usersWhoFavorite: List<User>
    ): TransformedRestaurant {
        val commentsOfRestaurant = otherData.comments.filter { comment ->
            comment.restaurant.id == restaurant.id
        }
        val isFavorited = otherData.favorites.map { it.id }.contains(restaurant.id)
        return TransformedRestaurant(
            id = restaurant.id,
            name = restaurant.name,
            description = restaurant.description,
            imageUrl = restaurant.image.url,
            branches = restaurant.branches,
            comments = commentsOfRestaurant,
            isFavoriteByCurrentUser = isFavorited,
            favoriteSize = usersWhoFavorite.size
        )
    }

    private data class RestaurantsWithFav(
        val restaurants: List<Restaurant>,
        val favoriteUsers: List<List<User>>
    )

    private data class OtherData(
        val comments: List<Comment>,
        val favorites: List<Restaurant>
    )

    private suspend fun getUserId(): String? {
        val userResource = getCurrentLoggedInUserUseCase().last()
        if (userResource is Resource.Failure) return null
        return (userResource as Resource.Success).result.id
    }
}