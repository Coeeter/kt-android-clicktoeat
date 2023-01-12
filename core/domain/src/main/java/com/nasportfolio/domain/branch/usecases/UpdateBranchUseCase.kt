package com.nasportfolio.domain.branch.usecases

import com.nasportfolio.domain.utils.Resource
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UpdateBranchUseCase @Inject constructor(
    private val createBranchUseCase: CreateBranchUseCase,
    private val deleteBranchUseCase: DeleteBranchUseCase
) {
    operator fun invoke(
        branchId: String,
        restaurantId: String,
        address: String,
        latitude: Double?,
        longitude: Double?,
    ) = flow<Resource<Unit>> {
        createBranchUseCase.validate(
            address = address,
            latitude = latitude,
            longitude = longitude
        )?.let {
            return@flow emit(
                Resource.Failure(error = it)
            )
        }
        emitAll(
            createBranchUseCase(
                restaurantId = restaurantId,
                address = address,
                latitude = latitude,
                longitude = longitude
            )
        )
        emitAll(
            deleteBranchUseCase(
                branchId = branchId,
                restaurantId = restaurantId
            )
        )
    }
}