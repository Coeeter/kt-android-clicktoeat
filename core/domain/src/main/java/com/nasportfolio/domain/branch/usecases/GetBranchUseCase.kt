package com.nasportfolio.domain.branch.usecases

import com.nasportfolio.domain.branch.Branch
import com.nasportfolio.domain.branch.BranchRepository
import com.nasportfolio.domain.utils.Resource
import com.nasportfolio.domain.utils.ResourceError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class GetBranchUseCase @Inject constructor(
    private val branchRepository: BranchRepository
) {
    operator fun invoke(fetchFromRemote: Boolean = false): Flow<Resource<List<Branch>>> =
        branchRepository.getAllBranches(fetchFromRemote = fetchFromRemote)
            .onStart { emit(Resource.Loading(isLoading = true)) }
            .catch { e ->
                val error = ResourceError.DefaultError(e.message.toString())
                emit(Resource.Failure(error))
            }

    fun byId(branchId: String): Flow<Resource<Branch>> = branchRepository.getAllBranches()
        .onStart { emit(Resource.Loading(isLoading = true)) }
        .map {
            when (it) {
                is Resource.Success -> {
                    val branch = it.result.find { branch -> branch.id == branchId }
                    branch ?: return@map Resource.Failure(
                        ResourceError.DefaultError("Unable to find branch with id $branchId")
                    )
                    Resource.Success(branch)
                }
                is Resource.Loading -> Resource.Loading(it.isLoading)
                is Resource.Failure -> Resource.Failure(it.error)
            }
        }
        .catch { e ->
            val error = ResourceError.DefaultError(e.message.toString())
            emit(Resource.Failure(error))
        }
}