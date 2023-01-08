package com.nasportfolio.restaurant.details

import android.annotation.SuppressLint
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.nasportfolio.domain.favorites.usecases.ToggleFavoriteUseCase
import com.nasportfolio.domain.restaurant.usecases.GetRestaurantsUseCase
import com.nasportfolio.domain.user.usecases.GetCurrentLoggedInUser
import com.nasportfolio.domain.utils.Resource
import com.nasportfolio.domain.utils.ResourceError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@SuppressLint("MissingPermission")
@HiltViewModel
class RestaurantDetailsViewModel @Inject constructor(
    private val getRestaurantsUseCase: GetRestaurantsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val getCurrentLoggedInUser: GetCurrentLoggedInUser,
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow(RestaurantsDetailState())
    val state = _state.asStateFlow()

    private val _errorChannel = Channel<String>()
    val errorChannel = _errorChannel.receiveAsFlow()

    init {
        savedStateHandle.get<String>("restaurantId")?.let {
            getRestaurant(id = it)
        } ?: runBlocking {
            _state.update { it.copy(shouldNavigateBack = true) }
            _errorChannel.send("Unknown error has occurred. Please try again later")
        }
        getCurrentLocation()
    }

    private fun getCurrentLocation() {
        val task = fusedLocationProviderClient.getCurrentLocation(
            LocationRequest.PRIORITY_HIGH_ACCURACY,
            object : CancellationToken() {
                override fun isCancellationRequested() = false
                override fun onCanceledRequested(p0: OnTokenCanceledListener) =
                    CancellationTokenSource().token
            }
        )
        task.addOnCompleteListener {
            it.exception?.let {
                return@addOnCompleteListener runBlocking {
                    _errorChannel.send(it.message.toString())
                }
            }
            _state.update { state ->
                state.copy(
                    currentLocation = LatLng(
                        it.result.latitude,
                        it.result.longitude
                    )
                )
            }
        }
    }

    private fun getRestaurant(id: String) {
        getRestaurantsUseCase.getById(restaurantId = id).onEach {
            when (it) {
                is Resource.Loading -> _state.update { state ->
                    state.copy(isLoading = it.isLoading)
                }
                is Resource.Success -> _state.update { state ->
                    state.copy(
                        restaurant = it.result,
                        isLoading = false
                    )
                }
                is Resource.Failure -> {
                    _state.update { state -> state.copy(isLoading = false) }
                    if (it.error !is ResourceError.DefaultError) return@onEach
                    _errorChannel.send((it.error as ResourceError.DefaultError).error)
                }
            }
        }.flowOn(Dispatchers.IO).launchIn(viewModelScope)
    }

    fun toggleFavorite() {
        _state.value.restaurant ?: return
        viewModelScope.launch {
            val restaurant = state.value.restaurant!!
            lateinit var oldState: RestaurantsDetailState
            _state.update { state ->
                oldState = state
                state.copy(
                    restaurant = state.restaurant!!.copy(
                        isFavoriteByCurrentUser = !state.restaurant.isFavoriteByCurrentUser,
                        favoriteSize = if (state.restaurant.isFavoriteByCurrentUser) {
                            state.restaurant.favoriteSize - 1
                        } else {
                            state.restaurant.favoriteSize + 1
                        }
                    )
                )
            }
            when (val resource = toggleFavoriteUseCase(restaurant)) {
                is Resource.Failure -> {
                    _state.value = oldState
                    if (resource.error !is ResourceError.DefaultError) return@launch
                    val defaultError = resource.error as ResourceError.DefaultError
                    _errorChannel.send(defaultError.error)
                }
                is Resource.Success -> _state.update { state ->
                    state.copy(isUpdated = true)
                }
                else -> Unit
            }
        }
    }
}