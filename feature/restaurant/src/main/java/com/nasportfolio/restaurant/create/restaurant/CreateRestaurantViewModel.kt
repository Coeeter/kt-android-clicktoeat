package com.nasportfolio.restaurant.create.restaurant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nasportfolio.domain.restaurant.usecases.CreateRestaurantUseCase
import com.nasportfolio.domain.utils.Resource
import com.nasportfolio.domain.utils.ResourceError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class CreateRestaurantViewModel @Inject constructor(
    private val createRestaurantUseCase: CreateRestaurantUseCase
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
        createRestaurantUseCase(
            name = _state.value.name,
            description = _state.value.description,
            image = _state.value.image
        ).onEach {
            when (it) {
                is Resource.Success -> _state.update { state ->
                    state.copy(
                        isLoading = false,
                        insertId = it.result
                    )
                }
                is Resource.Loading -> _state.update { state ->
                    state.copy(isLoading = it.isLoading)
                }
                is Resource.Failure -> {
                    when (it.error) {
                        is ResourceError.DefaultError -> {
                            _state.update { state -> state.copy(isLoading = false) }
                            _errorChannel.send((it.error as ResourceError.DefaultError).error)
                        }
                        is ResourceError.FieldError -> _state.update { state ->
                            val fieldErrorFields = (it.error as ResourceError.FieldError).errors
                            state.copy(
                                nameError = fieldErrorFields.find { item -> item.field == "name" }?.error,
                                descriptionError = fieldErrorFields.find { item -> item.field == "description" }?.error,
                                imageError = fieldErrorFields.find { item -> item.field == "brandImage" }?.error,
                                isLoading = false
                            )
                        }
                    }
                }
            }
        }.flowOn(Dispatchers.IO).launchIn(viewModelScope)
    }
}