package com.nasportfolio.user.delete

data class DeleteAccountState(
    val password: String = "",
    val passwordError: String? = null,
    val isLoading: Boolean = false,
    val isDeleted: Boolean = false,
)