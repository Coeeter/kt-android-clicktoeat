package com.nasportfolio.restaurant.create.branch

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nasportfolio.domain.branch.usecases.CreateBranchUseCase
import com.nasportfolio.domain.utils.Resource
import com.nasportfolio.domain.utils.ResourceError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class CreateBranchViewModel @Inject constructor(
    private val createBranchUseCase: CreateBranchUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow(CreateBranchState())
    val state = _state.asStateFlow()

    private val _errorChannel = Channel<String>()
    val errorChannel = _errorChannel.receiveAsFlow()

    init {
        savedStateHandle.get<String>("restaurantId")?.let {
            _state.update { state -> state.copy(restaurantId = it) }
        } ?: runBlocking {
            _state.update { state -> state.copy(isError = true) }
            _errorChannel.send(
                "Unknown error has occurred. Please try again later"
            )
        }
    }

    fun onEvent(event: CreateBranchEvent) {
        when (event) {
            is CreateBranchEvent.OnAddressChanged -> _state.update { state ->
                state.copy(
                    address = event.address,
                    addressError = null
                )
            }
            is CreateBranchEvent.OnLocationChanged -> _state.update { state ->
                state.copy(
                    latLng = event.latLng,
                    latLngError = null
                )
            }
            is CreateBranchEvent.OnSubmit -> onSubmit()
        }
    }

    private fun onSubmit() {
        createBranchUseCase(
            restaurantId = _state.value.restaurantId!!,
            address = _state.value.address,
            longitude = _state.value.latLng?.longitude,
            latitude = _state.value.latLng?.latitude,
        ).onEach { resource ->
            when (resource) {
                is Resource.Loading -> _state.update { state ->
                    state.copy(isLoading = resource.isLoading)
                }
                is Resource.Success -> _state.update { state ->
                    state.copy(
                        isLoading = false,
                        isCreated = true
                    )
                }
                is Resource.Failure -> {
                    when (resource.error) {
                        is ResourceError.DefaultError -> {
                            _state.update { state -> state.copy(isLoading = false) }
                            _errorChannel.send(
                                (resource.error as ResourceError.DefaultError).error
                            )
                        }
                        is ResourceError.FieldError -> _state.update { state ->
                            val fieldErrors = (resource.error as ResourceError.FieldError).errors
                            state.copy(
                                isLoading = false,
                                addressError = fieldErrors.find { it.field == "address" }?.error,
                                latLngError = fieldErrors.find { it.field == "mapError" }?.error
                            )
                        }
                    }
                }
            }
        }.flowOn(Dispatchers.IO).launchIn(viewModelScope)
    }
}