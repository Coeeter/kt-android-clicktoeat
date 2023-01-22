package com.nasportfolio.restaurant.likedislike

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nasportfolio.domain.comment.usecases.GetCommentsUseCase
import com.nasportfolio.domain.utils.Resource
import com.nasportfolio.domain.utils.ResourceError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class LikeDislikeViewModel @Inject constructor(
    private val getCommentsUseCase: GetCommentsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow(LikeDislikeState())
    val state = _state.asStateFlow()

    private val _errorChannel = Channel<String>()
    val errorChannel = _errorChannel.receiveAsFlow()

    init {
        savedStateHandle.get<String>("commentId")?.let {
            getComment(commentId = it)
        }
        savedStateHandle.get<Int>("index")?.let {
            _state.value = _state.value.copy(initialIndex = it)
        }
    }

    private fun getComment(commentId: String) {
        getCommentsUseCase.byId(commentId = commentId).onEach {
            when (it) {
                is Resource.Success -> _state.update { state ->
                    state.copy(
                        isLoading = false,
                        comment = it.result
                    )
                }
                is Resource.Failure -> {
                    if (it.error !is ResourceError.DefaultError) return@onEach
                    val defaultError = (it.error as ResourceError.DefaultError).error
                    _errorChannel.send(defaultError)
                }
                is Resource.Loading -> _state.update { state ->
                    state.copy(isLoading = it.isLoading)
                }
            }
        }.flowOn(Dispatchers.IO).launchIn(viewModelScope)
    }

    fun refresh() {
        _state.value.comment?.let {
            getComment(commentId = it.id)
        }
    }
}