package com.nasportfolio.clicktoeat.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nasportfolio.domain.user.UserRepository
import com.nasportfolio.domain.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn = _isLoggedIn.asStateFlow()

    init {
        checkIfLoggedIn()
    }

    private fun checkIfLoggedIn() {
        viewModelScope.launch {
            when (val tokenResource = userRepository.getToken()) {
                is Resource.Success -> {
                    when (userRepository.validateToken(tokenResource.result)) {
                        is Resource.Success -> _isLoggedIn.value = true
                        else -> Unit
                    }
                }
                else -> Unit
            }
        }
    }
}