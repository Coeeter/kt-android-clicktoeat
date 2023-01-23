package com.nasportfolio.domain.comment.usecases

import com.nasportfolio.domain.comment.FakeCommentRepository
import com.nasportfolio.domain.user.FakeUserRepository
import com.nasportfolio.domain.utils.Resource
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DeleteCommentUseCaseTest {

    lateinit var deleteCommentUseCase: DeleteCommentUseCase
    lateinit var fakeCommentRepository: FakeCommentRepository

    @Before
    fun setUp() {
        fakeCommentRepository = FakeCommentRepository()
        deleteCommentUseCase = DeleteCommentUseCase(
            commentRepository = fakeCommentRepository,
            userRepository = FakeUserRepository()
        )
    }

    @Test
    fun `When given valid inputs, should return success resource and update list`() = runBlocking {
        val commentToBeDeleted = fakeCommentRepository.comments.last()
        val result = deleteCommentUseCase(commentToBeDeleted.id).toList()
        assertTrue(result[0] is Resource.Loading)
        assertTrue(result.last() is Resource.Success)
        assertNull(fakeCommentRepository.comments.find { it.id == commentToBeDeleted.id })
    }

    @Test
    fun `When given invalid inputs, should return failure resource`() = runBlocking {
        val result = deleteCommentUseCase("sdfasdfasdf").toList()
        assertTrue(result[0] is Resource.Loading)
        assertTrue(result.last() is Resource.Failure)
    }
}