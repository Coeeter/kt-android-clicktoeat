package com.nasportfolio.restaurant.create.restaurant

import android.graphics.Bitmap

sealed class CreateRestaurantEvent {
    class OnNameChanged(val name: String) : CreateRestaurantEvent()
    class OnDescriptionChanged(val description: String) : CreateRestaurantEvent()
    class OnImageChanged(val image: Bitmap) : CreateRestaurantEvent()
    object OnSubmit : CreateRestaurantEvent()
}