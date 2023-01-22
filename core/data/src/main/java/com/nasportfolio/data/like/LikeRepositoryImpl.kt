package com.nasportfolio.data.like

import com.nasportfolio.data.like.remote.RemoteLikeDao
import com.nasportfolio.domain.comment.Comment
import com.nasportfolio.domain.likesdislikes.like.Like
import com.nasportfolio.domain.likesdislikes.like.LikeRepository
import com.nasportfolio.domain.user.User
import com.nasportfolio.domain.utils.Resource
import javax.inject.Inject

class LikeRepositoryImpl @Inject constructor(
    private val remoteLikeDao: RemoteLikeDao
) : LikeRepository {
    override suspend fun getAllLikes(): Resource<List<Like>> {
        return remoteLikeDao.getAllLikes()
    }

    override suspend fun getLikedCommentsOfUser(
        userId: String
    ): Resource<List<Comment>> = remoteLikeDao.getLikedCommentsOfUser(
        userId = userId
    )

    override suspend fun getUsersWhoLikedComment(
        commentId: String
    ): Resource<List<User>> = remoteLikeDao.getUsersWhoLikedComment(
        commentId = commentId
    )

    override suspend fun createLike(
        token: String,
        commentId: String
    ): Resource<String> = remoteLikeDao.createLike(
        token = token,
        commentId = commentId
    )

    override suspend fun deleteLike(
        token: String,
        commentId: String
    ): Resource<String> = remoteLikeDao.deleteLike(
        token = token,
        commentId = commentId
    )
}