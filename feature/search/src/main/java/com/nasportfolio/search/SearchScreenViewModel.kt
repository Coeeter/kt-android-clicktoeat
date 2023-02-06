package com.nasportfolio.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nasportfolio.domain.favorites.usecases.ToggleFavoriteUseCase
import com.nasportfolio.domain.restaurant.usecases.GetRestaurantsUseCase
import com.nasportfolio.domain.user.usecases.GetCurrentLoggedInUser
import com.nasportfolio.domain.user.usecases.GetUsersUseCase
import com.nasportfolio.domain.utils.Resource
import com.nasportfolio.domain.utils.ResourceError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchScreenViewModel @Inject constructor(
    private val getRestaurantsUseCase: GetRestaurantsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val getCurrentLoggedInUser: GetCurrentLoggedInUser,
    private val getUsersUseCase: GetUsersUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(SearchScreenState())
    val state = _state.asStateFlow()

    private val _errorChannel = Channel<String>()
    val errorChannel = _errorChannel.receiveAsFlow()

    init {
        getRestaurants()
        getUsers()
        getCurrentUser()
    }

    private fun getCurrentUser() {
        getCurrentLoggedInUser().onEach {
            when (it) {
                is Resource.Success -> _state.update { state ->
                    state.copy(
                        currentLoggedInUser = it.result
                    )
                }
                is Resource.Failure -> {
                    if (it.error !is ResourceError.DefaultError) return@onEach
                    val defaultError = it.error as ResourceError.DefaultError
                    _errorChannel.send(defaultError.error)
                }
                is Resource.Loading -> _state.update { state ->
                    state.copy(isRestaurantLoading = it.isLoading)
                }
            }
        }
    }

    private fun getRestaurants() {
        getRestaurantsUseCase().onEach {
            when (it) {
                is Resource.Success -> _state.update { state ->
                    state.copy(
                        isRefreshing = false,
                        isRestaurantLoading = false,
                        restaurants = it.result,
                        query = ""
                    )
                }
                is Resource.Failure -> {
                    _state.value = _state.value.copy(
                        isRefreshing = false,
                        isRestaurantLoading = false
                    )
                    if (it.error !is ResourceError.DefaultError) return@onEach
                    val defaultError = it.error as ResourceError.DefaultError
                    _errorChannel.send(defaultError.error)
                }
                is Resource.Loading -> _state.update { state ->
                    state.copy(isRestaurantLoading = it.isLoading)
                }
            }
        }.flowOn(Dispatchers.IO).launchIn(viewModelScope)
    }

    private fun getUsers() {
        getUsersUseCase().onEach {
            when (it) {
                is Resource.Success -> _state.update { state ->
                    state.copy(
                        isRefreshing = false,
                        isUserLoading = false,
                        users = it.result,
                        query = ""
                    )
                }
                is Resource.Failure -> {
                    _state.value = _state.value.copy(
                        isRefreshing = false,
                        isUserLoading = false
                    )
                    if (it.error !is ResourceError.DefaultError) return@onEach
                    val defaultError = it.error as ResourceError.DefaultError
                    _errorChannel.send(defaultError.error)
                }
                is Resource.Loading -> _state.update { state ->
                    state.copy(isUserLoading = it.isLoading)
                }
            }
        }.flowOn(Dispatchers.IO).launchIn(viewModelScope)
    }

    fun toggleFavorite(restaurantId: String) {
        viewModelScope.launch {
            val index = _state.value.restaurants.map { it.id }.indexOf(restaurantId)
            val restaurant = _state.value.restaurants[index]
            val isFavorited = restaurant.favoriteUsers
                .map { it.id }
                .contains(_state.value.currentLoggedInUser?.id)
            lateinit var oldState: SearchScreenState
            _state.update { state ->
                oldState = state
                state.copy(
                    restaurants = state.restaurants.toMutableList().apply {
                        set(
                            index,
                            restaurant.copy(
                                favoriteUsers = restaurant.favoriteUsers.filter {
                                    if (!isFavorited) return@filter true
                                    it.id != state.currentLoggedInUser?.id
                                }.toMutableList().apply list@{
                                    if (isFavorited) return@list
                                    add(state.currentLoggedInUser!!)
                                }
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

    fun onQuery(query: String) {
        _state.update { state ->
            state.copy(query = query)
        }
    }

    fun clearQuery() {
        _state.update { state ->
            state.copy(query = "")
        }
    }

    fun refresh() {
        _state.value = _state.value.copy(isRefreshing = true)
        getRestaurants()
        getUsers()
        getCurrentUser()
    }
}