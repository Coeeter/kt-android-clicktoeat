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
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class EditCommentUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val commentRepository: CommentRepository,
    private val likeRepository: LikeRepository,
    private val dislikeRepository: DislikeRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(
        commentId: String,
        review: String,
        rating: Int
    ) = flow<Resource<Comment>> {
        validate(
            review = review,
            rating = rating
        )?.let { return@flow emit(Resource.Failure(it)) }
        emit(Resource.Loading(isLoading = true))
        val tokenResource = userRepository.getToken()
        if (tokenResource !is Resource.Success) {
            when (tokenResource) {
                is Resource.Failure -> emit(Resource.Failure(tokenResource.error))
                else -> Unit
            }
            return@flow
        }
        val commentResource = commentRepository.updateComment(
            token = tokenResource.result,
            commentId = commentId,
            review = review,
            rating = rating
        )
        if (commentResource !is Resource.Success) return@flow emit(commentResource)
        val comment = coroutineScope {
            val deferredLikes = async {
                likeRepository.getUsersWhoLikedComment(
                    commentId = commentResource.result.id
                )
            }
            val deferredDislikes = async {
                dislikeRepository.getUsersWhoDislikedComments(
                    commentId = commentResource.result.id
                )
            }
            val likes = deferredLikes.await()
            val dislikes = deferredDislikes.await()
            if (likes !is Resource.Success) return@coroutineScope Resource.Failure(
                (likes as Resource.Failure).error
            )
            if (dislikes !is Resource.Success) return@coroutineScope Resource.Failure(
                (dislikes as Resource.Failure).error
            )
            Resource.Success(
                result = Comment(
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
            )
        }
        emit(comment)
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