package com.nasportfolio.clicktoeat.domain.branch

import com.nasportfolio.clicktoeat.domain.restaurant.Restaurant

data class Branch(
    val id: String,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val restaurant: Restaurant,
)
