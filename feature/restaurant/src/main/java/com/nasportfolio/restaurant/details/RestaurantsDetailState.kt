package com.nasportfolio.restaurant.details

import com.nasportfolio.domain.restaurant.TransformedRestaurant

data class RestaurantsDetailState(
    val restaurant: TransformedRestaurant? = null,
    val isLoading: Boolean = true,
    val shouldNavigateBack: Boolean = false,
    val isUpdated: Boolean = false,
)