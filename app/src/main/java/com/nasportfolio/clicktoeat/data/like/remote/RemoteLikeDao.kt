package com.nasportfolio.clicktoeat.data.like.remote

import com.nasportfolio.clicktoeat.domain.comment.Comment
import com.nasportfolio.clicktoeat.domain.user.User
import com.nasportfolio.clicktoeat.domain.utils.Resource

interface RemoteLikeDao {
    suspend fun getLikedCommentsOfUser(userId: String): Resource<List<Comment>>
    suspend fun getUsersWhoLikedComment(commentId: String): Resource<List<User>>
    suspend fun createLike(token: String, commentId: String): Resource<String>
    suspend fun deleteLike(token: String, commentId: String): Resource<String>
}