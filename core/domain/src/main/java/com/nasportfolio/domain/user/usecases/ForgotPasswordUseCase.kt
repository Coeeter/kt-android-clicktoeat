package com.nasportfolio.domain.user.usecases

import com.nasportfolio.domain.user.UserRepository
import com.nasportfolio.domain.utils.Resource
import com.nasportfolio.domain.utils.ResourceError
import com.nasportfolio.domain.validation.usecases.ValidateConfirmPassword
import com.nasportfolio.domain.validation.usecases.ValidateEmail
import com.nasportfolio.domain.validation.usecases.ValidatePassword
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ForgotPasswordUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val validateEmail: ValidateEmail,
    private val validatePassword: ValidatePassword,
    private val validateConfirmPassword: ValidateConfirmPassword
) {
    operator fun invoke(email: String) = flow<Resource<Unit>> {
        validateEmail(email)?.let {
            return@flow emit(
                Resource.Failure(
                    ResourceError.FieldError(
                        message = "Errors in fields provided",
                        errors = listOf(it)
                    )
                )
            )
        }
        emit(Resource.Loading(isLoading = true))
        when (val result = userRepository.forgotPassword(email = email)) {
            is Resource.Success -> emit(Resource.Success(Unit))
            is Resource.Failure -> emit(Resource.Failure(result.error))
            else -> throw IllegalStateException()
        }
    }

    fun resetPassword(
        token: String,
        password: String,
        confirmPassword: String
    ) = flow<Resource<Unit>> {
        when (val error = validateFields(password = password, confirmPassword = confirmPassword)) {
            is Resource.Failure -> return@flow emit(Resource.Failure(error.error))
            else -> emit(Resource.Loading(isLoading = true))
        }
        val result = userRepository.updateAccount(
            token = token,
            password = password
        )
        when (result) {
            is Resource.Success -> emit(Resource.Success(Unit))
            is Resource.Failure -> emit(Resource.Failure(result.error))
            else -> throw IllegalStateException()
        }
    }

    private fun validateFields(
        password: String,
        confirmPassword: String
    ): Resource<Unit> {
        val passwordValidationError = validatePassword(
            value = password,
            flag = ValidatePassword.CREATE_FLAG
        )
        val confirmPasswordValidationError = validateConfirmPassword(
            value = confirmPassword,
            password = password
        )
        if (
            passwordValidationError != null ||
            confirmPasswordValidationError != null
        ) {
            return Resource.Failure(
                ResourceError.FieldError(
                    message = "Invalid fields provided",
                    errors = listOfNotNull(
                        confirmPasswordValidationError,
                        passwordValidationError,
                    )
                )
            )
        }
        return Resource.Success(Unit)
    }
}