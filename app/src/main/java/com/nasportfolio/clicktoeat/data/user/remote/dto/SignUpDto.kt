package com.nasportfolio.clicktoeat.data.user.remote.dto

import java.io.File

data class SignUpDto(
    val username: String,
    val email: String,
    val password: String,
    val image: File,
    val fcmToken: String,
)
