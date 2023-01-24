package com.nasportfolio.domain.comment.usecases

import com.nasportfolio.domain.comment.FakeCommentRepository
import com.nasportfolio.domain.likesdislikes.FakeDislikeRepository
import com.nasportfolio.domain.likesdislikes.FakeLikeRepository
import com.nasportfolio.domain.user.FakeUserRepository
import com.nasportfolio.domain.utils.Resource
import com.nasportfolio.domain.utils.ResourceError
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class EditCommentUseCaseTest {
    private lateinit var editCommentUseCase: EditCommentUseCase
    private lateinit var fakeCommentRepository: FakeCommentRepository

    @Before
    fun setUp() {
        fakeCommentRepository = FakeCommentRepository()
        val fakeUserRepository = FakeUserRepository()
        editCommentUseCase = EditCommentUseCase(
            userRepository = fakeUserRepository,
            commentRepository = fakeCommentRepository,
            likeRepository = FakeLikeRepository(
                fakeCommentRepository = fakeCommentRepository,
                fakeUserRepository = fakeUserRepository
            ),
            dislikeRepository = FakeDislikeRepository(
                fakeCommentRepository = fakeCommentRepository,
                fakeUserRepository = fakeUserRepository
            )
        )
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