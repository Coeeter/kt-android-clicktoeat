package com.nasportfolio.restaurant.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

@HiltViewModel
class RestaurantDetailsViewModel @Inject constructor(
    private val getRestaurantsUseCase: GetRestaurantsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val getCurrentLoggedInUser: GetCurrentLoggedInUser,
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