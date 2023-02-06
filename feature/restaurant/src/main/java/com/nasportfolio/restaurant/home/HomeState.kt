package com.nasportfolio.restaurant.home

import com.google.android.gms.maps.model.LatLng
import com.nasportfolio.domain.branch.Branch
import com.nasportfolio.domain.restaurant.TransformedRestaurant
import com.nasportfolio.domain.user.User

data class HomeState(
    val restaurantList: List<TransformedRestaurant> = emptyList(),
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val currentUser: User? = null,
    val favRestaurants: List<Int> = emptyList(),
    val featuredRestaurants: List<Int> = emptyList(),
    val branches: List<Branch> = emptyList(),
    val currentLocation: LatLng? = null,
)