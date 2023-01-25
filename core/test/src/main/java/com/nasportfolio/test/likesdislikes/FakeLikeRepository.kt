package com.nasportfolio.test.likesdislikes

import com.nasportfolio.domain.comment.Comment
import com.nasportfolio.test.comment.FakeCommentRepository
import com.nasportfolio.domain.likesdislikes.like.Like
import com.nasportfolio.domain.likesdislikes.like.LikeRepository
import com.nasportfolio.test.user.FakeUserRepository
import com.nasportfolio.domain.user.User
import com.nasportfolio.domain.utils.Resource

class FakeLikeRepository(
    private val fakeCommentRepository: FakeCommentRepository,
    private val fakeUserRepository: FakeUserRepository
) : LikeRepository {
    var likes: List<Like> = emptyList()

    init {
        likes = likes.toMutableList().apply {
            repeat(10) {
                val like = Like(
                    userId = it.toString(),
                    commentId = it.toString()
                )
                add(like)
            }
        }
    }

    override suspend fun getAllLikes(): Resource<List<Like>> {
        return Resource.Success(likes)
    }

    override suspend fun getLikedCommentsOfUser(userId: String): Resource<List<Comment>> {
        return Resource.Success(
            likes.filter { it.userId == userId }.mapNotNull { like ->
                fakeCommentRepository.comments.find { it.id == like.commentId }
            }
        )
    }

    override suspend fun getUsersWhoLikedComment(commentId: String): Resource<List<User>> {
        return Resource.Success(
            likes.filter { it.commentId == commentId }.mapNotNull { like ->
                fakeUserRepository.users.find { it.id == like.userId }
            }
        )
    }

    override suspend fun createLike(token: String, commentId: String): Resource<String> {
        return when (val account = fakeUserRepository.validateToken(token)) {
            is Resource.Failure -> Resource.Failure(account.error)
            is Resource.Success -> {
                val user = account.result
                likes = likes.toMutableList().apply {
                    add(Like(userId = user.id, commentId = commentId))
                }
                Resource.Success("Created Like!")
            }
            else -> throw IllegalStateException()
        }
    }

    override suspend fun deleteLike(token: String, commentId: String): Resource<String> {
        return when (val account = fakeUserRepository.validateToken(token)) {
            is Resource.Failure -> Resource.Failure(account.error)
            is Resource.Success -> {
                val user = account.result
                likes = likes.filter { it.userId != user.id && it.commentId != commentId }
                Resource.Success("Deleted Like!")
            }
            else -> throw IllegalStateException()
        }
    }
}