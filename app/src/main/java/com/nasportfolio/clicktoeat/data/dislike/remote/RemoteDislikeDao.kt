package com.nasportfolio.clicktoeat.data.dislike.remote

import com.nasportfolio.clicktoeat.domain.comment.Comment
import com.nasportfolio.clicktoeat.domain.user.User
import com.nasportfolio.clicktoeat.domain.utils.Resource

interface RemoteDislikeDao {
    suspend fun getDislikedCommentsOfUser(userId: String): Resource<List<Comment>>
    suspend fun getUsersWhoDislikedComments(commentId: String): Resource<List<User>>
    suspend fun createDislike(token: String, commentId: String): Resource<String>
    suspend fun deleteDislike(token: String, commentId: String): Resource<String>
}