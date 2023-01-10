package com.nasportfolio.data.comment.remote

import com.nasportfolio.data.comment.remote.dtos.CreateCommentDto
import com.nasportfolio.data.comment.remote.dtos.UpdateCommentDto
import com.nasportfolio.domain.comment.Comment
import com.nasportfolio.domain.utils.Resource

interface RemoteCommentDao {

    suspend fun getAllComments(): Resource<List<Comment>>

    suspend fun getCommentById(id: String): Resource<Comment>

    suspend fun getCommentsByUser(userId: String): Resource<List<Comment>>

    suspend fun getCommentsByRestaurant(restaurantId: String): Resource<List<Comment>>

    suspend fun createComment(
        token: String,
        restaurantId: String,
        createCommentDto: CreateCommentDto
    ): Resource<String>

    suspend fun updateComment(
        token: String,
        commentId: String,
        updateCommentDto: UpdateCommentDto
    ): Resource<Comment>

    suspend fun deleteComment(token: String, commentId: String): Resource<String>

}
