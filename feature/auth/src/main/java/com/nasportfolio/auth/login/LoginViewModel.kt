package com.nasportfolio.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import com.nasportfolio.domain.user.usecases.LoginUseCase
import com.nasportfolio.domain.utils.Resource
import com.nasportfolio.domain.utils.ResourceError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
internal class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val firebaseMessaging: FirebaseMessaging
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
        getToken { token ->
            val email = _loginState.value.email
            val password = _loginState.value.password
            loginUseCase(
                email = email,
                password = password,
                fcmToken = token
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
                            is ResourceError.FieldError -> handleFieldError(
                                result.error as ResourceError.FieldError
                            )
                            is ResourceError.DefaultError -> handleDefaultError(
                                result.error as ResourceError.DefaultError
                            )
                        }
                    }
                    is Resource.Loading -> _loginState.update { state ->
                        state.copy(isLoading = result.isLoading)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun getToken(callback: (String) -> Unit) {
        firebaseMessaging.token.addOnCompleteListener {
            if (it.exception != null) return@addOnCompleteListener runBlocking {
                it.exception!!.printStackTrace()
                _errorChannel.send(it.exception!!.message.toString())
                _loginState.update { state ->
                    state.copy(isLoading = false)
                }
            }
            callback(it.result)
        }
    }

    private suspend fun handleDefaultError(
        defaultError: ResourceError.DefaultError
    ) {
        _errorChannel.send(defaultError.error)
        _loginState.update {
            it.copy(isLoading = false)
        }
    }

    private fun handleFieldError(
        fieldError: ResourceError.FieldError
    ) {
        _loginState.update { state ->
            state.copy(
                isLoading = false,
                emailError = fieldError.errors.find { it.field == "email" }?.error,
                passwordError = fieldError.errors.find { it.field == "password" }?.error
            )
        }
    }
}