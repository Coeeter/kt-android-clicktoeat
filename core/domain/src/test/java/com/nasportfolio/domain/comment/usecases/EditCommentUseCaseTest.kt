package com.nasportfolio.domain.comment.usecases

import com.nasportfolio.domain.comment.FakeCommentRepository
import com.nasportfolio.domain.likesdislikes.dislike.DislikeRepository
import com.nasportfolio.domain.likesdislikes.like.LikeRepository
import com.nasportfolio.domain.user.FakeUserRepository
import com.nasportfolio.domain.utils.Resource
import com.nasportfolio.domain.utils.ResourceError
import kotlinx.coroutines.flow.last
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

class EditCommentUseCaseTest {
    lateinit var editCommentUseCase: EditCommentUseCase
    lateinit var fakeCommentRepository: FakeCommentRepository
    lateinit var closeable: AutoCloseable

    @Mock
    lateinit var likeRepository: LikeRepository

    @Mock
    lateinit var dislikeRepository: DislikeRepository

    @Before
    fun setUp() {
        closeable = MockitoAnnotations.openMocks(this)
        fakeCommentRepository = FakeCommentRepository()
        editCommentUseCase = EditCommentUseCase(
            userRepository = FakeUserRepository(),
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
    fun `When given invalid fields, should return Failure resource`() = runBlocking {
        val result = editCommentUseCase(
            commentId = "",
            review = "",
            rating = 0
        ).last()
        assertTrue(result is Resource.Failure)
        val failure = result as Resource.Failure
        assertTrue(failure.error is ResourceError.FieldError)
        val fieldError = failure.error as ResourceError.FieldError
        assertEquals(2, fieldError.errors.size)
    }

    @Test
    fun `When given valid fields, should return success resource and update list`() = runBlocking {
        whenever(
            likeRepository.getUsersWhoLikedComment(anyString())
        ).thenReturn(
            Resource.Success(
                emptyList()
            )
        )
        whenever(
            dislikeRepository.getUsersWhoDislikedComments(anyString())
        ).thenReturn(
            Resource.Success(
                emptyList()
            )
        )
        val oldComment = fakeCommentRepository.comments.last()
        val result = editCommentUseCase(
            commentId = oldComment.id,
            review = "Test",
            rating = 5
        ).last()
        assertTrue(result is Resource.Success)
        val newComment = (result as Resource.Success).result
        assertEquals(oldComment.id, newComment.id)
        assertEquals("Test", newComment.review)
        assertEquals(5, newComment.rating)
    }
}