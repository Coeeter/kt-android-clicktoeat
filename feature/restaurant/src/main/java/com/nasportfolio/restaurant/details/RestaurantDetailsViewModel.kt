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
import com.nasportfolio.domain.branch.usecases.DeleteBranchUseCase
import com.nasportfolio.domain.comment.usecases.CreateCommentUseCase
import com.nasportfolio.domain.comment.usecases.DeleteCommentUseCase
import com.nasportfolio.domain.comment.usecases.EditCommentUseCase
import com.nasportfolio.domain.favorites.usecases.ToggleFavoriteUseCase
import com.nasportfolio.domain.likesdislikes.usecases.ToggleLikeDislike
import com.nasportfolio.domain.restaurant.usecases.DeleteRestaurantUseCase
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
    private val createCommentUseCase: CreateCommentUseCase,
    private val editCommentUseCase: EditCommentUseCase,
    private val deleteCommentUseCase: DeleteCommentUseCase,
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    private val deleteRestaurantUseCase: DeleteRestaurantUseCase,
    private val deleteBranchUseCase: DeleteBranchUseCase,
    private val toggleLikeDislike: ToggleLikeDislike,
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
        getCurrentUser()
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

    private fun getCurrentUser() {
        getCurrentLoggedInUser().onEach {
            when (it) {
                is Resource.Success -> _state.update { state ->
                    state.copy(currentUserId = it.result.id)
                }
                is Resource.Failure -> _errorChannel.send(
                    (it.error as ResourceError.DefaultError).error
                )
                else -> Unit
            }
        }.flowOn(Dispatchers.IO).launchIn(viewModelScope)
    }

    private fun getRestaurant(id: String) {
        getRestaurantsUseCase.getById(restaurantId = id).onEach {
            when (it) {
                is Resource.Loading -> _state.update { state ->
                    state.copy(isLoading = it.isLoading)
                }
                is Resource.Success -> _state.update { state ->
                    state.copy(
                        restaurant = it.result.copy(
                            comments = it.result.comments.sortedByDescending { it.createdAt }
                        ),
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
                        oldCommentSize = restaurant.comments.size,
                        restaurant = restaurant.copy(
                            comments = newCommentList
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

    private fun editComment() {
        editCommentUseCase(
            commentId = _state.value.commentBeingEdited!!.id,
            review = _state.value.editingReviewValue,
            rating = _state.value.editingRatingValue
        ).onEach {
            when (it) {
                is Resource.Failure -> {
                    when (it.error) {
                        is ResourceError.DefaultError -> {
                            _state.update { state -> state.copy(isEditSubmitting = false) }
                            val defaultError = it.error as ResourceError.DefaultError
                            _errorChannel.send(defaultError.error)
                        }
                        is ResourceError.FieldError -> _state.update { state ->
                            val fieldError = (it.error as ResourceError.FieldError).errors
                            state.copy(
                                editingRatingError = fieldError.find { it.field == "rating" }?.error,
                                editingReviewError = fieldError.find { it.field == "review" }?.error,
                                isEditSubmitting = false
                            )
                        }
                    }
                }
                is Resource.Loading -> _state.update { state ->
                    state.copy(isEditSubmitting = it.isLoading)
                }
                is Resource.Success -> _state.update { state ->
                    state.copy(
                        commentBeingEdited = null,
                        editingReviewError = null,
                        editingRatingError = null,
                        editingRatingValue = 0,
                        editingReviewValue = "",
                        isEditSubmitting = false,
                        isUpdated = true,
                        restaurant = state.restaurant!!.copy(
                            comments = state.restaurant.comments.toMutableList().apply {
                                val index = map { it.id }.indexOf(state.commentBeingEdited!!.id)
                                set(index, it.result)
                            }
                        )
                    )
                }
            }
        }.flowOn(Dispatchers.IO).launchIn(viewModelScope)
    }

    private fun deleteComment(index: Int) {
        val comment = _state.value.restaurant!!.comments[index]
        lateinit var oldState: RestaurantsDetailState
        _state.update { state ->
            oldState = state
            state.copy(
                oldCommentSize = state.restaurant!!.comments.size,
                restaurant = state.restaurant.copy(
                    comments = state.restaurant.comments.toMutableList().apply {
                        removeAt(index)
                    }
                )
            )
        }
        deleteCommentUseCase(commentId = comment.id).onEach {
            when (it) {
                is Resource.Failure -> {
                    _state.value = oldState
                    if (it.error !is ResourceError.DefaultError) return@onEach
                    val error = (it.error as ResourceError.DefaultError).error
                    _errorChannel.send(error)
                }
                is Resource.Success -> _state.update { state ->
                    state.copy(isUpdated = true)
                }
                else -> Unit
            }
        }.flowOn(Dispatchers.IO).launchIn(viewModelScope)
    }

    private fun deleteRestaurant() {
        _state.value.restaurant?.let {
            deleteRestaurantUseCase(it.id).onEach { deleteResource ->
                when (deleteResource) {
                    is Resource.Success -> _state.update { state ->
                        state.copy(
                            isDeleted = true,
                            isDeleting = false
                        )
                    }
                    is Resource.Loading -> _state.update { state ->
                        state.copy(
                            isDeleting = deleteResource.isLoading
                        )
                    }
                    is Resource.Failure -> {
                        if (deleteResource.error !is ResourceError.DefaultError) return@onEach
                        val error = (deleteResource.error as ResourceError.DefaultError).error
                        _errorChannel.send(error)
                    }
                }
            }.flowOn(Dispatchers.IO).launchIn(viewModelScope)
        }
    }

    private fun deleteBranch(branchId: String) {
        val restaurant = _state.value.restaurant ?: return
        lateinit var oldState: RestaurantsDetailState
        _state.update { state ->
            oldState = state
            state.copy(
                restaurant = restaurant.copy(
                    branches = restaurant.branches.toMutableList().apply {
                        val index = map { it.id }.indexOf(branchId)
                        removeAt(index)
                    }
                )
            )
        }
        deleteBranchUseCase(
            branchId = branchId,
            restaurantId = _state.value.restaurant!!.id
        ).onEach {
            when (it) {
                is Resource.Failure -> {
                    _state.value = oldState
                    if (it.error !is ResourceError.DefaultError) return@onEach
                    val defaultError = it.error as ResourceError.DefaultError
                    _errorChannel.send(defaultError.error)
                }
                else -> Unit
            }
        }.flowOn(Dispatchers.IO).launchIn(viewModelScope)
    }

    private fun likeDislikeComment(index: Int, action: ToggleLikeDislike.Action) {
        val comment = _state.value.restaurant?.comments?.get(index) ?: return
        toggleLikeDislike(comment = comment, action = action).onEach {
            when (it) {
                is Resource.Success -> _state.update { state ->
                    state.copy(
                        restaurant = state.restaurant!!.copy(
                            comments = state.restaurant.comments.toMutableList().apply {
                                set(index, it.result)
                            }
                        )
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
            is RestaurantDetailsEvent.DeleteComment -> {
                deleteComment(index = event.index)
            }
            is RestaurantDetailsEvent.OpenEditCommentDialog -> _state.update { state ->
                val comment = state.restaurant!!.comments[event.index]
                state.copy(
                    commentBeingEdited = comment,
                    editingReviewValue = comment.review,
                    editingRatingValue = comment.rating,
                )
            }
            is RestaurantDetailsEvent.OnEditReview -> _state.update { state ->
                state.copy(
                    editingReviewValue = event.review,
                    editingReviewError = null
                )
            }
            is RestaurantDetailsEvent.OnEditRating -> _state.update { state ->
                state.copy(
                    editingRatingValue = getRating(event.rating, state.editingRatingValue),
                    editingRatingError = null
                )
            }
            is RestaurantDetailsEvent.OnCloseEditCommentDialog -> _state.update { state ->
                state.copy(
                    commentBeingEdited = null,
                    editingReviewError = null,
                    editingRatingError = null,
                    editingRatingValue = 0,
                    editingReviewValue = ""
                )
            }
            is RestaurantDetailsEvent.OnCompleteEdit -> {
                editComment()
            }
            is RestaurantDetailsEvent.DeleteRestaurant -> {
                deleteRestaurant()
            }
            is RestaurantDetailsEvent.DeleteBranch -> {
                deleteBranch(branchId = event.branchId)
            }
            is RestaurantDetailsEvent.LikeComment -> {
                likeDislikeComment(
                    index = event.index,
                    action = ToggleLikeDislike.Action.Like
                )
            }
            is RestaurantDetailsEvent.DislikeComment -> {
                likeDislikeComment(
                    index = event.index,
                    action = ToggleLikeDislike.Action.Dislike
                )
            }
        }
    }
}