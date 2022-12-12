package com.nasportfolio.data.restaurant.remote.dtos

import java.io.File

data class UpdateRestaurantDto(
    val name: String? = null,
    val description: String? = null,
    val image: File? = null
)