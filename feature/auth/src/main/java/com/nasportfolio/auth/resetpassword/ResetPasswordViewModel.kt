package com.nasportfolio.auth.resetpassword

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nasportfolio.domain.user.UserRepository
import com.nasportfolio.domain.user.usecases.ForgotPasswordUseCase
import com.nasportfolio.domain.utils.Resource
import com.nasportfolio.domain.utils.ResourceError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val forgotPasswordUseCase: ForgotPasswordUseCase,
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow(ResetPasswordState())
    val state = _state.asStateFlow()

    private val _errorChannel = Channel<String>()
    val errorChannel = _errorChannel.receiveAsFlow()

    init {
        savedStateHandle.get<String>("email")?.let { email ->
            savedStateHandle.get<String>("credential")?.let { credential ->
                getToken(
                    email = email,
                    credential = credential
                )
            }
        }
    }

    private fun getToken(email: String, credential: String) {
        viewModelScope.launch {
            _state.update { state -> state.copy(isLoading = true) }
            val tokenResource = userRepository.validateCredential(
                tokenizedEmail = email,
                credential = credential
            )
            when (tokenResource) {
                is Resource.Success -> _state.update { state ->
                    state.copy(
                        isLoading = false,
                        token = tokenResource.result
                    )
                }
                is Resource.Failure -> _state.update { state ->
                    state.copy(isLoading = false)
                }
                else -> throw IllegalStateException()
            }
        }
    }

    private fun submit() {
        val token = _state.value.token ?: return
        forgotPasswordUseCase.resetPassword(
            token = token,
            password = _state.value.password,
            confirmPassword = _state.value.confirmPassword
        ).onEach {
            when (it) {
                is Resource.Success -> _state.update { state ->
                    state.copy(
                        isSubmitting = false,
                        isUpdated = true,
                        password = "",
                        confirmPassword = ""
                    )
                }
                is Resource.Failure -> {
                    _state.value = _state.value.copy(isSubmitting = false)
                    when (it.error) {
                        is ResourceError.DefaultError -> _errorChannel.send(
                            (it.error as ResourceError.DefaultError).error
                        )
                        is ResourceError.FieldError -> _state.update { state ->
                            val fieldErrors = (it.error as ResourceError.FieldError).errors
                            state.copy(
                                passwordError = fieldErrors.find { it.field == "password" || it.field == "confirmPassword" }?.error,
                                confirmPasswordError = fieldErrors.find { it.field == "confirmPassword" }?.error
                            )
                        }
                    }
                }
                is Resource.Loading -> _state.update { state ->
                    state.copy(isSubmitting = it.isLoading)
                }
            }
        }.flowOn(Dispatchers.IO).launchIn(viewModelScope)
    }

    fun onEvent(event: ResetPasswordEvent) {
        when (event) {
            is ResetPasswordEvent.OnPasswordChanged -> _state.update { state ->
                state.copy(
                    password = event.password,
                    passwordError = null,
                    confirmPasswordError = null
                )
            }
            is ResetPasswordEvent.OnConfirmPasswordChanged -> _state.update { state ->
                state.copy(
                    confirmPassword = event.confirmPassword,
                    confirmPasswordError = null
                )
            }
            is ResetPasswordEvent.OnSubmit -> submit()
        }
    }
}