package com.nasportfolio.restaurant.createUpdate.branch

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.nasportfolio.domain.branch.usecases.CreateBranchUseCase
import com.nasportfolio.domain.branch.usecases.GetBranchUseCase
import com.nasportfolio.domain.branch.usecases.UpdateBranchUseCase
import com.nasportfolio.domain.utils.Resource
import com.nasportfolio.domain.utils.ResourceError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class CreateUpdateBranchViewModel @Inject constructor(
    private val createBranchUseCase: CreateBranchUseCase,
    private val getBranchUseCase: GetBranchUseCase,
    private val updateBranchUseCase: UpdateBranchUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow(CreateUpdateBranchState())
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
        savedStateHandle.get<String>("branchId")?.let {
            if (it == "null") return@let
            _state.update { state -> state.copy(isUpdateForm = true) }
            getBranch(branchId = it)
        }
    }

    private fun getBranch(branchId: String) {
        getBranchUseCase.byId(branchId = branchId).onEach {
            when (it) {
                is Resource.Success -> _state.update { state ->
                    state.copy(
                        branchId = it.result.id,
                        latLng = LatLng(
                            it.result.latitude,
                            it.result.longitude
                        ),
                        address = it.result.address,
                    )
                }
                is Resource.Failure -> _errorChannel.send(
                    (it.error as ResourceError.DefaultError).error
                )
                else -> Unit
            }
        }.flowOn(Dispatchers.IO).launchIn(viewModelScope)
    }

    fun onEvent(event: CreateUpdateBranchEvent) {
        when (event) {
            is CreateUpdateBranchEvent.OnAddressChanged -> _state.update { state ->
                state.copy(
                    address = event.address,
                    addressError = null
                )
            }
            is CreateUpdateBranchEvent.OnLocationChanged -> _state.update { state ->
                state.copy(
                    latLng = event.latLng,
                    latLngError = null
                )
            }
            is CreateUpdateBranchEvent.OnSubmit -> onSubmit()
        }
    }

    private fun onSubmit() {
        if (_state.value.isUpdateForm) return updateBranch()
        createBranch()
    }

    private fun updateBranch() {
        updateBranchUseCase(
            restaurantId = _state.value.restaurantId!!,
            address = _state.value.address,
            longitude = _state.value.latLng?.longitude,
            latitude = _state.value.latLng?.latitude,
            branchId = _state.value.branchId!!
        ).onEach { resource ->
            when (resource) {
                is Resource.Loading -> _state.update { state ->
                    state.copy(isLoading = resource.isLoading)
                }
                is Resource.Success -> _state.update { state ->
                    state.copy(
                        isLoading = false,
                        isUpdated = true
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

    private fun createBranch() {
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