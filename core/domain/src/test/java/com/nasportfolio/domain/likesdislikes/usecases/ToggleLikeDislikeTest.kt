package com.nasportfolio.domain.likesdislikes.usecases

import com.nasportfolio.domain.comment.Comment
import com.nasportfolio.domain.likesdislikes.dislike.DislikeRepository
import com.nasportfolio.domain.likesdislikes.like.LikeRepository
import com.nasportfolio.domain.restaurant.Restaurant
import com.nasportfolio.domain.user.FakeUserRepository
import com.nasportfolio.domain.user.User
import com.nasportfolio.domain.user.usecases.GetCurrentLoggedInUser
import com.nasportfolio.domain.utils.Resource
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever
import java.util.*

class ToggleLikeDislikeTest {

    lateinit var toggleLikeDislike: ToggleLikeDislike
    private lateinit var closeable: AutoCloseable

    @Mock
    lateinit var likeRepository: LikeRepository

    @Mock
    lateinit var dislikeRepository: DislikeRepository

    @Mock
    lateinit var getCurrentLoggedInUser: GetCurrentLoggedInUser

    @Mock
    lateinit var user: User

    @Mock
    lateinit var restaurant: Restaurant

    @Before
    fun setUp() {
        closeable = MockitoAnnotations.openMocks(this)
        toggleLikeDislike = ToggleLikeDislike(
            likeRepository = likeRepository,
            dislikeRepository = dislikeRepository,
            userRepository = FakeUserRepository(),
            getCurrentLoggedInUser = getCurrentLoggedInUser
        )
    }

    @After
    fun tearDown() {
        closeable.close()
    }

    @Test
    fun `When liking comment with no likes and dislikes, should return success resource with updated comment`() =
        runBlocking {
            whenever(user.id).thenReturn("id")
            whenever(getCurrentLoggedInUser()).thenReturn(flowOf(Resource.Success(user)))
            val comment = Comment(
                id = "0",
                review = "Test",
                rating = 5,
                createdAt = Date(),
                updatedAt = Date(),
                likes = emptyList(),
                dislikes = emptyList(),
                user = user,
                restaurant = restaurant
            )
            whenever(
                likeRepository.createLike(
                    token = anyString(),
                    commentId = anyString()
                )
            ).thenReturn(
                Resource.Success("")
            )
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
            whenever(user.id).thenReturn("id")
            whenever(getCurrentLoggedInUser()).thenReturn(flowOf(Resource.Success(user)))
            val comment = Comment(
                id = "0",
                review = "Test",
                rating = 5,
                createdAt = Date(),
                updatedAt = Date(),
                likes = emptyList(),
                dislikes = emptyList(),
                user = user,
                restaurant = restaurant
            )
            whenever(
                dislikeRepository.createDislike(
                    token = anyString(),
                    commentId = anyString()
                )
            ).thenReturn(
                Resource.Success("")
            )
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
            whenever(user.id).thenReturn("id")
            whenever(getCurrentLoggedInUser()).thenReturn(flowOf(Resource.Success(user)))
            val comment = Comment(
                id = "0",
                review = "Test",
                rating = 5,
                createdAt = Date(),
                updatedAt = Date(),
                likes = emptyList(),
                dislikes = listOf(user),
                user = user,
                restaurant = restaurant
            )
            whenever(
                likeRepository.createLike(
                    token = anyString(),
                    commentId = anyString()
                )
            ).thenReturn(
                Resource.Success("")
            )
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
            whenever(user.id).thenReturn("id")
            whenever(getCurrentLoggedInUser()).thenReturn(flowOf(Resource.Success(user)))
            val comment = Comment(
                id = "0",
                review = "Test",
                rating = 5,
                createdAt = Date(),
                updatedAt = Date(),
                likes = listOf(user),
                dislikes = emptyList(),
                user = user,
                restaurant = restaurant
            )
            whenever(
                dislikeRepository.createDislike(
                    token = anyString(),
                    commentId = anyString()
                )
            ).thenReturn(
                Resource.Success("")
            )
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
            whenever(user.id).thenReturn("id")
            whenever(getCurrentLoggedInUser()).thenReturn(flowOf(Resource.Success(user)))
            val comment = Comment(
                id = "0",
                review = "Test",
                rating = 5,
                createdAt = Date(),
                updatedAt = Date(),
                likes = emptyList(),
                dislikes = listOf(user),
                user = user,
                restaurant = restaurant
            )
            whenever(
                dislikeRepository.deleteDislike(
                    token = anyString(),
                    commentId = anyString()
                )
            ).thenReturn(
                Resource.Success("")
            )
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
            whenever(user.id).thenReturn("id")
            whenever(getCurrentLoggedInUser()).thenReturn(flowOf(Resource.Success(user)))
            val comment = Comment(
                id = "0",
                review = "Test",
                rating = 5,
                createdAt = Date(),
                updatedAt = Date(),
                likes = listOf(user),
                dislikes = emptyList(),
                user = user,
                restaurant = restaurant
            )
            whenever(
                likeRepository.deleteLike(
                    token = anyString(),
                    commentId = anyString()
                )
            ).thenReturn(
                Resource.Success("")
            )
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