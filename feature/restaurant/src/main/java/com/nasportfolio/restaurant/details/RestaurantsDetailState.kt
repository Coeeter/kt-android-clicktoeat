package com.nasportfolio.restaurant.details

import com.google.android.gms.maps.model.LatLng
import com.nasportfolio.domain.restaurant.TransformedRestaurant

data class RestaurantsDetailState(
    val restaurant: TransformedRestaurant? = null,
    val isLoading: Boolean = true,
    val shouldNavigateBack: Boolean = false,
    val isUpdated: Boolean = false,
    val currentLocation: LatLng? = null,
    val isAnimationDone: Boolean = false
)