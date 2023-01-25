package com.nasportfolio.domain.likesdislikes

import com.nasportfolio.domain.likesdislikes.dislike.DislikeRepository
import com.nasportfolio.domain.likesdislikes.like.LikeRepository
import com.nasportfolio.domain.likesdislikes.usecases.ToggleLikeDislike
import com.nasportfolio.domain.user.usecases.GetCurrentLoggedInUser
import com.nasportfolio.domain.utils.Resource
import com.nasportfolio.test.comment.FakeCommentRepository
import com.nasportfolio.test.likesdislikes.FakeDislikeRepository
import com.nasportfolio.test.likesdislikes.FakeLikeRepository
import com.nasportfolio.test.user.FakeUserRepository
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.*

class ToggleLikeDislikeTest {
    private lateinit var likeRepository: LikeRepository
    private lateinit var dislikeRepository: DislikeRepository
    private lateinit var fakeUserRepository: FakeUserRepository
    private lateinit var fakeCommentRepository: FakeCommentRepository
    private lateinit var toggleLikeDislike: ToggleLikeDislike
    private lateinit var getCurrentLoggedInUser: GetCurrentLoggedInUser

    @Before
    fun setUp() {
        fakeCommentRepository = FakeCommentRepository()
        fakeUserRepository = FakeUserRepository()
        likeRepository = FakeLikeRepository(
            fakeUserRepository = fakeUserRepository,
            fakeCommentRepository = fakeCommentRepository
        )
        dislikeRepository = FakeDislikeRepository(
            fakeUserRepository = fakeUserRepository,
            fakeCommentRepository = fakeCommentRepository
        )
        getCurrentLoggedInUser = GetCurrentLoggedInUser(
            userRepository = fakeUserRepository
        )
        toggleLikeDislike = ToggleLikeDislike(
            likeRepository = likeRepository,
            dislikeRepository = dislikeRepository,
            userRepository = fakeUserRepository,
            getCurrentLoggedInUser = getCurrentLoggedInUser
        )
    }

    @Test
    fun `When liking comment with no likes and dislikes, should return success resource with updated comment`() =
        runBlocking {
            val index = Random().nextInt(fakeCommentRepository.comments.size)
            val user = fakeUserRepository.users.last()
            val comment = fakeCommentRepository.comments[index]
            val result = toggleLikeDislike(
                comment = comment,
                action = ToggleLikeDislike.Action.Like
            ).toList()
            assertTrue(result[0] is Resource.Loading)
            assertTrue(result[1] is Resource.Success)
            assertTrue(result.last() is Resource.Loading)
            val updatedComment = (result[1] as Resource.Success).result
            assertEquals(comment.id, updatedComment.id)
            assertTrue(updatedComment.likes.isNotEmpty())
            assertTrue(updatedComment.likes.map { it.id }.contains(user.id))
        }

    @Test
    fun `When disliking comment with no likes and dislikes, should return success resource with updated comment`() =
        runBlocking {
            val index = Random().nextInt(fakeCommentRepository.comments.size)
            val user = fakeUserRepository.users.last()
            val comment = fakeCommentRepository.comments[index]
            val result = toggleLikeDislike(
                comment = comment,
                action = ToggleLikeDislike.Action.Dislike
            ).toList()
            assertTrue(result[0] is Resource.Loading)
            assertTrue(result[1] is Resource.Success)
            assertTrue(result.last() is Resource.Loading)
            val updatedComment = (result[1] as Resource.Success).result
            assertEquals(comment.id, updatedComment.id)
            assertTrue(updatedComment.dislikes.isNotEmpty())
            assertTrue(updatedComment.dislikes.map { it.id }.contains(user.id))
        }

    @Test
    fun `When liking comment with dislike, should return success resource with updated comment`() =
        runBlocking {
            val index = Random().nextInt(fakeCommentRepository.comments.size)
            val user = fakeUserRepository.users.last()
            fakeCommentRepository.comments = fakeCommentRepository.comments.toMutableList().apply {
                set(index, get(index).copy(dislikes = listOf(user)))
            }
            val comment = fakeCommentRepository.comments[index]
            val result = toggleLikeDislike(
                comment = comment,
                action = ToggleLikeDislike.Action.Like
            ).toList()
            assertTrue(result[0] is Resource.Loading)
            assertTrue(result[1] is Resource.Success)
            assertTrue(result.last() is Resource.Loading)
            val updatedComment = (result[1] as Resource.Success).result
            assertEquals(comment.id, updatedComment.id)
            assertTrue(updatedComment.likes.isNotEmpty())
            assertTrue(updatedComment.likes.map { it.id }.contains(user.id))
        }

    @Test
    fun `When disliking comment with like, should return success resource with updated comment`() =
        runBlocking {
            val index = Random().nextInt(fakeCommentRepository.comments.size)
            val user = fakeUserRepository.users.last()
            fakeCommentRepository.comments = fakeCommentRepository.comments.toMutableList().apply {
                set(index, get(index).copy(likes = listOf(user)))
            }
            val comment = fakeCommentRepository.comments[index]
            val result = toggleLikeDislike(
                comment = comment,
                action = ToggleLikeDislike.Action.Dislike
            ).toList()
            assertTrue(result[0] is Resource.Loading)
            assertTrue(result[1] is Resource.Success)
            assertTrue(result.last() is Resource.Loading)
            val updatedComment = (result[1] as Resource.Success).result
            assertEquals(comment.id, updatedComment.id)
            assertTrue(updatedComment.dislikes.isNotEmpty())
            assertTrue(updatedComment.dislikes.map { it.id }.contains(user.id))
        }

    @Test
    fun `When disliking comment with dislike, should return success resource with updated comment and remove dislike`() =
        runBlocking {
            val index = Random().nextInt(fakeCommentRepository.comments.size)
            val user = fakeUserRepository.users.last()
            fakeCommentRepository.comments = fakeCommentRepository.comments.toMutableList().apply {
                set(index, get(index).copy(dislikes = listOf(user)))
            }
            val comment = fakeCommentRepository.comments[index]
            val result = toggleLikeDislike(
                comment = comment,
                action = ToggleLikeDislike.Action.Dislike
            ).toList()
            assertTrue(result[0] is Resource.Loading)
            assertTrue(result[1] is Resource.Success)
            assertTrue(result.last() is Resource.Loading)
            val updatedComment = (result[1] as Resource.Success).result
            assertEquals(comment.id, updatedComment.id)
            assertTrue(updatedComment.dislikes.isEmpty())
        }

    @Test
    fun `When liking comment with like, should return success resource with updated comment and remove like`() =
        runBlocking {
            val index = Random().nextInt(fakeCommentRepository.comments.size)
            val user = fakeUserRepository.users.last()
            fakeCommentRepository.comments = fakeCommentRepository.comments.toMutableList().apply {
                set(index, get(index).copy(likes = listOf(user)))
            }
            val comment = fakeCommentRepository.comments[index]
            val result = toggleLikeDislike(
                comment = comment,
                action = ToggleLikeDislike.Action.Like
            ).toList()
            assertTrue(result[0] is Resource.Loading)
            assertTrue(result[1] is Resource.Success)
            assertTrue(result.last() is Resource.Loading)
            val updatedComment = (result[1] as Resource.Success).result
            assertEquals(comment.id, updatedComment.id)
            assertTrue(updatedComment.likes.isEmpty())
        }
}