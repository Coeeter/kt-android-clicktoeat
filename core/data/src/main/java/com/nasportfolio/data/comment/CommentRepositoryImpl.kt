package com.nasportfolio.data.comment

import com.nasportfolio.data.comment.remote.RemoteCommentDao
import com.nasportfolio.data.comment.remote.dtos.CreateCommentDto
import com.nasportfolio.data.comment.remote.dtos.UpdateCommentDto
import com.nasportfolio.domain.comment.Comment
import com.nasportfolio.domain.comment.CommentRepository
import com.nasportfolio.domain.utils.Resource
import javax.inject.Inject

class CommentRepositoryImpl @Inject constructor(
    private val remoteCommentDao: RemoteCommentDao
) : CommentRepository {

    override suspend fun getAllComments(): Resource<List<Comment>> =
        remoteCommentDao.getAllComments()

    override suspend fun getCommentById(id: String): Resource<Comment> =
        remoteCommentDao.getCommentById(id = id)

    override suspend fun getCommentsByUser(userId: String): Resource<List<Comment>> =
        remoteCommentDao.getCommentsByUser(userId = userId)

    override suspend fun getCommentsByRestaurant(restaurantId: String): Resource<List<Comment>> =
        remoteCommentDao.getCommentsByRestaurant(restaurantId = restaurantId)

    override suspend fun createComment(
        token: String,
        restaurantId: String,
        review: String,
        rating: Int,
        parentComment: String?
    ): Resource<String> = remoteCommentDao.createComment(
        token = token,
        restaurantId = restaurantId,
        createCommentDto = CreateCommentDto(
            review = review,
            rating = rating,
            parentComment = parentComment
        )
    )

    override suspend fun updateComment(
        token: String,
        commentId: String,
        review: String?,
        rating: Int?
    ): Resource<Comment> = remoteCommentDao.updateComment(
        token = token,
        commentId = commentId,
        updateCommentDto = UpdateCommentDto(
            review = review,
            rating = rating,
        )
    )

    override suspend fun deleteComment(
        token: String,
        commentId: String
    ): Resource<String> = remoteCommentDao.deleteComment(
        token = token,
        commentId = commentId
    )

}