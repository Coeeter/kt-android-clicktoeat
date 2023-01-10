package com.nasportfolio.domain.comment

import com.nasportfolio.domain.utils.Resource

interface CommentRepository {

    suspend fun getAllComments(): Resource<List<Comment>>

    suspend fun getCommentById(id: String): Resource<Comment>

    suspend fun getCommentsByUser(userId: String): Resource<List<Comment>>

    suspend fun getCommentsByRestaurant(restaurantId: String): Resource<List<Comment>>

    suspend fun createComment(
        token: String,
        restaurantId: String,
        review: String,
        rating: Int,
        parentComment: String? = null
    ): Resource<String>

    suspend fun updateComment(
        token: String,
        commentId: String,
        review: String? = null,
        rating: Int? = null
    ): Resource<Comment>

    suspend fun deleteComment(
        token: String,
        commentId: String
    ): Resource<String>

}