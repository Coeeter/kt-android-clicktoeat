package com.nasportfolio.user.update.password

import com.nasportfolio.domain.user.User

data class UpdatePasswordState(
    val user: User? = null,
    val isLoading: Boolean = true,
    val oldPassword: String = "",
    val oldPasswordError: String? = null,
    val newPassword: String = "",
    val newPasswordError: String? = null,
    val confirmNewPassword: String = "",
    val confirmNewPasswordError: String? = null,
    val isSubmitting: Boolean = false,
    val isUpdated: Boolean = false
)