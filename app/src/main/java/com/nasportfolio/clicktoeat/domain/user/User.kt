package com.nasportfolio.clicktoeat.domain.user

import com.nasportfolio.clicktoeat.domain.common.Image

data class User(
    val id: String,
    val username: String,
    val email: String,
    val image: Image? = null
)
