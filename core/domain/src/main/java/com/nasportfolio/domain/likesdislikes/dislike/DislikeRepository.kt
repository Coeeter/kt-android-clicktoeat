package com.nasportfolio.domain.likesdislikes.dislike

import com.nasportfolio.domain.comment.Comment
import com.nasportfolio.domain.user.User
import com.nasportfolio.domain.utils.Resource

interface DislikeRepository {
    suspend fun getAllDislikes(): Resource<List<Dislike>>
    suspend fun getDislikedCommentsOfUser(userId: String): Resource<List<Comment>>
    suspend fun getUsersWhoDislikedComments(commentId: String): Resource<List<User>>
    suspend fun createDislike(token: String, commentId: String): Resource<String>
    suspend fun deleteDislike(token: String, commentId: String): Resource<String>
}