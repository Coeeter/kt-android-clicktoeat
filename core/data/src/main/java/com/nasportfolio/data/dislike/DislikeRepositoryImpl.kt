package com.nasportfolio.data.dislike

import com.nasportfolio.data.dislike.remote.RemoteDislikeDao
import com.nasportfolio.domain.comment.Comment
import com.nasportfolio.domain.dislike.DislikeRepository
import com.nasportfolio.domain.user.User
import com.nasportfolio.domain.utils.Resource
import javax.inject.Inject

class DislikeRepositoryImpl @Inject constructor(
    private val remoteDislikeDao: RemoteDislikeDao
) : DislikeRepository {
    override suspend fun getDislikedCommentsOfUser(
        userId: String
    ): Resource<List<Comment>> = remoteDislikeDao.getDislikedCommentsOfUser(
        userId = userId
    )

    override suspend fun getUsersWhoDislikedComments(
        commentId: String
    ): Resource<List<User>> = remoteDislikeDao.getUsersWhoDislikedComments(
        commentId = commentId
    )

    override suspend fun createDislike(
        token: String,
        commentId: String
    ): Resource<String> = remoteDislikeDao.createDislike(
        token = token,
        commentId = commentId
    )

    override suspend fun deleteDislike(
        token: String,
        commentId: String
    ): Resource<String> = remoteDislikeDao.deleteDislike(
        token = token,
        commentId = commentId
    )
}