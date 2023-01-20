package com.nasportfolio.user

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nasportfolio.domain.comment.usecases.GetCommentsUseCase
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
class UserProfileViewModel @Inject constructor(
    private val getUsersUseCase: GetUsersUseCase,
    private val getCommentsUseCase: GetCommentsUseCase,
    private val getRestaurantsUseCase: GetRestaurantsUseCase,
    private val getCurrentLoggedInUser: GetCurrentLoggedInUser,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow(UserProfileState())
    val state = _state.asStateFlow()

    private val _errorChannel = Channel<String>()
    val errorChannel = _errorChannel.receiveAsFlow()

    init {
        getLoggedInUser()
        savedStateHandle.get<String>("userId")?.let {
            getUser(userId = it)
            getComments(userId = it)
            getFavoriteRestaurants(userId = it)
        }
        savedStateHandle.get<Boolean>("fromNav")?.let {
            _state.update { state -> state.copy(fromNav = it) }
        }
    }

    private fun getUser(userId: String) {
        getUsersUseCase.getById(id = userId).onEach {
            when (it) {
                is Resource.Success -> _state.update { state ->
                    state.copy(
                        isUserLoading = false,
                        isRefreshing = false,
                        user = it.result
                    )
                }
                is Resource.Loading -> _state.update { state ->
                    state.copy(isUserLoading = it.isLoading)
                }
                is Resource.Failure -> {
                    _state.update { state ->
                        state.copy(
                            isUserLoading = false,
                            isRefreshing = false
                        )
                    }
                    if (it.error !is ResourceError.DefaultError) return@onEach
                    val defaultError = it.error as ResourceError.DefaultError
                    _errorChannel.send(defaultError.error)
                }
            }
        }.flowOn(Dispatchers.IO).launchIn(viewModelScope)
    }

    private fun getLoggedInUser() {
        getCurrentLoggedInUser().onEach {
            when (it) {
                is Resource.Success -> _state.update { state ->
                    state.copy(
                        isUserLoading = false,
                        isRefreshing = false,
                        loggedInUserId = it.result.id
                    )
                }
                is Resource.Loading -> _state.update { state ->
                    state.copy(isUserLoading = it.isLoading)
                }
                is Resource.Failure -> {
                    _state.update { state ->
                        state.copy(
                            isUserLoading = false,
                            isRefreshing = false
                        )
                    }
                    if (it.error !is ResourceError.DefaultError) return@onEach
                    val defaultError = it.error as ResourceError.DefaultError
                    _errorChannel.send(defaultError.error)
                }
            }
        }.flowOn(Dispatchers.IO).launchIn(viewModelScope)
    }

    private fun getComments(userId: String) {
        getCommentsUseCase.byUser(userId = userId).onEach {
            when (it) {
                is Resource.Success -> _state.update { state ->
                    state.copy(
                        isCommentLoading = false,
                        isRefreshing = false,
                        comments = it.result.sortedByDescending { it.updatedAt }
                    )
                }
                is Resource.Loading -> _state.update { state ->
                    state.copy(isCommentLoading = it.isLoading)
                }
                is Resource.Failure -> {
                    _state.update { state ->
                        state.copy(
                            isCommentLoading = false,
                            isRefreshing = false
                        )
                    }
                    if (it.error !is ResourceError.DefaultError) return@onEach
                    val defaultError = it.error as ResourceError.DefaultError
                    _errorChannel.send(defaultError.error)
                }
            }
        }.flowOn(Dispatchers.IO).launchIn(viewModelScope)
    }

    private fun getFavoriteRestaurants(userId: String) {
        val filter = GetRestaurantsUseCase.Filter.UserIdInFav(
            userId = userId
        )
        getRestaurantsUseCase(filter = filter).onEach {
            when (it) {
                is Resource.Success -> _state.update { state ->
                    state.copy(
                        isRestaurantLoading = false,
                        isRefreshing = false,
                        favRestaurants = it.result
                    )
                }
                is Resource.Loading -> _state.update { state ->
                    state.copy(isRestaurantLoading = it.isLoading)
                }
                is Resource.Failure -> {
                    _state.update { state ->
                        state.copy(
                            isRestaurantLoading = false,
                            isRefreshing = false
                        )
                    }
                    if (it.error !is ResourceError.DefaultError) return@onEach
                    val defaultError = it.error as ResourceError.DefaultError
                    _errorChannel.send(defaultError.error)
                }
            }
        }.flowOn(Dispatchers.IO).launchIn(viewModelScope)
    }

    fun refresh() {
        _state.update { state -> state.copy(isRefreshing = true) }
        getLoggedInUser()
        _state.value.user?.let {
            getUser(userId = it.id)
            getFavoriteRestaurants(userId = it.id)
            getComments(userId = it.id)
        }
    }

    fun toggleFavorite(restaurantId: String) {
        viewModelScope.launch {
            val index = _state.value.favRestaurants.map { it.id }.indexOf(restaurantId)
            val restaurant = _state.value.favRestaurants[index]
            lateinit var oldState: UserProfileState
            _state.update { state ->
                oldState = state
                state.copy(
                    favRestaurants = state.favRestaurants.toMutableList().apply {
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
}