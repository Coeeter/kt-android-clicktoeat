package com.nasportfolio.clicktoeat.domain.restaurant

import com.nasportfolio.clicktoeat.data.restaurant.Restaurant
import com.nasportfolio.clicktoeat.utils.Resource

interface RestaurantRepository {
    suspend fun getAllRestaurants(): Resource<List<Restaurant>>
}