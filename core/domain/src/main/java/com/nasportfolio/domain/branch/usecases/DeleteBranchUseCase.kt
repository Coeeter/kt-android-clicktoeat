package com.nasportfolio.domain.branch.usecases

import com.nasportfolio.domain.branch.BranchRepository
import com.nasportfolio.domain.user.UserRepository
import com.nasportfolio.domain.utils.Resource
import com.nasportfolio.domain.utils.ResourceError
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DeleteBranchUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val branchRepository: BranchRepository
) {
    operator fun invoke(
        branchId: String,
        restaurantId: String
    ) = flow<Resource<Unit>> {
        emit(Resource.Loading(isLoading = true))
        val tokenResource = userRepository.getToken()
        if (tokenResource !is Resource.Success) return@flow emit(
            Resource.Failure(
                error = ResourceError.DefaultError("Must be logged in to do this task!")
            )
        )
        val deleteResource = branchRepository.deleteBranch(
            token = tokenResource.result,
            branchId = branchId,
            restaurantId = restaurantId
        )
        when (deleteResource) {
            is Resource.Success -> emit(Resource.Success(Unit))
            is Resource.Failure -> emit(Resource.Failure(deleteResource.error))
            else -> throw IllegalStateException()
        }
    }
}