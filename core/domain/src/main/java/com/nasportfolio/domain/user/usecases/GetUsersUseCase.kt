package com.nasportfolio.domain.user.usecases

import com.nasportfolio.domain.user.User
import com.nasportfolio.domain.user.UserRepository
import com.nasportfolio.domain.utils.Resource
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetUsersUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke() = flow<Resource<List<User>>> {
        emit(Resource.Loading(isLoading = true))
        emit(userRepository.getAllUsers())
    }

    fun getById(id: String) = flow<Resource<User>> {
        emit(Resource.Loading(isLoading = true))
        emit(userRepository.getUserById(id = id))
    }
}