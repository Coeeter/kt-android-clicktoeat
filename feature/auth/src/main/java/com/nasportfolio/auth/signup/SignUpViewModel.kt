package com.nasportfolio.auth.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nasportfolio.domain.user.usecases.CreateAccountUseCase
import com.nasportfolio.domain.utils.Resource
import com.nasportfolio.domain.utils.ResourceError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val createAccountUseCase: CreateAccountUseCase
) : ViewModel() {
    private val _errorChannel = Channel<String>()
    val errorChannel = _errorChannel.receiveAsFlow()

    private val _signUpState = MutableStateFlow(SignUpState())
    val signUpState = _signUpState.asStateFlow()

    fun onEvent(event: SignUpEvent) {
        when (event) {
            is SignUpEvent.OnUsernameChange -> _signUpState.update { state ->
                state.copy(
                    username = state.username,
                    usernameError = null
                )
            }
            is SignUpEvent.OnEmailChange -> _signUpState.update { state ->
                state.copy(
                    email = event.email,
                    emailError = null
                )
            }
            is SignUpEvent.OnPasswordChange -> _signUpState.update { state ->
                state.copy(
                    password = event.password,
                    passwordError = null,
                    confirmPasswordError = null
                )
            }
            is SignUpEvent.OnConfirmPasswordChange -> _signUpState.update { state ->
                state.copy(
                    confirmPassword = event.confirmPassword,
                    confirmPasswordError = null
                )
            }
            is SignUpEvent.ProceedNextStage -> _signUpState.update { state ->
                state.copy(
                    signUpStage = SignUpStage.PASSWORD
                )
            }
            is SignUpEvent.ProceedPrevStage -> _signUpState.update { state ->
                state.copy(
                    signUpStage = SignUpStage.NAME
                )
            }
            is SignUpEvent.OnSubmit -> signUp()
        }
    }

    private fun signUp() {
        viewModelScope.launch {
            _signUpState.update { state ->
                state.copy(isLoading = true)
            }
            createAccountUseCase(
                username = _signUpState.value.username,
                email = _signUpState.value.email,
                password = _signUpState.value.password,
                confirmPassword = _signUpState.value.confirmPassword,
                fcmToken = TODO("Need get FCM TOKEN")
            ).onEach {
                when (it) {
                    is Resource.Loading -> _signUpState.update { state ->
                        state.copy(isLoading = it.isLoading)
                    }
                    is Resource.Success -> _signUpState.update { state ->
                        state.copy(
                            isLoading = false,
                            isCreated = true
                        )
                    }
                    is Resource.Failure -> {
                        when (it.error) {
                            is ResourceError.DefaultError -> handleDefaultError(
                                it.error as ResourceError.DefaultError
                            )
                            is ResourceError.FieldError -> handleFieldError(
                                it.error as ResourceError.FieldError
                            )
                        }
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    private suspend fun handleDefaultError(defaultError: ResourceError.DefaultError) {
        _errorChannel.send(defaultError.error)
        _signUpState.update { state ->
            state.copy(isLoading = false)
        }
    }

    private fun handleFieldError(fieldError: ResourceError.FieldError) {
        _signUpState.update { state ->
            state.copy(
                isLoading = false,
                usernameError = fieldError.errors.find { it.field == "username" }?.error,
                emailError = fieldError.errors.find { it.field == "email" }?.error,
                passwordError = fieldError.errors.find { it.field == "password" }?.error,
                confirmPasswordError = fieldError.errors.find { it.field == "confirmPassword" }?.error
            )
        }
    }
}