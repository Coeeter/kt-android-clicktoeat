package com.nasportfolio.domain.comment.usecases

import com.nasportfolio.domain.comment.Comment
import com.nasportfolio.domain.comment.CommentRepository
import com.nasportfolio.domain.utils.Resource
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetCommentsUseCase @Inject constructor(
    private val commentRepository: CommentRepository
) {
    operator fun invoke(restaurantId: String) = flow<Resource<List<Comment>>> {
        emit(Resource.Loading(isLoading = true))
        val commentResource = commentRepository.getCommentsByRestaurant(
            restaurantId = restaurantId
        )
        emit(commentResource)
    }

    fun byUser(userId: String) = flow<Resource<List<Comment>>> {
        emit(Resource.Loading(isLoading = true))
        val commentResource = commentRepository.getCommentsByUser(
            userId = userId
        )
        emit(commentResource)
    }
}