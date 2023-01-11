package com.nasportfolio.domain.comment.usecases

import com.nasportfolio.domain.comment.Comment
import com.nasportfolio.domain.comment.CommentRepository
import com.nasportfolio.domain.user.UserRepository
import com.nasportfolio.domain.utils.Resource
import com.nasportfolio.domain.utils.ResourceError
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class EditCommentUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val commentRepository: CommentRepository
) {
    operator fun invoke(
        commentId: String,
        review: String,
        rating: Int
    ) = flow<Resource<Comment>> {
        emit(Resource.Loading(isLoading = true))
        val tokenResource = userRepository.getToken()
        if (tokenResource !is Resource.Success) {
            when (tokenResource) {
                is Resource.Failure -> emit(Resource.Failure(tokenResource.error))
                else -> Unit
            }
            return@flow
        }
        validate(
            review = review,
            rating = rating
        )?.let { return@flow emit(Resource.Failure(it)) }
        val commentResource = commentRepository.updateComment(
            token = tokenResource.result,
            commentId = commentId,
            review = review,
            rating = rating
        )
        emit(commentResource)
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