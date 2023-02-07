package com.nasportfolio.domain.user.usecases

import com.nasportfolio.domain.user.User
import com.nasportfolio.domain.user.UserRepository
import com.nasportfolio.domain.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class GetUsersUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(
        fetchFromRemote: Boolean = true
    ): Flow<Resource<List<User>>> = userRepository
        .getAllUsers(fetchFromRemote = fetchFromRemote)
        .onStart { emit(Resource.Loading(isLoading = true)) }


    fun getById(
        id: String,
        fetchFromRemote: Boolean = true
    ): Flow<Resource<User>> = userRepository
        .getUserById(id = id, fetchFromRemote = fetchFromRemote)
        .onStart { emit(Resource.Loading(isLoading = true)) }
}