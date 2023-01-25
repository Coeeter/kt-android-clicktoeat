package com.nasportfolio.domain.user

import com.nasportfolio.domain.user.usecases.GetCurrentLoggedInUser
import com.nasportfolio.domain.utils.Resource
import com.nasportfolio.test.user.FakeUserRepository
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetCurrentLoggedInUserTest {

    private lateinit var fakeUserRepository: FakeUserRepository
    private lateinit var getCurrentLoggedInUser: GetCurrentLoggedInUser

    @Before
    fun setUp() {
        fakeUserRepository = FakeUserRepository()
        getCurrentLoggedInUser = GetCurrentLoggedInUser(
            userRepository = fakeUserRepository
        )
    }

    @Test
    fun `When token is not present should send failure resource`() = runBlocking {
        fakeUserRepository.token = null
        val result = getCurrentLoggedInUser().toList()
        assertEquals(true, result[0] is Resource.Loading)
        assertEquals(true, result.last() is Resource.Failure)
    }

    @Test
    fun `When token is present should send user resource`() = runBlocking {
        val currentUser = fakeUserRepository.users.last()
        val result = getCurrentLoggedInUser().toList()
        assertEquals(true, result[0] is Resource.Loading)
        assertEquals(true, result.last() is Resource.Success)
        val user = result.last() as Resource.Success
        assertEquals(currentUser.id, user.result.id)
        assertEquals(currentUser.username, user.result.username)
        assertEquals(currentUser.email, user.result.email)
    }
}