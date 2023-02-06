package com.nasportfolio.user.profile

import android.graphics.Bitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nasportfolio.common.components.images.bitmapCache
import com.nasportfolio.domain.comment.usecases.GetCommentsUseCase
import com.nasportfolio.domain.favorites.usecases.ToggleFavoriteUseCase
import com.nasportfolio.domain.likesdislikes.usecases.ToggleLikeDislike
import com.nasportfolio.domain.restaurant.usecases.GetRestaurantsUseCase
import com.nasportfolio.domain.user.usecases.GetCurrentLoggedInUser
import com.nasportfolio.domain.user.usecases.GetUsersUseCase
import com.nasportfolio.domain.user.usecases.UpdateAccountUseCase
import com.nasportfolio.domain.utils.Resource
import com.nasportfolio.domain.utils.ResourceError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val getUsersUseCase: GetUsersUseCase,
    private val getCommentsUseCase: GetCommentsUseCase,
    private val getRestaurantsUseCase: GetRestaurantsUseCase,
    private val getCurrentLoggedInUser: GetCurrentLoggedInUser,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val updateAccountUseCase: UpdateAccountUseCase,
    private val toggleLikeDislike: ToggleLikeDislike,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow(UserProfileState())
    val state = _state.asStateFlow()

    private val _errorChannel = Channel<String>()
    val errorChannel = _errorChannel.receiveAsFlow()

    private val _photoUpdatedChannel = Channel<Boolean>()
    val photoUpdatedChannel = _photoUpdatedChannel.receiveAsFlow()

    init {
        savedStateHandle.get<String>("userId")?.let { userId ->
            _state.update { state -> state.copy(fromNav = userId == "null") }
            getLoggedInUser(currentUser = userId == "null")
            if (userId == "null") return@let
            getUser(userId = userId)
            getComments(userId = userId)
            getFavoriteRestaurants(userId = userId)
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

    private fun getLoggedInUser(currentUser: Boolean) {
        getCurrentLoggedInUser().onEach {
            when (it) {
                is Resource.Success -> _state.update { state ->
                    if (currentUser) {
                        getUser(userId = it.result.id)
                        getComments(userId = it.result.id)
                        getFavoriteRestaurants(userId = it.result.id)
                    }
                    state.copy(
                        isUserLoading = false,
                        isRefreshing = false,
                        currentUser = it.result,
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
        val filter = GetRestaurantsUseCase.Filter.GetUsersFavoriteRestaurants(
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
        getLoggedInUser(currentUser = false)
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
            val isFavorited = restaurant.favoriteUsers
                .map { it.id }
                .contains(_state.value.currentUser?.id)
            lateinit var oldState: UserProfileState
            _state.update { state ->
                oldState = state
                state.copy(
                    favRestaurants = state.favRestaurants.toMutableList().apply {
                        set(
                            index,
                            restaurant.copy(
                                favoriteUsers = restaurant.favoriteUsers.filter {
                                    if (!isFavorited) return@filter true
                                    it.id != state.currentUser?.id
                                }.toMutableList().apply list@{
                                    if (isFavorited) return@list
                                    add(state.currentUser!!)
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

    fun editPhoto(bitmap: Bitmap) {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val byteArray = baos.toByteArray()
        updateAccountUseCase.updateImage(byteArray).onEach {
            when (it) {
                is Resource.Success -> {
                    _state.update { state ->
                        bitmapCache[it.result.image!!.url] = bitmap.asImageBitmap()
                        state.copy(
                            user = it.result,
                            isUserLoading = false
                        )
                    }
                    _photoUpdatedChannel.send(true)
                }
                is Resource.Failure -> {
                    _state.value = _state.value.copy(isUserLoading = false)
                    if (it.error !is ResourceError.DefaultError) return@onEach
                    val defaultError = (it.error as ResourceError.DefaultError).error
                    _errorChannel.send(defaultError)
                }
                is Resource.Loading -> _state.update { state ->
                    state.copy(isUserLoading = it.isLoading)
                }
            }
        }.flowOn(Dispatchers.IO).launchIn(viewModelScope)
    }

    fun deletePhoto() {
        updateAccountUseCase.deleteImage().onEach {
            when (it) {
                is Resource.Success -> {
                    _state.update { state ->
                        bitmapCache.remove(state.user!!.image!!.url)
                        state.copy(
                            user = state.user.copy(
                                image = null
                            ),
                            isUserLoading = false
                        )
                    }
                    _photoUpdatedChannel.send(true)
                }
                is Resource.Failure -> {
                    _state.value = _state.value.copy(isUserLoading = false)
                    if (it.error !is ResourceError.DefaultError) return@onEach
                    val defaultError = (it.error as ResourceError.DefaultError).error
                    _errorChannel.send(defaultError)
                }
                is Resource.Loading -> _state.update { state ->
                    state.copy(isUserLoading = it.isLoading)
                }
            }
        }.flowOn(Dispatchers.IO).launchIn(viewModelScope)
    }

    fun likeComment(index: Int) {
        likeDislikeComment(index = index, action = ToggleLikeDislike.Action.Like)
    }

    fun dislikeComment(index: Int) {
        likeDislikeComment(index = index, action = ToggleLikeDislike.Action.Dislike)
    }

    private fun likeDislikeComment(index: Int, action: ToggleLikeDislike.Action) {
        val comment = _state.value.comments[index]
        toggleLikeDislike(comment = comment, action = action).onEach {
            when (it) {
                is Resource.Success -> _state.update { state ->
                    state.copy(
                        comments = state.comments.toMutableList().apply {
                            set(index, it.result)
                        }
                    )
                }
                is Resource.Failure -> {
                    if (it.error !is ResourceError.DefaultError) return@onEach
                    val defaultError = (it.error as ResourceError.DefaultError).error
                    _errorChannel.send(defaultError)
                }
                is Resource.Loading -> Unit
            }
        }.flowOn(Dispatchers.IO).launchIn(viewModelScope)
    }
}