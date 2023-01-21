package com.nasportfolio.user.update.account

import android.graphics.Bitmap

data class UpdateUserState(
    val userId: String? = null,
    val isLoading: Boolean = true,
    val username: String = "",
    val usernameError: String? = null,
    val email: String = "",
    val emailError: String? = null,
    val image: Bitmap? = null,
    val isSubmitting: Boolean = false,
    val isImageSubmitting: Boolean = false,
    val isUpdated: Boolean = false
)