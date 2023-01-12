package com.nasportfolio.domain.restaurant.usecases

import com.nasportfolio.domain.restaurant.RestaurantRepository
import com.nasportfolio.domain.user.UserRepository
import com.nasportfolio.domain.utils.Resource
import com.nasportfolio.domain.utils.ResourceError
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DeleteRestaurantUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val restaurantRepository: RestaurantRepository
) {
    operator fun invoke(restaurantId: String) = flow<Resource<Unit>> {
        emit(Resource.Loading(isLoading = true))
        val tokenResource = userRepository.getToken()
        if (tokenResource !is Resource.Success) return@flow emit(
            Resource.Failure(
                error = ResourceError.DefaultError("Must be logged in to do this task!")
            )
        )
        val deleteResource = restaurantRepository.deleteRestaurant(
            token = tokenResource.result,
            restaurantId = restaurantId
        )
        when (deleteResource) {
            is Resource.Success -> emit(Resource.Success(Unit))
            is Resource.Failure -> emit(Resource.Failure(deleteResource.error))
            else -> throw IllegalStateException()
        }
    }
}