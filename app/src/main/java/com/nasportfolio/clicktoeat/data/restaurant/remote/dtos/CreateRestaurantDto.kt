package com.nasportfolio.clicktoeat.data.restaurant.remote.dtos

import java.io.File

data class CreateRestaurantDto(
    val name: String,
    val description: String,
    val image: File?
)