package com.nasportfolio.restaurant.home

import com.nasportfolio.domain.restaurant.TransformedRestaurant

data class HomeState(
    val restaurantList: List<TransformedRestaurant> = emptyList(),
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
)
