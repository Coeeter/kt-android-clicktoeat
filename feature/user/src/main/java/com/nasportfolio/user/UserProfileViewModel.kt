package com.nasportfolio.user

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nasportfolio.domain.user.usecases.GetUsersUseCase
import com.nasportfolio.domain.utils.Resource
import com.nasportfolio.domain.utils.ResourceError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val getUsersUseCase: GetUsersUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow(UserProfileState())
    val state = _state.asStateFlow()

    private val _errorChannel = Channel<String>()
    val errorChannel = _errorChannel.receiveAsFlow()

    init {
        savedStateHandle.get<String>("userId")?.let {
            getUser(userId = it)
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
                        isLoading = false,
                        user = it.result
                    )
                }
                is Resource.Loading -> _state.update { state ->
                    state.copy(isLoading = it.isLoading)
                }
                is Resource.Failure -> {
                    _state.update { state -> state.copy(isLoading = false) }
                    if (it.error !is ResourceError.DefaultError) return@onEach
                    val defaultError = it.error as ResourceError.DefaultError
                    _errorChannel.send(defaultError.error)
                }
            }
        }.flowOn(Dispatchers.IO).launchIn(viewModelScope)
    }
}