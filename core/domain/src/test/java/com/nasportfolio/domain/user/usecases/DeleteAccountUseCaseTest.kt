package com.nasportfolio.domain.user.usecases

import com.nasportfolio.domain.user.FakeUserRepository
import com.nasportfolio.domain.utils.Resource
import com.nasportfolio.domain.utils.ResourceError
import com.nasportfolio.domain.validation.usecases.ValidatePassword
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class DeleteAccountUseCaseTest {

    private lateinit var fakeUserRepository: FakeUserRepository
    private lateinit var deleteAccountUseCase: DeleteAccountUseCase

    @Before
    fun setUp() {
        fakeUserRepository = FakeUserRepository()
        deleteAccountUseCase = DeleteAccountUseCase(
            userRepository = fakeUserRepository,
            validatePassword = ValidatePassword()
        )
    }

    @Test
    fun `When given blank password should send failure resource`() = runBlocking {
        val result = deleteAccountUseCase(password = "").last()
        assertEquals(true, result is Resource.Failure)
        assertEquals(true, (result as Resource.Failure).error is ResourceError.FieldError)
        val fieldError = result.error as ResourceError.FieldError
        assertEquals(1, fieldError.errors.size)
        assertEquals("password", fieldError.errors[0].field)
    }

    @Test
    fun `When given password, should delete account and token`() = runBlocking {
        val oldList = fakeUserRepository.users
        val result = deleteAccountUseCase(password = "password").toList()
        assertEquals(true, result[0] is Resource.Loading)
        assertEquals(true, result.last() is Resource.Success)
        assertNotEquals(oldList.size, fakeUserRepository.users.size)
        assertNotEquals(oldList.last().id, fakeUserRepository.users.last().id)
        assertNull(fakeUserRepository.token)
    }
}