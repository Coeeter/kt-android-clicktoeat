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
        val otherData = getOtherData()
        if (otherData !is Resource.Success) return@flow emit(
            Resource.Failure((otherData as Resource.Failure).error)
        )
        val restaurants = restaurantRepository.getAllRestaurants()
        if (restaurants !is Resource.Success) return@flow emit(
            Resource.Failure((restaurants as Resource.Failure).error)
        )
        val transformedRestaurants = restaurants.result.map { restaurant ->
            transformData(
                restaurant = restaurant,
                favoritesOfUser = otherData.result.favorites,
                comments = otherData.result.comments
            )
        }
        emit(Resource.Success(transformedRestaurants))
    }

    fun getById(restaurantId: String) = flow<Resource<TransformedRestaurant>> {
        emit(Resource.Loading(isLoading = true))
        val otherData = getOtherData()
        if (otherData !is Resource.Success) return@flow emit(
            Resource.Failure((otherData as Resource.Failure).error)
        )
        val restaurant = restaurantRepository.getRestaurantById(id = restaurantId)
        if (restaurant !is Resource.Success) return@flow emit(
            Resource.Failure((restaurant as Resource.Failure).error)
        )
        emit(
            Resource.Success(
                transformData(
                    restaurant = restaurant.result,
                    comments = otherData.result.comments,
                    favoritesOfUser = otherData.result.favorites
                )
            )
        )
    }

    private suspend fun getOtherData(): Resource<OtherData> {
        val userId = getUserId() ?: return Resource.Failure(
            ResourceError.DefaultError("Must be logged in to do this action")
        )
        val favoriteRestaurants = favoriteRepository.getFavoriteRestaurantsOfUser(
            userId = userId
        )
        if (favoriteRestaurants !is Resource.Success) return Resource.Failure(
            (favoriteRestaurants as Resource.Failure).error
        )
        val comments = commentRepository.getAllComments()
        if (comments !is Resource.Success) return Resource.Failure(
            (comments as Resource.Failure).error
        )
        return Resource.Success(
            OtherData(
                comments = comments.result,
                favorites = favoriteRestaurants.result
            )
        )
    }

    private fun transformData(
        restaurant: Restaurant,
        favoritesOfUser: List<Restaurant>,
        comments: List<Comment>,
    ): TransformedRestaurant {
        val commentsOfRestaurant = comments.filter { comment ->
            comment.restaurant.id == restaurant.id
        }
        val isFavorited = favoritesOfUser.map { it.id }.contains(restaurant.id)
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