package com.nasportfolio.domain.restaurant

import com.nasportfolio.domain.utils.Resource

interface RestaurantRepository {
    suspend fun getAllRestaurants(): Resource<List<Restaurant>>
}