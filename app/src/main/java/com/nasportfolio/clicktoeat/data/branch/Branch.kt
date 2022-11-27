package com.nasportfolio.clicktoeat.data.branch

import com.nasportfolio.clicktoeat.data.restaurant.Restaurant

data class Branch(
    val id: String,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val restaurant: Restaurant,
)
