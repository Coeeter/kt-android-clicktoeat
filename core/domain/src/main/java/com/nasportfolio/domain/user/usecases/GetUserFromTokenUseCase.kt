package com.nasportfolio.domain.user.usecases

import com.nasportfolio.domain.user.User
import com.nasportfolio.domain.user.UserRepository
import com.nasportfolio.domain.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetUserFromTokenUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<Resource<User>> = flow {
        emit(Resource.Loading(isLoading = true))
        val tokenResource = userRepository.getToken()
        if (tokenResource !is Resource.Success) return@flow emit(
            Resource.Failure(
                (tokenResource as Resource.Failure).error
            )
        )
        val userResource = userRepository.validateToken(token = tokenResource.result)
        if (userResource !is Resource.Success) return@flow emit(
            Resource.Failure(
                (tokenResource as Resource.Failure).error
            )
        )
        emit(Resource.Success(userResource.result))
    }
}