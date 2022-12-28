package com.nasportfolio.domain.user.usecases

import com.nasportfolio.domain.user.UserRepository
import com.nasportfolio.domain.utils.Resource
import javax.inject.Inject

class ValidateTokenUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Boolean {
        return when (val tokenResource = userRepository.getToken()) {
            is Resource.Success -> {
                when (userRepository.validateToken(tokenResource.result)) {
                    is Resource.Success -> true
                    else -> false
                }
            }
            else -> false
        }
    }
}