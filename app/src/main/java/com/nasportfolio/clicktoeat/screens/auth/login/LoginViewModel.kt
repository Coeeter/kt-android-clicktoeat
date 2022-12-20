package com.nasportfolio.clicktoeat.screens.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nasportfolio.domain.user.usecases.LoginUseCase
import com.nasportfolio.domain.utils.Resource
import com.nasportfolio.domain.utils.ResourceError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
) : ViewModel() {
    private val _errorChannel = Channel<String>()
    val errorChannel = _errorChannel.receiveAsFlow()

    private val _loginState = MutableStateFlow(LoginState())
    val loginState = _loginState.asStateFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.OnEmailChange -> {
                _loginState.update { state ->
                    state.copy(
                        email = event.email,
                        emailError = null
                    )
                }
            }
            is LoginEvent.OnPasswordChange -> {
                _loginState.update { state ->
                    state.copy(
                        password = event.password,
                        passwordError = null
                    )
                }
            }
            is LoginEvent.OnSubmit -> login()
        }
    }

    private fun login() {
        val email = _loginState.value.email
        val password = _loginState.value.password
        loginUseCase(
            email = email,
            password = password,
        ).onEach { result ->
            when (result) {
                is Resource.Success -> _loginState.update { state ->
                    state.copy(
                        isLoading = false,
                        isLoggedIn = true
                    )
                }
                is Resource.Failure -> {
                    when (result.error) {
                        is ResourceError.FieldError -> handleFieldError(result)
                        is ResourceError.DefaultError -> handleDefaultError(result)
                    }
                }
                is Resource.Loading -> _loginState.update { state ->
                    state.copy(isLoading = result.isLoading)
                }
            }
        }.launchIn(viewModelScope)
    }

    private suspend fun handleDefaultError(result: Resource.Failure<Unit>) {
        val error = (result.error as ResourceError.DefaultError).error
        _errorChannel.send(error)
        _loginState.update {
            it.copy(isLoading = false)
        }
    }

    private fun handleFieldError(result: Resource.Failure<Unit>) {
        val errors = (result.error as ResourceError.FieldError).errors
        _loginState.update { state ->
            state.copy(
                isLoading = false,
                emailError = errors.find { it.field == "email" }?.error,
                passwordError = errors.find { it.field == "password" }?.error
            )
        }
    }
}