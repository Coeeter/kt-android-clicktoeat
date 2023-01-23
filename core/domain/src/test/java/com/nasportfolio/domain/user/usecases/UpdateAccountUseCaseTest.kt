package com.nasportfolio.domain.user.usecases

import com.nasportfolio.domain.image.Image
import com.nasportfolio.domain.user.FakeUserRepository
import com.nasportfolio.domain.utils.Resource
import com.nasportfolio.domain.utils.ResourceError
import com.nasportfolio.domain.validation.usecases.ValidateConfirmPassword
import com.nasportfolio.domain.validation.usecases.ValidateEmail
import com.nasportfolio.domain.validation.usecases.ValidatePassword
import com.nasportfolio.domain.validation.usecases.ValidateUsername
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class UpdateAccountUseCaseTest {

    private lateinit var fakeUserRepository: FakeUserRepository
    private lateinit var updateAccountUseCase: UpdateAccountUseCase

    @Before
    fun setUp() {
        fakeUserRepository = FakeUserRepository()
        updateAccountUseCase = UpdateAccountUseCase(
            userRepository = fakeUserRepository,
            validateEmail = ValidateEmail(),
            validatePassword = ValidatePassword(),
            validateConfirmPassword = ValidateConfirmPassword(),
            validateUsername = ValidateUsername()
        )
    }

    @Test
    fun `When given invalid fields should return failure resource`() = runBlocking {
        val result = updateAccountUseCase(username = "", email = "").last()
        assertEquals(true, result is Resource.Failure)
        val failure = result as Resource.Failure
        assertEquals(true, failure.error is ResourceError.FieldError)
        val fieldError = failure.error as ResourceError.FieldError
        assertEquals(2, fieldError.errors.size)
    }

    @Test
    fun `When given valid inputs should return success resource, update user and update token`() =
        runBlocking {
            val oldToken = fakeUserRepository.token
            val user = fakeUserRepository.users.last()
            val result = updateAccountUseCase(
                username = "updatedUsername",
                email = "updatedEmail@gmail.com"
            ).toList()
            assertEquals(true, result[0] is Resource.Loading)
            assertEquals(true, result.last() is Resource.Success)
            val updatedUser = (result.last() as Resource.Success).result
            assertEquals(updatedUser.id, user.id)
            assertEquals(updatedUser.username, "updatedUsername")
            assertEquals(updatedUser.email, "updatedEmail@gmail.com")
            assertNotEquals(oldToken, fakeUserRepository.token)
        }

    @Test
    fun `When given invalid fields to update password should return failure resource`() =
        runBlocking {
            val result =
                updateAccountUseCase.updatePassword(password = "adfadfs", confirmPassword = "c")
                    .last()
            assertEquals(true, result is Resource.Failure)
            val failure = result as Resource.Failure
            assertEquals(true, failure.error is ResourceError.FieldError)
            val fieldError = failure.error as ResourceError.FieldError
            assertEquals(2, fieldError.errors.size)
        }

    @Test
    fun `When given valid inputs to update password should return success resource and update token`() =
        runBlocking {
            val oldToken = fakeUserRepository.token
            val result = updateAccountUseCase.updatePassword(
                password = "StrongPassword!1",
                confirmPassword = "StrongPassword!1"
            ).toList()
            assertEquals(true, result[0] is Resource.Loading)
            assertEquals(true, result.last() is Resource.Success)
            assertNotEquals(oldToken, fakeUserRepository.token)
        }

    @Test
    fun `When updating image should return success resource and update token and user`() =
        runBlocking {
            val oldToken = fakeUserRepository.token
            val user = fakeUserRepository.users.last()
            val result = updateAccountUseCase.updateImage(image = ByteArray(5)).toList()
            assertEquals(true, result[0] is Resource.Loading)
            assertEquals(true, result.last() is Resource.Success)
            assertNotEquals(oldToken, fakeUserRepository.token)
            val updatedUser = result.last() as Resource.Success
            assertEquals(user.id, updatedUser.result.id)
            assertNotNull(updatedUser.result.image)
            assertNotEquals(user.image, updatedUser.result.image)
        }

    @Test
    fun `When deleting image should return success resource and update token and user`() =
        runBlocking {
            val oldToken = fakeUserRepository.token
            fakeUserRepository.users = fakeUserRepository.users.toMutableList().apply {
                val user = last().copy(image = Image(0, "", ""))
                removeLast()
                add(user)
            }
            val user = fakeUserRepository.users.last()
            val result = updateAccountUseCase.deleteImage().toList()
            assertEquals(true, result[0] is Resource.Loading)
            assertEquals(true, result.last() is Resource.Success)
            assertNotEquals(oldToken, fakeUserRepository.token)
            val updatedUser = fakeUserRepository.users.last()
            assertEquals(user.id, updatedUser.id)
            assertNull(updatedUser.image)
            assertNotEquals(user.image, updatedUser.image)
        }
}