package com.nasportfolio.clicktoeat.data.user.remote.dtos

import java.io.File

data class SignUpDto(
    val username: String,
    val email: String,
    val password: String,
    val fcmToken: String,
    val image: File? = null,
)