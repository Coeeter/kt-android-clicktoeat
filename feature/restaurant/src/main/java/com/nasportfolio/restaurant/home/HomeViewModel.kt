package com.nasportfolio.restaurant.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nasportfolio.domain.favorites.usecases.ToggleFavoriteUseCase
import com.nasportfolio.domain.restaurant.usecases.GetRestaurantsUseCase
import com.nasportfolio.domain.user.UserRepository
import com.nasportfolio.domain.user.usecases.GetCurrentLoggedInUser
import com.nasportfolio.domain.utils.Resource
import com.nasportfolio.domain.utils.ResourceError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val getRestaurantsUseCase: GetRestaurantsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val getCurrentLoggedInUser: GetCurrentLoggedInUser
) : ViewModel() {
    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    private val _errorChannel = Channel<String>()
    val errorChannel = _errorChannel.receiveAsFlow()

    init {
        getRestaurants()
        getLoggedInUser()
    }

    fun refreshPage() {
        _state.update { state ->
            state.copy(isRefreshing = true)
        }
        getRestaurants()
    }

    fun toggleFavorite(restaurantId: String) {
        viewModelScope.launch {
            val index = _state.value.restaurantList.map { it.id }.indexOf(restaurantId)
            val restaurant = _state.value.restaurantList[index]
            lateinit var oldState: HomeState
            _state.update { state ->
                oldState = state
                state.copy(
                    restaurantList = state.restaurantList.toMutableList().apply {
                        set(
                            index,
                            restaurant.copy(
                                isFavoriteByCurrentUser = !restaurant.isFavoriteByCurrentUser
                            )
                        )
                    }
                )
            }
            when (val resource = toggleFavoriteUseCase(restaurant)) {
                is Resource.Failure -> {
                    _state.value = oldState
                    if (resource.error !is ResourceError.DefaultError) return@launch
                    val defaultError = resource.error as ResourceError.DefaultError
                    _errorChannel.send(defaultError.error)
                }
                else -> Unit
            }
        }
    }

    private fun getLoggedInUser() {
        getCurrentLoggedInUser().onEach {
            when (it) {
                is Resource.Success -> _state.update { state ->
                    state.copy(currentUserUsername = it.result.username)
                }
                is Resource.Failure -> {
                    if (it.error !is ResourceError.DefaultError) return@onEach
                    val defaultError = it.error as ResourceError.DefaultError
                    _errorChannel.send(defaultError.error)
                }
                else -> Unit
            }
        }.flowOn(Dispatchers.IO).launchIn(viewModelScope)
    }

    private fun getRestaurants() {
        getRestaurantsUseCase().onEach { restaurantResource ->
            when (restaurantResource) {
                is Resource.Success -> _state.update { state ->
                    val restaurantList = restaurantResource.result.sortedBy { it.name }
                    val favRestaurants = restaurantList
                        .filter { it.isFavoriteByCurrentUser }
                        .map { restaurantList.indexOf(it) }
                    val featuredRestaurants = restaurantList
                        .sortedByDescending { it.averageRating }
                        .slice(0 until if (restaurantList.size < 5) restaurantList.size else 5)
                        .map { restaurantList.indexOf(it) }

                    state.copy(
                        restaurantList = restaurantList,
                        isLoading = false,
                        isRefreshing = false,
                        favRestaurants = favRestaurants,
                        featuredRestaurants = featuredRestaurants
                    )
                }
                is Resource.Failure -> {
                    _state.update { state ->
                        state.copy(
                            isLoading = false,
                            isRefreshing = false
                        )
                    }
                    if (restaurantResource.error !is ResourceError.DefaultError) return@onEach
                    val defaultError = restaurantResource.error as ResourceError.DefaultError
                    _errorChannel.send(defaultError.error)
                }
                is Resource.Loading -> _state.update { state ->
                    state.copy(isLoading = restaurantResource.isLoading)
                }
            }
        }.flowOn(Dispatchers.IO).launchIn(viewModelScope)
    }

    fun logout() {
        userRepository.removeToken()
    }
}