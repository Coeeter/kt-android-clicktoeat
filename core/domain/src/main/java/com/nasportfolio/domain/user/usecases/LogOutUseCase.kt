package com.nasportfolio.domain.user.usecases

import com.nasportfolio.domain.user.UserRepository
import javax.inject.Inject

class LogOutUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val updateAccountUseCase: UpdateAccountUseCase
) {
    operator fun invoke() {
        updateAccountUseCase.updateFcmToken(fcmToken = null)?.let {
            println("debug: $it")
        }
        userRepository.removeToken()
    }
}