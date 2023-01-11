package com.nasportfolio.domain.comment.usecases

import com.nasportfolio.domain.comment.CommentRepository
import com.nasportfolio.domain.user.UserRepository
import com.nasportfolio.domain.utils.Resource
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DeleteCommentUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val commentRepository: CommentRepository
) {
    operator fun invoke(commentId: String) = flow<Resource<Unit>> {
        emit(Resource.Loading(isLoading = true))
        val tokenResource = userRepository.getToken()
        if (tokenResource !is Resource.Success) {
            when (tokenResource) {
                is Resource.Failure -> {
                    emit(Resource.Failure(tokenResource.error))
                    return@flow
                }
                else -> throw IllegalStateException()
            }
        }
        val deleteResource = commentRepository.deleteComment(
            token = tokenResource.result,
            commentId = commentId
        )
        when(deleteResource) {
            is Resource.Success -> emit(Resource.Success(Unit))
            is Resource.Failure -> emit(Resource.Failure(deleteResource.error))
            else -> throw IllegalStateException()
        }
    }
}