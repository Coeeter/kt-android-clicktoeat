package com.nasportfolio.auth.resetpassword

sealed class ResetPasswordEvent {
    class OnPasswordChanged(val password: String) : ResetPasswordEvent()
    class OnConfirmPasswordChanged(val confirmPassword: String) : ResetPasswordEvent()
    object OnSubmit : ResetPasswordEvent()
}