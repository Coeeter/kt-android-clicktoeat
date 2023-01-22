package com.nasportfolio.domain.comment.usecases

import com.nasportfolio.domain.comment.Comment
import com.nasportfolio.domain.comment.CommentRepository
import com.nasportfolio.domain.likesdislikes.dislike.DislikeRepository
import com.nasportfolio.domain.likesdislikes.like.LikeRepository
import com.nasportfolio.domain.user.UserRepository
import com.nasportfolio.domain.utils.Resource
import com.nasportfolio.domain.utils.ResourceError
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class CreateCommentUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val commentRepository: CommentRepository,
    private val likeRepository: LikeRepository,
    private val dislikeRepository: DislikeRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(
        restaurantId: String,
        review: String,
        rating: Int
    ) = channelFlow<Resource<Comment>> {
        send(Resource.Loading(isLoading = true))
        val tokenResource = userRepository.getToken()
        if (tokenResource !is Resource.Success) {
            when (tokenResource) {
                is Resource.Failure -> send(Resource.Failure(tokenResource.error))
                else -> Unit
            }
            return@channelFlow
        }
        validate(
            review = review,
            rating = rating
        )?.let { return@channelFlow send(Resource.Failure(it)) }
        val insertIdResource = commentRepository.createComment(
            token = tokenResource.result,
            restaurantId = restaurantId,
            review = review,
            rating = rating
        )
        when (insertIdResource) {
            is Resource.Failure -> send(
                Resource.Failure(error = insertIdResource.error)
            )
            is Resource.Success -> coroutineScope {
                val deferredComment = async {
                    commentRepository.getCommentById(
                        id = insertIdResource.result
                    )
                }
                val deferredLikes = async {
                    likeRepository.getUsersWhoLikedComment(
                        commentId = insertIdResource.result
                    )
                }
                val deferredDislikes = async {
                    dislikeRepository.getUsersWhoDislikedComments(
                        commentId = insertIdResource.result
                    )
                }
                val commentResource = deferredComment.await()
                val likes = deferredLikes.await()
                val dislikes = deferredDislikes.await()
                if (commentResource !is Resource.Success) return@coroutineScope send(commentResource)
                if (likes !is Resource.Success) return@coroutineScope send(
                    Resource.Failure((likes as Resource.Failure).error)
                )
                if (dislikes !is Resource.Success) return@coroutineScope send(
                    Resource.Failure((dislikes as Resource.Failure).error)
                )
                val comment = Comment(
                    id = commentResource.result.id,
                    review = commentResource.result.review,
                    rating = commentResource.result.rating,
                    parentComment = commentResource.result.parentComment,
                    updatedAt = commentResource.result.updatedAt,
                    createdAt = commentResource.result.createdAt,
                    user = commentResource.result.user,
                    restaurant = commentResource.result.restaurant,
                    dislikes = dislikes.result,
                    likes = likes.result
                )
                send(Resource.Success(comment))
            }
            else -> Unit
        }
    }

    private fun validate(
        review: String,
        rating: Int,
    ): ResourceError.FieldError? {
        var error = ResourceError.FieldError(
            message = "Errors in fields provided",
            errors = emptyList()
        )
        if (review.isEmpty()) error = error.copy(
            errors = error.errors.toMutableList().apply {
                add(
                    ResourceError.FieldErrorItem(
                        field = "review",
                        error = "Review required!"
                    )
                )
            }
        )
        if (rating == 0) error = error.copy(
            errors = error.errors.toMutableList().apply {
                add(
                    ResourceError.FieldErrorItem(
                        field = "rating",
                        error = "Rating required!"
                    )
                )
            }
        )
        if (error.errors.isEmpty()) return null
        return error
    }
}