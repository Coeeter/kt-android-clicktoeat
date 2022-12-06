package com.nasportfolio.clicktoeat.data.comment.remote

import com.google.gson.Gson
import com.nasportfolio.clicktoeat.data.comment.remote.dtos.CreateCommentDto
import com.nasportfolio.clicktoeat.data.comment.remote.dtos.UpdateCommentDto
import com.nasportfolio.clicktoeat.data.common.OkHttpDao
import com.nasportfolio.clicktoeat.domain.comment.Comment
import com.nasportfolio.clicktoeat.domain.utils.Resource
import okhttp3.OkHttpClient

abstract class RemoteCommentDao(
    okHttpClient: OkHttpClient,
    gson: Gson
) : OkHttpDao(okHttpClient, gson, "/api/comments") {
    abstract suspend fun getAllComments(): Resource<List<Comment>>
    abstract suspend fun getCommentsByUser(userId: String): Resource<List<Comment>>
    abstract suspend fun getCommentsByRestaurant(restaurantId: String): Resource<List<Comment>>

    abstract suspend fun createComment(
        token: String,
        restaurantId: String,
        createCommentDto: CreateCommentDto
    ): Resource<String>

    abstract suspend fun updateComment(
        token: String,
        commentId: String,
        updateCommentDto: UpdateCommentDto
    ): Resource<Comment>

    abstract suspend fun deleteComment(token: String, commentId: String): Resource<String>
}
