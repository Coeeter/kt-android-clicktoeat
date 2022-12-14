package com.nasportfolio.auth.signup

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
    val isLoading: Boolean = false,
    val isCreated: Boolean = false,
)

enum class SignUpStage {
    NAME, PASSWORD
}