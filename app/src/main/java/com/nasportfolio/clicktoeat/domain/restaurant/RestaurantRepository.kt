package com.nasportfolio.clicktoeat.domain.restaurant

import com.nasportfolio.clicktoeat.domain.utils.Resource

interface RestaurantRepository {
    suspend fun getAllRestaurants(): Resource<List<Restaurant>>
}