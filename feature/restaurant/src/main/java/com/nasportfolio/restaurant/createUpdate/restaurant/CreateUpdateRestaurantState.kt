package com.nasportfolio.restaurant.createUpdate.restaurant

import android.graphics.Bitmap

data class CreateUpdateRestaurantState(
    val name: String = "",
    val description: String = "",
    val image: Bitmap? = null,
    val nameError: String? = null,
    val descriptionError: String? = null,
    val imageError: String? = null,
    val isLoading: Boolean = false,
    val insertId: String? = null,
    val isUpdateForm: Boolean = false,
    val isUpdateComplete: Boolean = false
)