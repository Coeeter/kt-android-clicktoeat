package com.nasportfolio.domain.user

import com.nasportfolio.domain.user.usecases.LoginUseCase
import com.nasportfolio.domain.utils.Resource
import com.nasportfolio.domain.utils.ResourceError
import com.nasportfolio.domain.validation.usecases.ValidateEmail
import com.nasportfolio.domain.validation.usecases.ValidatePassword
import com.nasportfolio.test.user.FakeUserRepository
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class LoginUseCaseTest {

    private lateinit var fakeUserRepository: FakeUserRepository
    private lateinit var loginUseCase: LoginUseCase

    @Before
    fun setUp() {
        fakeUserRepository = FakeUserRepository()
        loginUseCase = LoginUseCase(
            userRepository = fakeUserRepository,
            validatePassword = ValidatePassword(),
            validateEmail = ValidateEmail()
        )
    }

    @Test
    fun `When given invalid inputs should return failure resource`() = runBlocking {
        val result = loginUseCase(email = "", password = "").last()
        assertEquals(true, result is Resource.Failure)
        val failure = result as Resource.Failure
        assertEquals(true, failure.error is ResourceError.FieldError)
        val fieldError = failure.error as ResourceError.FieldError
        assertEquals(2, fieldError.errors.size)
    }

    @Test
    fun `When given valid inputs should return success resource and save token`() = runBlocking {
        val oldToken = fakeUserRepository.token
        val email = fakeUserRepository.users[0].email
        val result = loginUseCase(email = email, password = "asdfadfsdf").toList()
        assertEquals(true, result[0] is Resource.Loading)
        assertEquals(true, result.last() is Resource.Success)
        val savedToken = fakeUserRepository.token
        assertNotNull(savedToken)
        assertNotEquals(oldToken, savedToken)
    }
}