package com.nasportfolio.domain.user.usecases

import com.nasportfolio.domain.user.UserRepository
import com.nasportfolio.domain.utils.Resource
import com.nasportfolio.domain.utils.ResourceError
import com.nasportfolio.domain.validation.usecases.ValidateConfirmPassword
import com.nasportfolio.domain.validation.usecases.ValidateEmail
import com.nasportfolio.domain.validation.usecases.ValidatePassword
import com.nasportfolio.domain.validation.usecases.ValidateUsername
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CreateAccountUseCase @Inject constructor(
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
        confirmPassword: String,
        fcmToken: String,
        image: ByteArray? = null,
    ): Flow<Resource<Unit>> = flow {
        when (val error = validateFields(username, email, password, confirmPassword)) {
            is Resource.Failure -> return@flow emit(error)
            else -> emit(Resource.Loading<Unit>(isLoading = true))
        }
        val signUpResult = userRepository.signUp(
            username = username,
            email = email,
            password = password,
            fcmToken = fcmToken,
            image = image
        )
        when (signUpResult) {
            is Resource.Success -> {
                userRepository.saveToken(signUpResult.result)
                emit(Resource.Success(Unit))
            }
            is Resource.Failure -> {
                emit(Resource.Failure<Unit>(signUpResult.error))
            }
            else -> Unit
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