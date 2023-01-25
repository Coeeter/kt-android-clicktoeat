package com.nasportfolio.test.likesdislikes

import com.nasportfolio.domain.comment.Comment
import com.nasportfolio.domain.likesdislikes.dislike.Dislike
import com.nasportfolio.domain.likesdislikes.dislike.DislikeRepository
import com.nasportfolio.domain.user.User
import com.nasportfolio.domain.utils.Resource
import com.nasportfolio.test.comment.FakeCommentRepository
import com.nasportfolio.test.user.FakeUserRepository

class FakeDislikeRepository(
    private val fakeCommentRepository: FakeCommentRepository,
    private val fakeUserRepository: FakeUserRepository
) : DislikeRepository {
    var dislikes: List<Dislike> = emptyList()

    init {
        dislikes = dislikes.toMutableList().apply {
            repeat(10) {
                val like = Dislike(
                    userId = it.toString(),
                    commentId = it.toString()
                )
                add(like)
            }
        }
    }

    override suspend fun getAllDislikes(): Resource<List<Dislike>> {
        return Resource.Success(dislikes)
    }

    override suspend fun getDislikedCommentsOfUser(userId: String): Resource<List<Comment>> {
        return Resource.Success(
            dislikes.filter { it.userId == userId }.mapNotNull { like ->
                fakeCommentRepository.comments.find { it.id == like.commentId }
            }
        )
    }

    override suspend fun getUsersWhoDislikedComments(commentId: String): Resource<List<User>> {
        return Resource.Success(
            dislikes.filter { it.commentId == commentId }.mapNotNull { like ->
                fakeUserRepository.users.find { it.id == like.userId }
            }
        )
    }

    override suspend fun createDislike(token: String, commentId: String): Resource<String> {
        return when (val account = fakeUserRepository.validateToken(token)) {
            is Resource.Failure -> Resource.Failure(account.error)
            is Resource.Success -> {
                val user = account.result
                dislikes = dislikes.toMutableList().apply {
                    add(Dislike(userId = user.id, commentId = commentId))
                }
                Resource.Success("Created Dislike!")
            }
            else -> throw IllegalStateException()
        }
    }

    override suspend fun deleteDislike(token: String, commentId: String): Resource<String> {
        return when (val account = fakeUserRepository.validateToken(token)) {
            is Resource.Failure -> Resource.Failure(account.error)
            is Resource.Success -> {
                val user = account.result
                dislikes = dislikes.filter { it.userId != user.id && it.commentId != commentId }
                Resource.Success("Deleted Dislike!")
            }
            else -> throw IllegalStateException()
        }
    }
}