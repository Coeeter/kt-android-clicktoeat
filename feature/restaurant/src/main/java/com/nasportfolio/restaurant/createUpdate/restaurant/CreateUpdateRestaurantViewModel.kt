package com.nasportfolio.restaurant.createUpdate.restaurant

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nasportfolio.common.components.images.bitmapCache
import com.nasportfolio.domain.restaurant.usecases.CreateRestaurantUseCase
import com.nasportfolio.domain.restaurant.usecases.GetRestaurantsUseCase
import com.nasportfolio.domain.restaurant.usecases.UpdateRestaurantUseCase
import com.nasportfolio.domain.utils.Resource
import com.nasportfolio.domain.utils.ResourceError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import java.net.URL
import javax.inject.Inject

@HiltViewModel
class CreateUpdateRestaurantViewModel @Inject constructor(
    private val createRestaurantUseCase: CreateRestaurantUseCase,
    private val updateRestaurantUseCase: UpdateRestaurantUseCase,
    private val getRestaurantsUseCase: GetRestaurantsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow(CreateUpdateRestaurantState())
    val state = _state.asStateFlow()

    private val _errorChannel = Channel<String>()
    val errorChannel = _errorChannel.receiveAsFlow()

    init {
        savedStateHandle.get<String>("restaurantId")?.let {
            if (it == "null") return@let
            _state.update { state -> state.copy(isUpdateForm = true) }
            getRestaurant(restaurantId = it)
        }
    }

    private fun getRestaurant(restaurantId: String) {
        getRestaurantsUseCase.getById(restaurantId).onEach {
            when (it) {
                is Resource.Success -> _state.update { state ->
                    state.copy(
                        insertId = it.result.id,
                        name = it.result.name,
                        description = it.result.description,
                        image = coroutineScope {
                            bitmapCache[it.result.imageUrl]?.asAndroidBitmap() ?: withContext(
                                Dispatchers.IO
                            ) {
                                val stream = URL(it.result.imageUrl)
                                    .openConnection()
                                    .getInputStream()
                                BitmapFactory.decodeStream(stream)
                            }
                        }
                    )
                }
                is Resource.Failure -> _errorChannel.send(
                    (it.error as ResourceError.DefaultError).error
                )
                else -> Unit
            }
        }.flowOn(Dispatchers.IO).launchIn(viewModelScope)
    }

    fun onEvent(event: CreateUpdateRestaurantEvent) {
        when (event) {
            is CreateUpdateRestaurantEvent.OnNameChanged -> _state.update { state ->
                state.copy(
                    name = event.name,
                    nameError = null
                )
            }
            is CreateUpdateRestaurantEvent.OnDescriptionChanged -> _state.update { state ->
                state.copy(
                    description = event.description,
                    descriptionError = null
                )
            }
            is CreateUpdateRestaurantEvent.OnImageChanged -> _state.update { state ->
                state.copy(
                    image = event.image,
                    imageError = null
                )
            }
            is CreateUpdateRestaurantEvent.OnSubmit -> onSubmit()
        }
    }

    private fun onSubmit() {
        if (!_state.value.isUpdateForm) return createRestaurant()
        updateRestaurant()
    }

    private fun updateRestaurant() {
        _state.value.insertId?.let {
            updateRestaurantUseCase(
                restaurantId = it,
                name = _state.value.name,
                description = _state.value.description,
                image = _state.value.image
            ).onEach {
                when (it) {
                    is Resource.Success -> _state.update { state ->
                        state.copy(
                            isLoading = false,
                            isUpdateComplete = true,
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

    private fun createRestaurant() {
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