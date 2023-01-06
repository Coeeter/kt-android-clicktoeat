package com.nasportfolio.domain.user

import com.nasportfolio.domain.image.Image

data class User(
    val id: String,
    val username: String,
    val email: String,
    val image: Image? = null
)
