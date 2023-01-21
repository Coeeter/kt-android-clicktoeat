package com.nasportfolio.auth.forgotPassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nasportfolio.domain.user.usecases.ForgotPasswordUseCase
import com.nasportfolio.domain.utils.Resource
import com.nasportfolio.domain.utils.ResourceError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val forgotPasswordUseCase: ForgotPasswordUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(ForgotPasswordState())
    val state = _state.asStateFlow()

    private val _snackbarChannel = Channel<String>()
    val snackbarChannel = _snackbarChannel.receiveAsFlow()

    fun onEmailChanged(email: String) {
        _state.update { state ->
            state.copy(
                email = email,
                emailError = null
            )
        }
    }

    fun submit() {
        forgotPasswordUseCase(email = _state.value.email).onEach {
            when (it) {
                is Resource.Success -> {
                    _state.update { state -> state.copy(isLoading = false, email = "") }
                    _snackbarChannel.send("Successfully sent password reset link to email provided if it has an account attached to it")
                }
                is Resource.Failure -> {
                    _state.update { state -> state.copy(isLoading = false) }
                    when (it.error) {
                        is ResourceError.DefaultError -> _snackbarChannel.send(
                            (it.error as ResourceError.DefaultError).error
                        )
                        is ResourceError.FieldError -> {
                            val fieldError = (it.error as ResourceError.FieldError).errors
                            _state.update { state ->
                                state.copy(
                                    emailError = fieldError.find { it.field == "email" }?.error
                                )
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