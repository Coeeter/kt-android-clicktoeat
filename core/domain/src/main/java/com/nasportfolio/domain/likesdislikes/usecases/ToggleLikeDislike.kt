package com.nasportfolio.domain.likesdislikes.usecases

import com.nasportfolio.domain.comment.Comment
import com.nasportfolio.domain.likesdislikes.dislike.DislikeRepository
import com.nasportfolio.domain.likesdislikes.like.LikeRepository
import com.nasportfolio.domain.user.User
import com.nasportfolio.domain.user.UserRepository
import com.nasportfolio.domain.user.usecases.GetCurrentLoggedInUser
import com.nasportfolio.domain.utils.Resource
import com.nasportfolio.domain.utils.ResourceError
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last
import javax.inject.Inject

class ToggleLikeDislike @Inject constructor(
    private val getCurrentLoggedInUser: GetCurrentLoggedInUser,
    private val userRepository: UserRepository,
    private val likeRepository: LikeRepository,
    private val dislikeRepository: DislikeRepository
) {
    operator fun invoke(comment: Comment, action: Action) = flow<Resource<Comment>> {
        emit(Resource.Loading(isLoading = true))
        val notLoggedInError = Resource.Failure<Comment>(
            ResourceError.DefaultError(
                error = "Must be logged in to do this action"
            )
        )
        val user = getCurrentUser() ?: return@flow emit(notLoggedInError)
        val tokenResource = userRepository.getToken()
        when (tokenResource) {
            is Resource.Failure -> return@flow emit(notLoggedInError)
            is Resource.Loading -> throw IllegalStateException()
            else -> Unit
        }
        val token = (tokenResource as Resource.Success).result
        when (action) {
            Action.Like -> {
                val index = comment.likes.map { it.id }.indexOf(user.id)
                val updatedComment = comment.copy(
                    dislikes = comment.dislikes.filter { it.id != user.id },
                    likes = comment.likes.toMutableList().apply {
                        if (index != -1) return@apply run {
                            removeAt(index)
                        }
                        add(user)
                    }
                )
                emit(Resource.Success(updatedComment))
                val result = if (index != -1) {
                    likeRepository.deleteLike(
                        token = token,
                        commentId = comment.id
                    )
                } else {
                    likeRepository.createLike(
                        token = token,
                        commentId = comment.id
                    )
                }
                when (result) {
                    is Resource.Failure -> {
                        emit(Resource.Failure(result.error))
                        emit(Resource.Success(comment))
                    }
                    else -> Unit
                }
            }
            Action.Dislike -> {
                val index = comment.dislikes.map { it.id }.indexOf(user.id)
                val updatedComment = comment.copy(
                    likes = comment.likes.filter { it.id != user.id },
                    dislikes = comment.dislikes.toMutableList().apply {
                        if (index != -1) return@apply run {
                            removeAt(index)
                        }
                        add(user)
                    }
                )
                emit(Resource.Success(updatedComment))
                val result = if (index != -1) {
                    dislikeRepository.deleteDislike(
                        token = token,
                        commentId = comment.id
                    )
                } else {
                    dislikeRepository.createDislike(
                        token = token,
                        commentId = comment.id
                    )
                }
                when (result) {
                    is Resource.Failure -> {
                        emit(Resource.Failure(result.error))
                        emit(Resource.Success(comment))
                    }
                    else -> Unit
                }
            }
        }
        emit(Resource.Loading(isLoading = false))
    }

    private suspend fun getCurrentUser(): User? {
        return when (val user = getCurrentLoggedInUser().last()) {
            is Resource.Success -> user.result
            else -> null
        }
    }

    enum class Action {
        Like,
        Dislike
    }
}