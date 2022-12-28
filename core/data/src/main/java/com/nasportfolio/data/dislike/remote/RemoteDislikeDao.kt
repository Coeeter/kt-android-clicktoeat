package com.nasportfolio.data.dislike.remote

import com.nasportfolio.domain.comment.Comment
import com.nasportfolio.domain.user.User
import com.nasportfolio.domain.utils.Resource

interface RemoteDislikeDao {
    suspend fun getDislikedCommentsOfUser(userId: String): Resource<List<Comment>>
    suspend fun getUsersWhoDislikedComments(commentId: String): Resource<List<User>>
    suspend fun createDislike(token: String, commentId: String): Resource<String>
    suspend fun deleteDislike(token: String, commentId: String): Resource<String>
}