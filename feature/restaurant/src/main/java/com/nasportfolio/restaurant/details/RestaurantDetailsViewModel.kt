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
import com.nasportfolio.domain.comment.usecases.CreateCommentUseCase
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
    private val createCommentUseCase: CreateCommentUseCase,
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

    private fun toggleFavorite() {
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

    private fun createComment() {
        val restaurant = _state.value.restaurant ?: return
        createCommentUseCase(
            restaurantId = restaurant.id,
            review = _state.value.review,
            rating = _state.value.rating
        ).onEach {
            when (it) {
                is Resource.Loading -> _state.update { state ->
                    state.copy(isSubmitting = it.isLoading)
                }
                is Resource.Success -> _state.update { state ->
                    val newCommentList = restaurant.comments.toMutableList().apply {
                        add(0, it.result)
                    }
                    state.copy(
                        isSubmitting = false,
                        rating = 0,
                        review = "",
                        isUpdated = true,
                        restaurant = restaurant.copy(
                            comments = newCommentList,
                            averageRating = newCommentList.sumOf { it.rating } / newCommentList.size.toDouble(),
                            ratingCount = newCommentList.size,
                        ),
                    )
                }
                is Resource.Failure -> {
                    when (it.error) {
                        is ResourceError.DefaultError -> {
                            _state.update { state ->
                                state.copy(isSubmitting = false)
                            }
                            _errorChannel.send((it.error as ResourceError.DefaultError).error)
                        }
                        is ResourceError.FieldError -> _state.update { state ->
                            val fieldErrors = (it.error as ResourceError.FieldError).errors
                            state.copy(
                                isSubmitting = false,
                                reviewError = fieldErrors.find { it.field == "review" }?.error,
                                ratingError = fieldErrors.find { it.field == "rating" }?.error
                            )
                        }
                    }
                }
            }
        }.flowOn(Dispatchers.IO).launchIn(viewModelScope)
    }

    private fun setAnimationIsDone(isDone: Boolean) {
        _state.update { state ->
            state.copy(isAnimationDone = isDone)
        }
    }

    private fun getRating(newRating: Int, oldRating: Int): Int {
        if (newRating == oldRating && newRating != 1) return newRating - 1
        return newRating
    }

    fun onEvent(event: RestaurantDetailsEvent) {
        when (event) {
            is RestaurantDetailsEvent.AnimationOverEvent -> {
                setAnimationIsDone(isDone = event.isAnimationDone)
            }
            is RestaurantDetailsEvent.ToggleFavoriteEvent -> {
                toggleFavorite()
            }
            is RestaurantDetailsEvent.OnReviewChangedEvent -> _state.update { state ->
                state.copy(
                    review = event.review,
                    reviewError = null
                )
            }
            is RestaurantDetailsEvent.OnRatingChangedEvent -> _state.update { state ->
                state.copy(
                    rating = getRating(
                        newRating = event.rating,
                        oldRating = state.rating
                    ),
                    ratingError = null
                )
            }
            is RestaurantDetailsEvent.OnSubmit -> {
                createComment()
            }
        }
    }
}