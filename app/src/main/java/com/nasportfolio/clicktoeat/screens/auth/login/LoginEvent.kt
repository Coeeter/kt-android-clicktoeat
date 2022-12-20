package com.nasportfolio.clicktoeat.screens.auth.login

sealed class LoginEvent {
    class OnEmailChange(val email: String): LoginEvent()
    class OnPasswordChange(val password: String): LoginEvent()
    object OnSubmit: LoginEvent()
}