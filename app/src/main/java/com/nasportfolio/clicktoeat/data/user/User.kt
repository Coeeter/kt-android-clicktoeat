package com.nasportfolio.clicktoeat.data.user

import com.nasportfolio.clicktoeat.data.common.models.Image

data class User(
    val id: String,
    val username: String,
    val email: String,
    val image: Image? = null
)
