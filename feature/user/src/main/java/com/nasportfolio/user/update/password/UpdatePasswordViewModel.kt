package com.nasportfolio.user.update.password

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nasportfolio.domain.user.usecases.GetCurrentLoggedInUser
import com.nasportfolio.domain.user.usecases.LoginUseCase
import com.nasportfolio.domain.user.usecases.UpdateAccountUseCase
import com.nasportfolio.domain.utils.Resource
import com.nasportfolio.domain.utils.ResourceError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class UpdatePasswordViewModel @Inject constructor(
    private val getCurrentLoggedInUser: GetCurrentLoggedInUser,
    private val loginUseCase: LoginUseCase,
    private val updateAccountUseCase: UpdateAccountUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(UpdatePasswordState())
    val state = _state.asStateFlow()

    private val _errorChannel = Channel<String>()
    val errorChannel = _errorChannel.receiveAsFlow()

    init {
        getCurrentUser()
    }

    private fun getCurrentUser() {
        getCurrentLoggedInUser().onEach {
            when (it) {
                is Resource.Success -> _state.update { state ->
                    state.copy(
                        user = it.result,
                        isLoading = false
                    )
                }
                is Resource.Failure -> {
                    _state.update { state -> state.copy(isLoading = false) }
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

    private fun changePassword() {
        updateAccountUseCase.updatePassword(
            password = _state.value.newPassword,
            confirmPassword = _state.value.confirmNewPassword
        ).onEach {
            when (it) {
                is Resource.Loading -> _state.update { state ->
                    state.copy(isLoading = it.isLoading)
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
                                newPasswordError = fieldErrors.find { it.field == "password" || it.field == "confirmPassword" }?.error,
                                confirmNewPasswordError = fieldErrors.find { it.field == "confirmPassword" }?.error,
                            )
                        }
                    }
                }
                is Resource.Success -> _state.update { state ->
                    state.copy(
                        isSubmitting = false,
                        isUpdated = true
                    )
                }
            }
        }.flowOn(Dispatchers.IO).launchIn(viewModelScope)
    }

    private fun submit() {
        val user = _state.value.user ?: return
        loginUseCase(email = user.email, password = _state.value.oldPassword).onEach {
            when (it) {
                is Resource.Success -> changePassword()
                is Resource.Failure -> {
                    _state.value = _state.value.copy(isSubmitting = false)
                    when (it.error) {
                        is ResourceError.DefaultError -> _state.update { state ->
                            val defaultError = (it.error as ResourceError.DefaultError).error
                            state.copy(oldPasswordError = defaultError)
                        }
                        is ResourceError.FieldError -> _state.update { state ->
                            val fieldErrors = (it.error as ResourceError.FieldError).errors
                            state.copy(
                                oldPasswordError = fieldErrors.find { it.field == "password" }?.error
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

    fun onEvent(event: UpdatePasswordEvent) {
        when (event) {
            is UpdatePasswordEvent.OnOldPasswordChange -> _state.update { state ->
                state.copy(
                    oldPassword = event.oldPassword,
                    oldPasswordError = null
                )
            }
            is UpdatePasswordEvent.OnNewPasswordChange -> _state.update { state ->
                state.copy(
                    newPassword = event.newPassword,
                    newPasswordError = null,
                    confirmNewPasswordError = null
                )
            }
            is UpdatePasswordEvent.OnConfirmNewPasswordChange -> _state.update { state ->
                state.copy(
                    confirmNewPassword = event.confirmNewPassword,
                    confirmNewPasswordError = null
                )
            }
            is UpdatePasswordEvent.OnSubmit -> submit()
        }
    }
}