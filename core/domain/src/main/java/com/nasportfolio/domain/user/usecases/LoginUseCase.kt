package com.nasportfolio.domain.user.usecases

import com.nasportfolio.domain.user.UserRepository
import com.nasportfolio.domain.utils.Resource
import com.nasportfolio.domain.utils.ResourceError
import com.nasportfolio.domain.validation.usecases.ValidateEmail
import com.nasportfolio.domain.validation.usecases.ValidatePassword
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val validateEmail: ValidateEmail,
    private val validatePassword: ValidatePassword,
) {
    operator fun invoke(
        email: String,
        password: String,
        fcmToken: String? = null
    ): Flow<Resource<Unit>> = flow {
        val emailValidationError = validateEmail(
            value = email
        )
        val passwordValidationError = validatePassword(
            value = password,
            flag = ValidatePassword.LOGIN_FLAG
        )
        if (emailValidationError != null || passwordValidationError != null) {
            val error = Resource.Failure<Unit>(
                ResourceError.FieldError(
                    message = "Invalid fields provided",
                    errors = listOfNotNull(
                        emailValidationError,
                        passwordValidationError
                    )
                )
            )
            return@flow emit(error)
        }
        emit(Resource.Loading<Unit>(isLoading = true))
        val loginResult = userRepository.login(
            email = email,
            password = password
        )
        when (loginResult) {
            is Resource.Success -> {
                userRepository.saveToken(loginResult.result)
                fcmToken?.let {
                    userRepository.updateAccount(token = loginResult.result, fcmToken = fcmToken)
                }
                emit(Resource.Success(Unit))
            }
            is Resource.Failure -> {
                emit(Resource.Failure<Unit>(loginResult.error))
            }
            else -> Unit
        }
    }
}