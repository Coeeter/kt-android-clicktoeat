package com.nasportfolio.auth.login

internal sealed class LoginEvent {
    class OnEmailChange(val email: String): LoginEvent()
    class OnPasswordChange(val password: String): LoginEvent()
    object OnSubmit: LoginEvent()
}