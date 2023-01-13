package com.nasportfolio.restaurant.comments

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nasportfolio.domain.comment.usecases.CreateCommentUseCase
import com.nasportfolio.domain.comment.usecases.DeleteCommentUseCase
import com.nasportfolio.domain.comment.usecases.EditCommentUseCase
import com.nasportfolio.domain.comment.usecases.GetCommentsUseCase
import com.nasportfolio.domain.user.usecases.GetCurrentLoggedInUser
import com.nasportfolio.domain.utils.Resource
import com.nasportfolio.domain.utils.ResourceError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(
    private val getCommentsUseCase: GetCommentsUseCase,
    private val createCommentUseCase: CreateCommentUseCase,
    private val editCommentUseCase: EditCommentUseCase,
    private val deleteCommentUseCase: DeleteCommentUseCase,
    private val getCurrentLoggedInUser: GetCurrentLoggedInUser,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow(CommentScreenState())
    val state = _state.asStateFlow()

    private val _errorChannel = Channel<String>()
    val errorChannel = _errorChannel.receiveAsFlow()

    init {
        savedStateHandle.get<String>("restaurantId")?.let {
            _state.update { state ->
                state.copy(restaurantId = it)
            }
            getComments()
        }
        getCurrentUser()
    }

    private fun getComments() {
        getCommentsUseCase(
            restaurantId = _state.value.restaurantId!!
        ).onEach {
            when (it) {
                is Resource.Success -> _state.update { state ->
                    state.copy(
                        comments = it.result,
                        isLoading = false
                    )
                }
                is Resource.Loading -> _state.update { state ->
                    state.copy(isLoading = it.isLoading)
                }
                is Resource.Failure -> {
                    _state.update { state -> state.copy(isLoading = false) }
                    val defaultError = it.error as ResourceError.DefaultError
                    _errorChannel.send(defaultError.error)
                }
            }
        }.flowOn(Dispatchers.IO).launchIn(viewModelScope)
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

    private fun getRating(newRating: Int, oldRating: Int): Int {
        if (newRating == oldRating && newRating != 1) return newRating - 1
        return newRating
    }

    private fun createComment() {
        val restaurantId = _state.value.restaurantId ?: return
        createCommentUseCase(
            restaurantId = restaurantId,
            review = _state.value.review,
            rating = _state.value.rating
        ).onEach {
            when (it) {
                is Resource.Loading -> _state.update { state ->
                    state.copy(isCreating = it.isLoading)
                }
                is Resource.Success -> _state.update { state ->
                    state.copy(
                        isCreating = false,
                        rating = 0,
                        review = "",
                        isUpdated = true,
                        comments = state.comments.toMutableList().apply {
                            add(0, it.result)
                        }
                    )
                }
                is Resource.Failure -> {
                    when (it.error) {
                        is ResourceError.DefaultError -> {
                            _state.update { state ->
                                state.copy(isCreating = false)
                            }
                            _errorChannel.send((it.error as ResourceError.DefaultError).error)
                        }
                        is ResourceError.FieldError -> _state.update { state ->
                            val fieldErrors = (it.error as ResourceError.FieldError).errors
                            state.copy(
                                isCreating = false,
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
            review = _state.value.editingReview,
            rating = _state.value.editingRating
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
                        editingRating = 0,
                        editingReview = "",
                        isEditSubmitting = false,
                        isUpdated = true,
                        comments = state.comments.toMutableList().apply {
                            val index = map { it.id }.indexOf(state.commentBeingEdited!!.id)
                            set(index, it.result)
                        }
                    )
                }
            }
        }.flowOn(Dispatchers.IO).launchIn(viewModelScope)
    }

    private fun deleteComment(index: Int) {
        val comment = _state.value.comments[index]
        lateinit var oldState: CommentScreenState
        _state.update { state ->
            oldState = state
            state.copy(
                comments = state.comments.toMutableList().apply {
                    removeAt(index)
                }
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

    fun onEvent(event: CommentsScreenEvent) {
        when (event) {
            is CommentsScreenEvent.OnReviewChangedEvent -> _state.update { state ->
                state.copy(
                    review = event.review,
                    reviewError = null
                )
            }
            is CommentsScreenEvent.OnRatingChangedEvent -> _state.update { state ->
                state.copy(
                    rating = getRating(
                        newRating = event.rating,
                        oldRating = state.rating
                    ),
                    ratingError = null
                )
            }
            is CommentsScreenEvent.OnCreate -> {
                createComment()
            }
            is CommentsScreenEvent.OpenEditCommentDialog -> _state.update { state ->
                val comment = state.comments[event.index]
                state.copy(
                    commentBeingEdited = comment,
                    editingReview = comment.review,
                    editingRating = comment.rating
                )
            }
            is CommentsScreenEvent.OnCloseEditCommentDialog -> _state.update { state ->
                state.copy(
                    commentBeingEdited = null,
                    editingReview = "",
                    editingReviewError = null,
                    editingRating = 0,
                    editingRatingError = null,
                )
            }
            is CommentsScreenEvent.OnEditRating -> _state.update { state ->
                state.copy(
                    editingRating = getRating(
                        newRating = event.rating,
                        oldRating = state.editingRating,
                    ),
                    editingRatingError = null
                )
            }
            is CommentsScreenEvent.OnEditReview -> _state.update { state ->
                state.copy(
                    editingReview = event.review,
                    editingReviewError = null
                )
            }
            is CommentsScreenEvent.OnCompleteEdit -> {
                editComment()
            }
            is CommentsScreenEvent.OnDeleteComment -> {
                deleteComment(index = event.index)
            }
            is CommentsScreenEvent.RefreshPage -> {
                getComments()
                getCurrentUser()
            }
        }
    }
}