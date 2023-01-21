package com.nasportfolio.user.delete

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nasportfolio.domain.user.usecases.DeleteAccountUseCase
import com.nasportfolio.domain.utils.Resource
import com.nasportfolio.domain.utils.ResourceError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class DeleteAccountViewModel @Inject constructor(
    private val deleteAccountUseCase: DeleteAccountUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(DeleteAccountState())
    val state = _state.asStateFlow()

    private val _errorChannel = Channel<String>()
    val errorChannel = _errorChannel.receiveAsFlow()

    fun onPasswordChanged(password: String) {
        _state.update { state ->
            state.copy(
                password = password,
                passwordError = null
            )
        }
    }

    fun submit() {
        deleteAccountUseCase(password = _state.value.password).onEach {
            when (it) {
                is Resource.Success -> _state.update { state ->
                    state.copy(
                        isLoading = false,
                        isDeleted = true
                    )
                }
                is Resource.Failure -> {
                    _state.value = _state.value.copy(isLoading = false)
                    when (it.error) {
                        is ResourceError.DefaultError -> _errorChannel.send(
                            (it.error as ResourceError.DefaultError).error
                        )
                        is ResourceError.FieldError -> {
                            val fieldErrors = (it.error as ResourceError.FieldError).errors
                            _state.update { state ->
                                state.copy(passwordError = fieldErrors[0].error)
                            }
                        }
                    }
                }
                is Resource.Loading -> _state.update { state ->
                    state.copy(isLoading = it.isLoading)
                }
            }
        }.flowOn(Dispatchers.IO).launchIn(viewModelScope)
    }
}