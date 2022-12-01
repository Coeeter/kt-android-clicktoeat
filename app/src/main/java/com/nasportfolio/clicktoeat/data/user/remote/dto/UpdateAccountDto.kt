package com.nasportfolio.clicktoeat.data.user.remote.dto

import java.io.File

data class UpdateAccountDto(
    val username: String? = null,
    val email: String? = null,
    val password: String? = null,
    val image: File? = null,
    val fcmToken: String? = null,
    val deleteImage: Boolean? = null,
)