package com.nasportfolio.auth.resetpassword

data class ResetPasswordState(
    val isLoading: Boolean = true,
    val token: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val confirmPassword: String = "",
    val confirmPasswordError: String? = null,
    val isSubmitting: Boolean = false,
    val isUpdated: Boolean = false
)