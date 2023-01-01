package com.nasportfolio.restaurant.create

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nasportfolio.domain.branch.BranchRepository
import com.nasportfolio.domain.restaurant.RestaurantRepository
import com.nasportfolio.domain.user.UserRepository
import com.nasportfolio.domain.utils.Resource
import com.nasportfolio.domain.utils.ResourceError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@HiltViewModel
class CreateRestaurantViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val restaurantRepository: RestaurantRepository,
    private val branchRepository: BranchRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(CreateRestaurantState())
    val state = _state.asStateFlow()

    private val _errorChannel = Channel<String>()
    val errorChannel = _errorChannel.receiveAsFlow()

    fun onEvent(event: CreateRestaurantEvent) {
        when (event) {
            is CreateRestaurantEvent.OnNameChanged -> _state.update { state ->
                state.copy(
                    name = event.name,
                    nameError = null
                )
            }
            is CreateRestaurantEvent.OnDescriptionChanged -> _state.update { state ->
                state.copy(
                    description = event.description,
                    descriptionError = null
                )
            }
            is CreateRestaurantEvent.OnImageChanged -> _state.update { state ->
                state.copy(
                    image = event.image,
                    imageError = null
                )
            }
            is CreateRestaurantEvent.OnSubmit -> onSubmit()
        }
    }

    private fun onSubmit() {
        viewModelScope.launch {
            if (_state.value.image == null) _state.update { state ->
                state.copy(imageError = "Image required!")
            }
            if (_state.value.name.isEmpty()) _state.update { state ->
                state.copy(nameError = "Name required!")
            }
            if (_state.value.description.isEmpty()) _state.update { state ->
                state.copy(descriptionError = "Description required!")
            }
            val hasError = listOf(
                _state.value.nameError,
                _state.value.descriptionError,
                _state.value.imageError
            ).any { it != null }
            if (hasError) return@launch
            _state.update { state ->
                state.copy(isLoading = true)
            }
            val deferredByteArray = async {
                convertBitmapToByteArray(_state.value.image!!)
            }
            val tokenResource = userRepository.getToken()
            if (tokenResource !is Resource.Success) return@launch _errorChannel.send(
                "Must be logged in to do this task"
            )
            val result = restaurantRepository.createRestaurant(
                tokenResource.result,
                _state.value.name,
                _state.value.description,
                deferredByteArray.await()
            )
            if (result is Resource.Success) return@launch _state.update { state ->
                state.copy(
                    isLoading = false,
                    isCreated = true
                )
            }
            when ((result as Resource.Failure).error) {
                is ResourceError.DefaultError -> {
                    _state.update { state ->
                        state.copy(isLoading = false)
                    }
                    _errorChannel.send(
                        (result.error as ResourceError.DefaultError).error
                    )
                }
                is ResourceError.FieldError -> {
                    _state.update { state ->
                        val fieldError = result.error as ResourceError.FieldError
                        state.copy(
                            nameError = fieldError.errors.find { it.field == "name" }?.error,
                            descriptionError = fieldError.errors.find { it.field == "description" }?.error,
                            imageError = fieldError.errors.find { it.field == "brandImage" }?.error,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    private suspend fun convertBitmapToByteArray(bitmap: Bitmap) = coroutineScope {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.toByteArray().also {
            launch(Dispatchers.IO) { outputStream.close() }
        }
    }
}