package com.nasportfolio.domain.likesdislikes.like

import com.nasportfolio.domain.comment.Comment
import com.nasportfolio.domain.user.User
import com.nasportfolio.domain.utils.Resource

interface LikeRepository {
    suspend fun getAllLikes(): Resource<List<Like>>
    suspend fun getLikedCommentsOfUser(userId: String): Resource<List<Comment>>
    suspend fun getUsersWhoLikedComment(commentId: String): Resource<List<User>>
    suspend fun createLike(token: String, commentId: String): Resource<String>
    suspend fun deleteLike(token: String, commentId: String): Resource<String>
}