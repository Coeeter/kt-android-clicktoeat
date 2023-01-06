package com.nasportfolio.domain.restaurant.usecases

import com.nasportfolio.domain.comment.Comment
import com.nasportfolio.domain.comment.CommentRepository
import com.nasportfolio.domain.favorites.FavoriteRepository
import com.nasportfolio.domain.restaurant.Restaurant
import com.nasportfolio.domain.restaurant.RestaurantRepository
import com.nasportfolio.domain.restaurant.TransformedRestaurant
import com.nasportfolio.domain.user.usecases.GetCurrentLoggedInUser
import com.nasportfolio.domain.utils.Resource
import com.nasportfolio.domain.utils.ResourceError
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last
import javax.inject.Inject

class GetRestaurantsUseCase @Inject constructor(
    private val getCurrentLoggedInUserUseCase: GetCurrentLoggedInUser,
    private val favoriteRepository: FavoriteRepository,
    private val commentRepository: CommentRepository,
    private val restaurantRepository: RestaurantRepository
) {
    operator fun invoke() = flow<Resource<List<TransformedRestaurant>>> {
        emit(Resource.Loading(isLoading = true))
        val transformedRestaurants = coroutineScope {
            val deferredOtherData = async {
                getOtherData()
            }
            val deferredRestaurants = async {
                restaurantRepository.getAllRestaurants()
            }
            transformMultipleData(
                restaurants = when (val restaurants = deferredRestaurants.await()) {
                    is Resource.Success -> restaurants.result
                    is Resource.Failure -> {
                        emit(Resource.Failure(restaurants.error))
                        return@coroutineScope null
                    }
                    else -> throw IllegalStateException()
                },
                otherData = when (val otherData = deferredOtherData.await()) {
                    is Resource.Success -> otherData.result
                    is Resource.Failure -> {
                        emit(Resource.Failure(otherData.error))
                        return@coroutineScope null
                    }
                    else -> throw IllegalStateException()
                }
            )
        } ?: return@flow
        emit(Resource.Success(transformedRestaurants))
    }

    fun getById(restaurantId: String) = flow<Resource<TransformedRestaurant>> {
        emit(Resource.Loading(isLoading = true))
        val transformedRestaurant = coroutineScope {
            val deferredOtherData = async {
                getOtherData()
            }
            val deferredRestaurant = async {
                restaurantRepository.getRestaurantById(id = restaurantId)
            }
            transformData(
                restaurant = when (val restaurant = deferredRestaurant.await()) {
                    is Resource.Success -> restaurant.result
                    is Resource.Failure -> {
                        emit(Resource.Failure(restaurant.error))
                        return@coroutineScope null
                    }
                    else -> throw IllegalStateException()
                },
                otherData = when (val otherData = deferredOtherData.await()) {
                    is Resource.Success -> otherData.result
                    is Resource.Failure -> {
                        emit(Resource.Failure(otherData.error))
                        return@coroutineScope null
                    }
                    else -> throw IllegalStateException()
                }
            )
        } ?: return@flow
        emit(Resource.Success(transformedRestaurant))
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
        restaurants: List<Restaurant>,
        otherData: OtherData
    ): List<TransformedRestaurant> = restaurants.map {
        transformData(restaurant = it, otherData = otherData)
    }

    private fun transformData(
        restaurant: Restaurant,
        otherData: OtherData
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
            averageRating = getAverageRating(commentsOfRestaurant),
            ratingCount = commentsOfRestaurant.size
        )
    }

    private data class OtherData(
        val comments: List<Comment>,
        val favorites: List<Restaurant>
    )

    private fun getAverageRating(comments: List<Comment>): Double {
        if (comments.isEmpty()) return 0.0
        return comments.sumOf { comment -> comment.rating } / comments.size.toDouble()
    }

    private suspend fun getUserId(): String? {
        val userResource = getCurrentLoggedInUserUseCase().last()
        if (userResource is Resource.Failure) return null
        return (userResource as Resource.Success).result.id
    }
}