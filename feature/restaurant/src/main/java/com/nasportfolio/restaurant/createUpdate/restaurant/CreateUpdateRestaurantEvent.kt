package com.nasportfolio.restaurant.createUpdate.restaurant

import android.graphics.Bitmap

sealed class CreateUpdateRestaurantEvent {
    class OnNameChanged(val name: String) : CreateUpdateRestaurantEvent()
    class OnDescriptionChanged(val description: String) : CreateUpdateRestaurantEvent()
    class OnImageChanged(val image: Bitmap) : CreateUpdateRestaurantEvent()
    object OnSubmit : CreateUpdateRestaurantEvent()
}