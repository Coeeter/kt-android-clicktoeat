package com.nasportfolio.domain.restaurant.usecases

import com.nasportfolio.domain.comment.Comment
import com.nasportfolio.domain.comment.CommentRepository
import com.nasportfolio.domain.favorites.FavoriteRepository
import com.nasportfolio.domain.restaurant.RestaurantRepository
import com.nasportfolio.domain.restaurant.TransformedRestaurant
import com.nasportfolio.domain.user.usecases.GetUserFromTokenUseCase
import com.nasportfolio.domain.utils.Resource
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetAllRestaurantsUseCase @Inject constructor(
    private val getUserFromTokenUseCase: GetUserFromTokenUseCase,
    private val favoriteRepository: FavoriteRepository,
    private val commentRepository: CommentRepository,
    private val restaurantRepository: RestaurantRepository
) {
    operator fun invoke() = flow<Resource<List<TransformedRestaurant>>> {
        emit(Resource.Loading(isLoading = true))
        getUserFromTokenUseCase().collect { userResource ->
            if (userResource is Resource.Loading) return@collect emit(
                Resource.Loading(isLoading = true)
            )
            if (userResource is Resource.Failure) return@collect emit(
                Resource.Failure(userResource.error)
            )
            val userId = (userResource as Resource.Success).result.id
            val favoriteRestaurants = favoriteRepository.getFavoriteRestaurantsOfUser(
                userId = userId
            )
            if (favoriteRestaurants !is Resource.Success) return@collect emit(
                Resource.Failure((favoriteRestaurants as Resource.Failure).error)
            )
            val restaurants = restaurantRepository.getAllRestaurants()
            if (restaurants !is Resource.Success) return@collect emit(
                Resource.Failure((restaurants as Resource.Failure).error)
            )
            val comments = commentRepository.getAllComments()
            if (comments !is Resource.Success) return@collect emit(
                Resource.Failure((comments as Resource.Failure).error)
            )
            val transformedRestaurants = restaurants.result.map {
                val commentsOfRestaurant = comments.result.filter { comment ->
                    comment.restaurant.id == it.id
                }
                val isFavorited = favoriteRestaurants.result.map { restaurant ->
                    restaurant.id
                }.contains(it.id)
                TransformedRestaurant(
                    id = it.id,
                    name = it.name,
                    description = it.description,
                    imageUrl = it.image.url,
                    branches = it.branches,
                    comments = commentsOfRestaurant,
                    isFavoriteByCurrentUser = isFavorited,
                    averageRating = getAverageRating(commentsOfRestaurant),
                    ratingCount = commentsOfRestaurant.size
                )
            }
            emit(Resource.Success(transformedRestaurants))
        }
    }

    private fun getAverageRating(comments: List<Comment>): Double {
        if (comments.isEmpty()) return 0.0
        return comments.sumOf { comment -> comment.rating } / comments.size.toDouble()
    }
}