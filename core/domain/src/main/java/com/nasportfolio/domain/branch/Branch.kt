package com.nasportfolio.domain.branch

import com.nasportfolio.domain.restaurant.Restaurant

data class Branch(
    val id: String,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val restaurant: Restaurant,
)
