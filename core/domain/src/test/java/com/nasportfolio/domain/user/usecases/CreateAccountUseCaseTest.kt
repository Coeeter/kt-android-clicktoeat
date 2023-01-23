package com.nasportfolio.domain.user.usecases

import com.nasportfolio.domain.user.FakeUserRepository
import com.nasportfolio.domain.user.UserRepository
import com.nasportfolio.domain.utils.Resource
import com.nasportfolio.domain.validation.usecases.ValidateConfirmPassword
import com.nasportfolio.domain.validation.usecases.ValidateEmail
import com.nasportfolio.domain.validation.usecases.ValidatePassword
import com.nasportfolio.domain.validation.usecases.ValidateUsername
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class CreateAccountUseCaseTest {

    private lateinit var createAccountUseCase: CreateAccountUseCase
    private lateinit var userRepository: UserRepository

    @Before
    fun setUp() {
        userRepository = FakeUserRepository()
        createAccountUseCase = CreateAccountUseCase(
            userRepository = userRepository,
            validateEmail = ValidateEmail(),
            validateUsername = ValidateUsername(),
            validatePassword = ValidatePassword(),
            validateConfirmPassword = ValidateConfirmPassword()
        )
    }

    @Test
    fun `When given invalid input should return failure`() = runBlocking {
        val result = createAccountUseCase(
            username = "",
            email = "",
            password = "",
            confirmPassword = "",
            fcmToken = "",
        ).last()
        assertEquals(true, result is Resource.Failure)
    }

    @Test
    fun `When creating account, should update user list and save new token`() = runBlocking {
        val repo = userRepository as FakeUserRepository
        val oldToken = repo.token
        val oldList = repo.users
        val result = createAccountUseCase(
            username = "test",
            email = "test@gmail.com",
            password = "StrongPassword!1",
            confirmPassword = "StrongPassword!1",
            fcmToken = "fcmToken",
        ).toList()
        assertEquals(true, result[0] is Resource.Loading)
        assertEquals(true, result.last() is Resource.Success)
        val newToken = repo.token
        val newList = repo.users
        assertEquals(false, oldToken == newToken)
        assertEquals(false, oldList.last().id == newList.last().id)
        assertEquals("test", newList.last().username)
        assertEquals("test@gmail.com", newList.last().email)
    }
}