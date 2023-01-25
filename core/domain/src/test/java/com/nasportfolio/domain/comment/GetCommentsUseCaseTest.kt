package com.nasportfolio.domain.comment

import com.nasportfolio.domain.comment.usecases.GetCommentsUseCase
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

class GetCommentsUseCaseTest {

    private lateinit var getCommentsUseCase: GetCommentsUseCase
    private lateinit var fakeCommentRepository: FakeCommentRepository

    @Before
    fun setUp() {
        fakeCommentRepository = FakeCommentRepository()
        val fakeUserRepository = FakeUserRepository()
        getCommentsUseCase = GetCommentsUseCase(
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
    fun `Invoke should return success resource related to restaurant id`() = runBlocking {
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
        val index = Random().nextInt(fakeCommentRepository.comments.size)
        val comment = fakeCommentRepository.comments[index]
        val result = getCommentsUseCase.byId(commentId = comment.id).toList()
        assertTrue(result[0] is Resource.Loading)
        assertTrue(result.last() is Resource.Success)
        val commentList = (result.last() as Resource.Success).result
        assertEquals(comment.id, commentList.id)
    }
}