package com.nasportfolio.auth.forgotPassword

data class ForgotPasswordState(
    val email: String = "",
    val emailError: String? = null,
    val isLoading: Boolean = false
)