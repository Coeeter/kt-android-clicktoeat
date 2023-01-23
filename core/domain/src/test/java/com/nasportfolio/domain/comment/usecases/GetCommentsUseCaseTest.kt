package com.nasportfolio.domain.comment.usecases

import com.nasportfolio.domain.comment.FakeCommentRepository
import com.nasportfolio.domain.likesdislikes.dislike.DislikeRepository
import com.nasportfolio.domain.likesdislikes.like.LikeRepository
import com.nasportfolio.domain.utils.Resource
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

class GetCommentsUseCaseTest {

    private lateinit var getCommentsUseCase: GetCommentsUseCase
    private lateinit var fakeCommentRepository: FakeCommentRepository
    private lateinit var closeable: AutoCloseable

    @Mock
    lateinit var likeRepository: LikeRepository

    @Mock
    lateinit var dislikeRepository: DislikeRepository

    @Before
    fun setUp() {
        closeable = MockitoAnnotations.openMocks(this)
        fakeCommentRepository = FakeCommentRepository()
        getCommentsUseCase = GetCommentsUseCase(
            commentRepository = fakeCommentRepository,
            likeRepository = likeRepository,
            dislikeRepository = dislikeRepository
        )
    }

    @After
    fun tearDown() {
        closeable.close()
    }

    @Test
    fun `Invoke should return success resource related to restaurant id`() = runBlocking {
        whenever(
            likeRepository.getUsersWhoLikedComment(anyString())
        ).thenReturn(
            Resource.Success(emptyList())
        )
        whenever(
            dislikeRepository.getUsersWhoDislikedComments(anyString())
        ).thenReturn(
            Resource.Success(emptyList())
        )
        val index = Random().nextInt(fakeCommentRepository.comments.size)
        val comment = fakeCommentRepository.comments[index]
        val result = getCommentsUseCase(restaurantId = comment.restaurant.id).toList()
        assertTrue(result[0] is Resource.Loading)
        assertTrue(result.last() is Resource.Success)
        val commentList = (result.last() as Resource.Success).result
        assertEquals(1, commentList.size)
        assertEquals(comment.id, commentList[0].id)
    }

    @Test
    fun `byUser should return success resource related to user id`() = runBlocking {
        whenever(
            likeRepository.getUsersWhoLikedComment(anyString())
        ).thenReturn(
            Resource.Success(emptyList())
        )
        whenever(
            dislikeRepository.getUsersWhoDislikedComments(anyString())
        ).thenReturn(
            Resource.Success(emptyList())
        )
        val index = Random().nextInt(fakeCommentRepository.comments.size)
        val comment = fakeCommentRepository.comments[index]
        val result = getCommentsUseCase.byUser(userId = comment.user.id).toList()
        assertTrue(result[0] is Resource.Loading)
        assertTrue(result.last() is Resource.Success)
        val commentList = (result.last() as Resource.Success).result
        assertEquals(1, commentList.size)
        assertEquals(comment.id, commentList[0].id)
    }

    @Test
    fun `byId should return success resource related to comment id`() = runBlocking {
        whenever(
            likeRepository.getUsersWhoLikedComment(anyString())
        ).thenReturn(
            Resource.Success(emptyList())
        )
        whenever(
            dislikeRepository.getUsersWhoDislikedComments(anyString())
        ).thenReturn(
            Resource.Success(emptyList())
        )
        val index = Random().nextInt(fakeCommentRepository.comments.size)
        val comment = fakeCommentRepository.comments[index]
        val result = getCommentsUseCase.byId(commentId = comment.id).toList()
        assertTrue(result[0] is Resource.Loading)
        assertTrue(result.last() is Resource.Success)
        val commentList = (result.last() as Resource.Success).result
        assertEquals(comment.id, commentList.id)
    }
}