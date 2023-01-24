package com.nasportfolio.domain.user.usecases

import com.nasportfolio.domain.user.FakeUserRepository
import com.nasportfolio.domain.utils.Resource
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetUsersUseCaseTest {

    private lateinit var fakeUserRepository: FakeUserRepository
    private lateinit var getUsersUseCase: GetUsersUseCase

    @Before
    fun setUp() {
        fakeUserRepository = FakeUserRepository()
        getUsersUseCase = GetUsersUseCase(fakeUserRepository)
    }

    @Test
    fun `when called should return all users`() = runBlocking {
        val users = fakeUserRepository.users
        val result = getUsersUseCase().toList()
        assertEquals(true, result[0] is Resource.Loading)
        assertEquals(true, result.last() is Resource.Success)
        val resultUsers = result.last() as Resource.Success
        assertEquals(users, resultUsers.result)
    }

    @Test
    fun `When id of user is valid, should return success resource`() = runBlocking {
        val user = fakeUserRepository.users[0]
        val result = getUsersUseCase.getById(user.id).toList()
        assertEquals(true, result[0] is Resource.Loading)
        assertEquals(true, result.last() is Resource.Success)
        val resultUsers = result.last() as Resource.Success
        assertEquals(user, resultUsers.result)
    }

    @Test
    fun `When id of user is invalid, should return failure resource`() = runBlocking {
        val user = ";alfskjdf;alskdjf;alsdfk"
        val result = getUsersUseCase.getById(user).toList()
        assertEquals(true, result[0] is Resource.Loading)
        assertEquals(true, result.last() is Resource.Failure)
    }
}