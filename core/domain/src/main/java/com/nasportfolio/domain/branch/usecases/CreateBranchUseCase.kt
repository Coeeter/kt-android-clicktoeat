package com.nasportfolio.domain.branch.usecases

import com.nasportfolio.domain.branch.BranchRepository
import com.nasportfolio.domain.user.UserRepository
import com.nasportfolio.domain.utils.Resource
import com.nasportfolio.domain.utils.ResourceError
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CreateBranchUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val branchRepository: BranchRepository,
) {
    operator fun invoke(
        restaurantId: String,
        address: String,
        latitude: Double?,
        longitude: Double?,
    ) = flow<Resource<Unit>> {
        validate(
            address = address,
            latitude = latitude,
            longitude = longitude
        )?.let {
            return@flow emit(
                Resource.Failure(error = it)
            )
        }
        emit(Resource.Loading(isLoading = true))
        val tokenResource = userRepository.getToken()
        if (tokenResource !is Resource.Success) return@flow emit(
            Resource.Failure(
                error = ResourceError.DefaultError("Must be logged in to do this task!")
            )
        )
        val createResult = branchRepository.createBranch(
            token = tokenResource.result,
            address = address,
            latitude = latitude!!,
            longitude = longitude!!,
            restaurantId = restaurantId
        )
        if (createResult !is Resource.Success) return@flow emit(
            Resource.Failure<Unit>(
                (createResult as Resource.Failure).error
            )
        )
        return@flow emit(Resource.Success(Unit))
    }

    private fun validate(
        address: String,
        latitude: Double?,
        longitude: Double?
    ): ResourceError.FieldError? {
        val fieldError = ResourceError.FieldError(
            message = "Errors in fields provided",
            errors = emptyList()
        )
        if (address.isEmpty()) fieldError.errors.toMutableList().add(
            ResourceError.FieldErrorItem(
                field = "address",
                error = "Address required!"
            )
        )
        if (latitude == null || longitude == null) fieldError.errors.toMutableList().add(
            ResourceError.FieldErrorItem(
                field = "mapError",
                error = "Please select the location of the restaurant"
            )
        )
        if (fieldError.errors.isEmpty()) return null
        return fieldError
    }
}