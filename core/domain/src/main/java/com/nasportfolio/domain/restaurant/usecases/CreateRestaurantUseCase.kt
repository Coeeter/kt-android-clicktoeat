package com.nasportfolio.domain.restaurant.usecases

import android.graphics.Bitmap
import com.nasportfolio.domain.restaurant.RestaurantRepository
import com.nasportfolio.domain.user.UserRepository
import com.nasportfolio.domain.utils.Resource
import com.nasportfolio.domain.utils.ResourceError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class CreateRestaurantUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val restaurantRepository: RestaurantRepository
) {
    operator fun invoke(
        name: String,
        description: String,
        image: Bitmap?
    ): Flow<Resource<String>> = flow {
        validate(
            name = name,
            description = description,
            image = image
        )?.let {
            return@flow emit(Resource.Failure(error = it))
        }
        emit(Resource.Loading(isLoading = true))
        val tokenResource = userRepository.getToken()
        if (tokenResource !is Resource.Success) return@flow emit(
            Resource.Failure(
                error = ResourceError.DefaultError("Must be logged in to do this task!")
            )
        )
        val insertIdResource = restaurantRepository.createRestaurant(
            token = tokenResource.result,
            name = name,
            description = description,
            image = convertBitmapToByteArray(bitmap = image!!)
        )
        emit(insertIdResource)
    }

    private suspend fun convertBitmapToByteArray(
        bitmap: Bitmap
    ): ByteArray = coroutineScope {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        baos.toByteArray().also {
            launch(Dispatchers.IO) { baos.close() }
        }
    }

    private fun validate(
        name: String,
        description: String,
        image: Bitmap?
    ): ResourceError.FieldError? {
        var nameError: ResourceError.FieldErrorItem? = null
        var descriptionError: ResourceError.FieldErrorItem? = null
        var imageError: ResourceError.FieldErrorItem? = null
        if (name.isEmpty()) nameError = ResourceError.FieldErrorItem(
            field = "name",
            error = "Name of restaurant required!"
        )
        if (description.isEmpty()) descriptionError = ResourceError.FieldErrorItem(
            field = "description",
            error = "Description of restaurant required!"
        )
        if (image == null) imageError = ResourceError.FieldErrorItem(
            field = "brandImage",
            error = "Brand image of restaurant required!"
        )
        val hasError = listOf(
            nameError,
            descriptionError,
            imageError
        ).any { it != null }
        if (!hasError) return null
        return ResourceError.FieldError(
            message = "Errors in fields provided",
            errors = listOfNotNull(
                nameError,
                descriptionError,
                imageError
            )
        )
    }
}