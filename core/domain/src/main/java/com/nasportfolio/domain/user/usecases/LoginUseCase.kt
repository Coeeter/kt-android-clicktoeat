package com.nasportfolio.domain.user.usecases

import com.nasportfolio.domain.user.UserRepository
import com.nasportfolio.domain.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(
        email: String,
        password: String
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading<Unit>(
            isLoading = true
        ))
        val loginResult = userRepository.login(
            email = email,
            password = password
        )
        when (loginResult) {
            is Resource.Success -> {
                userRepository.saveToken(loginResult.result)
                emit(Resource.Success(Unit))
            }
            is Resource.Failure -> {
                emit(Resource.Failure<Unit>(loginResult.error))
            }
            else -> Unit
        }
    }
}