package com.nasportfolio.domain.user.usecases

import com.nasportfolio.domain.user.UserRepository
import com.nasportfolio.domain.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import javax.inject.Inject

class CreateAccountUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(
        username: String,
        email: String,
        password: String,
        confirmPassword: String,
        fcmToken: String,
        image: File?,
    ): Flow<Resource<Unit>> = flow {
        TODO()
    }
}