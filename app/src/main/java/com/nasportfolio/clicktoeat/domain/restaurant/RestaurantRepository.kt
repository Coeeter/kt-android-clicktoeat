package com.nasportfolio.clicktoeat.domain.restaurant

interface RestaurantRepository {
    suspend fun getAllRestaurants(): Resource<List<Restaurant>>
}