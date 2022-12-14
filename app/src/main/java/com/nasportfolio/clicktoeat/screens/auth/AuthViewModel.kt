package com.nasportfolio.clicktoeat.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nasportfolio.domain.user.UserRepository
import com.nasportfolio.domain.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _currentPage = MutableStateFlow(CurrentPage.LOGIN)
    val currentPage = _currentPage.asStateFlow()

    private val _isLoggedIn = Channel<Boolean>()
    val isLoggedIn = _isLoggedIn.receiveAsFlow()

    init {
        checkIfLoggedIn()
    }

    private fun checkIfLoggedIn() {
        viewModelScope.launch {
            val tokenResource = withContext(Dispatchers.IO) {
                userRepository.getToken()
            }
            when (tokenResource) {
                is Resource.Success -> {
                    val resource = withContext(Dispatchers.IO) {
                        userRepository.validateToken(
                            token = tokenResource.result
                        )
                    }
                    when (resource) {
                        is Resource.Success -> _isLoggedIn.send(true)
                        else -> Unit
                    }
                }
                else -> Unit
            }
        }
    }

    fun togglePage() {
        _currentPage.update { page ->
            when (page) {
                CurrentPage.LOGIN -> CurrentPage.SIGNUP
                CurrentPage.SIGNUP -> CurrentPage.LOGIN
            }
        }
    }
}

data class LoginState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val isLoading: Boolean = false
)

data class SignUpState(
    val email: String = "",
    val username: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val emailError: String? = null,
    val usernameError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val signUpStage: SignUpStage = SignUpStage.NAME,
    val isLoading: Boolean = false
)

enum class SignUpStage {
    NAME, PASSWORD
}

enum class CurrentPage {
    LOGIN, SIGNUP
}