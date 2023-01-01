package com.nasportfolio.restaurant.create

import android.graphics.Bitmap

data class CreateRestaurantState(
    val name: String = "",
    val description: String = "",
    val image: Bitmap? = null,
    val nameError: String? = null,
    val descriptionError: String? = null,
    val imageError: String? = null,
    val isLoading: Boolean = false,
    val isCreated: Boolean = false,
)