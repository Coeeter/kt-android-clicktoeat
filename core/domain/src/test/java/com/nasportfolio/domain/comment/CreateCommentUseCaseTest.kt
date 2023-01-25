package com.nasportfolio.domain.comment

import com.nasportfolio.domain.comment.usecases.CreateCommentUseCase
import com.nasportfolio.domain.utils.Resource
import com.nasportfolio.domain.utils.ResourceError
import com.nasportfolio.test.comment.FakeCommentRepository
import com.nasportfolio.test.likesdislikes.FakeDislikeRepository
import com.nasportfolio.test.likesdislikes.FakeLikeRepository
import com.nasportfolio.test.user.FakeUserRepository
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test


class CreateCommentUseCaseTest {

    private lateinit var fakeCommentRepository: FakeCommentRepository
    private lateinit var createCommentUseCase: CreateCommentUseCase

    @Before
    fun setUp() {
        fakeCommentRepository = FakeCommentRepository()
        val userRepository = FakeUserRepository()
        createCommentUseCase = CreateCommentUseCase(
            commentRepository = fakeCommentRepository,
            userRepository = userRepository,
            likeRepository = FakeLikeRepository(
                fakeCommentRepository = fakeCommentRepository,
                fakeUserRepository = userRepository
            ),
            dislikeRepository = FakeDislikeRepository(
                fakeCommentRepository = fakeCommentRepository,
                fakeUserRepository = userRepository
            )
        )
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
            val result = createCommentUseCase("test", "test", 5).toList()
            assertTrue(result[0] is Resource.Loading)
            assertTrue(result.last() is Resource.Success)
            val insertedComment = (result.last() as Resource.Success).result
            assertEquals(insertedComment.id, fakeCommentRepository.comments.last().id)
            assertEquals("test", insertedComment.review)
            assertEquals(5, insertedComment.rating)
        }
}