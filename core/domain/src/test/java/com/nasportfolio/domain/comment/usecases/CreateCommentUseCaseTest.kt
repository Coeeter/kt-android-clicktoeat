package com.nasportfolio.domain.comment.usecases

import com.nasportfolio.domain.comment.FakeCommentRepository
import com.nasportfolio.domain.likesdislikes.dislike.DislikeRepository
import com.nasportfolio.domain.likesdislikes.like.LikeRepository
import com.nasportfolio.domain.user.FakeUserRepository
import com.nasportfolio.domain.utils.Resource
import com.nasportfolio.domain.utils.ResourceError
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever


class CreateCommentUseCaseTest {

    lateinit var fakeCommentRepository: FakeCommentRepository
    lateinit var createCommentUseCase: CreateCommentUseCase
    lateinit var closeable: AutoCloseable

    @Mock
    lateinit var likeRepository: LikeRepository

    @Mock
    lateinit var dislikeRepository: DislikeRepository

    @Before
    fun setUp() {
        closeable = MockitoAnnotations.openMocks(this)
        fakeCommentRepository = FakeCommentRepository()
        createCommentUseCase = CreateCommentUseCase(
            commentRepository = fakeCommentRepository,
            userRepository = FakeUserRepository(),
            likeRepository = likeRepository,
            dislikeRepository = dislikeRepository
        )
    }

    @After
    fun tearDown() {
        closeable.close()
    }

    @Test
    fun `When given invalid inputs should return failure resource`() = runBlocking {
        val result = createCommentUseCase("", "", 0).last()
        assertTrue(result is Resource.Failure)
        val failure = result as Resource.Failure
        assertTrue(failure.error is ResourceError.FieldError)
        val fieldError = failure.error as ResourceError.FieldError
        assertEquals(2, fieldError.errors.size)
    }

    @Test
    fun `When given valid inputs should return success resource and list should be updated`() =
        runBlocking {
            whenever(
                methodCall = likeRepository.getUsersWhoLikedComment(
                    anyString()
                )
            ).thenReturn(
                Resource.Success(
                    emptyList()
                )
            )
            whenever(
                methodCall = dislikeRepository.getUsersWhoDislikedComments(
                    anyString()
                )
            ).thenReturn(
                Resource.Success(
                    emptyList()
                )
            )
            val result = createCommentUseCase("test", "test", 5).toList()
            assertTrue(result[0] is Resource.Loading)
            assertTrue(result.last() is Resource.Success)
            val insertedComment = (result.last() as Resource.Success).result
            assertEquals(insertedComment.id, fakeCommentRepository.comments.last().id)
            assertEquals("test", insertedComment.review)
            assertEquals(5, insertedComment.rating)
        }
}