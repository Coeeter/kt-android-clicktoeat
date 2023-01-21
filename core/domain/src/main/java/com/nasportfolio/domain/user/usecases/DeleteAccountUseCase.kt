package com.nasportfolio.domain.user.usecases

import com.nasportfolio.domain.user.UserRepository
import com.nasportfolio.domain.utils.Resource
import com.nasportfolio.domain.utils.ResourceError
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DeleteAccountUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(password: String) = flow<Resource<Unit>> {
        validatePassword(password)?.let {
            return@flow emit(Resource.Failure(it))
        }
        emit(Resource.Loading(isLoading = true))
        val tokenResource = userRepository.getToken()
        if (tokenResource !is Resource.Success) {
            val error = (tokenResource as Resource.Failure).error
            return@flow emit(Resource.Failure(error = error))
        }
        val result = userRepository.deleteAccount(
            token = tokenResource.result,
            password = password
        )
        when (result) {
            is Resource.Success -> emit(Resource.Success(Unit))
            is Resource.Failure -> emit(Resource.Failure(result.error))
            else -> throw IllegalStateException()
        }
    }

    private fun validatePassword(password: String): ResourceError? {
        if (password.isNotEmpty()) return null
        return ResourceError.FieldError(
            message = "Errors in fields provided",
            errors = listOf(
                ResourceError.FieldErrorItem(
                    field = "password",
                    error = "Password required!"
                )
            )
        )
    }
}