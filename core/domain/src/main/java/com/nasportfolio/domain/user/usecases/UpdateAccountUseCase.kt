package com.nasportfolio.domain.user.usecases

import com.nasportfolio.domain.user.User
import com.nasportfolio.domain.user.UserRepository
import com.nasportfolio.domain.utils.Resource
import com.nasportfolio.domain.utils.ResourceError
import com.nasportfolio.domain.validation.usecases.ValidateConfirmPassword
import com.nasportfolio.domain.validation.usecases.ValidateEmail
import com.nasportfolio.domain.validation.usecases.ValidatePassword
import com.nasportfolio.domain.validation.usecases.ValidateUsername
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UpdateAccountUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val validateUsername: ValidateUsername,
    private val validateEmail: ValidateEmail,
    private val validatePassword: ValidatePassword,
    private val validateConfirmPassword: ValidateConfirmPassword,
) {
    operator fun invoke(
        username: String,
        email: String,
        password: String,
        confirmPassword: String
    ) = flow<Resource<User>> {
        when (val error = validateFields(username, email, password, confirmPassword)) {
            is Resource.Failure -> return@flow emit(Resource.Failure(error.error))
            else -> emit(Resource.Loading(isLoading = true))
        }
        emit(Resource.Loading(isLoading = true))
        val currentTokenResource = userRepository.getToken()
        if (currentTokenResource !is Resource.Success) {
            val error = (currentTokenResource as Resource.Failure).error
            return@flow emit(Resource.Failure(error = error))
        }
        val updatedTokenResource = userRepository.updateAccount(
            token = currentTokenResource.result,
            username = username,
            email = email,
            password = password
        )
        if (updatedTokenResource !is Resource.Success) {
            val error = (currentTokenResource as Resource.Failure).error
            return@flow emit(Resource.Failure(error = error))
        }
        userRepository.saveToken(updatedTokenResource.result)
        emit(userRepository.validateToken(updatedTokenResource.result))
    }

    fun updateImage(image: ByteArray) = flow<Resource<User>> {
        emit(Resource.Loading(isLoading = true))
        val currentTokenResource = userRepository.getToken()
        if (currentTokenResource !is Resource.Success) {
            val error = (currentTokenResource as Resource.Failure).error
            return@flow emit(Resource.Failure(error = error))
        }
        val updateResult = userRepository.updateAccount(
            token = currentTokenResource.result,
            image = image
        )
        if (updateResult !is Resource.Success) {
            val error = (currentTokenResource as Resource.Failure).error
            return@flow emit(Resource.Failure(error = error))
        }
        userRepository.saveToken(updateResult.result)
        emit(userRepository.validateToken(updateResult.result))
    }

    fun deleteImage() = flow<Resource<Unit>> {
        emit(Resource.Loading(isLoading = true))
        val currentTokenResource = userRepository.getToken()
        if (currentTokenResource !is Resource.Success) {
            val error = (currentTokenResource as Resource.Failure).error
            return@flow emit(Resource.Failure(error = error))
        }
        val deleteResult = userRepository.updateAccount(
            token = currentTokenResource.result,
            deleteImage = true
        )
        when (deleteResult) {
            is Resource.Success -> emit(Resource.Success(Unit))
            is Resource.Failure -> emit(Resource.Failure(deleteResult.error))
            else -> throw IllegalStateException()
        }
    }

    private fun validateFields(
        username: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Resource<Unit> {
        val usernameValidationError = validateUsername(
            value = username
        )
        val emailValidationError = validateEmail(
            value = email
        )
        val passwordValidationError = validatePassword(
            value = password,
            flag = ValidatePassword.CREATE_FLAG
        )
        val confirmPasswordValidationError = validateConfirmPassword(
            value = confirmPassword,
            password = password
        )
        if (
            emailValidationError != null ||
            passwordValidationError != null ||
            usernameValidationError != null ||
            confirmPasswordValidationError != null
        ) {
            return Resource.Failure(
                ResourceError.FieldError(
                    message = "Invalid fields provided",
                    errors = listOfNotNull(
                        usernameValidationError,
                        emailValidationError,
                        passwordValidationError,
                        confirmPasswordValidationError
                    )
                )
            )
        }
        return Resource.Success(Unit)
    }
}