package com.nasportfolio.domain.branch.usecases

import com.nasportfolio.domain.branch.Branch
import com.nasportfolio.domain.branch.BranchRepository
import com.nasportfolio.domain.utils.Resource
import com.nasportfolio.domain.utils.ResourceError
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetBranchUseCase @Inject constructor(
    private val branchRepository: BranchRepository
) {
    operator fun invoke() = flow<Resource<List<Branch>>> {
        emit(Resource.Loading(isLoading = true))
        emit(branchRepository.getAllBranches())
    }

    fun byId(branchId: String) = flow<Resource<Branch>> {
        emit(Resource.Loading(isLoading = true))
        when (val branchResources = branchRepository.getAllBranches()) {
            is Resource.Success -> {
                val branch = branchResources.result.find { it.id == branchId } ?: return@flow emit(
                    Resource.Failure(
                        ResourceError.DefaultError(
                            error = "Branch with id $branchId does not exist"
                        )
                    )
                )
                emit(Resource.Success(branch))
            }
            is Resource.Failure -> emit(Resource.Failure(branchResources.error))
            else -> throw IllegalStateException()
        }
    }
}