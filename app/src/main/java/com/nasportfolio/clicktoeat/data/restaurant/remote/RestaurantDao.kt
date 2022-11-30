package com.nasportfolio.clicktoeat.data.restaurant.remote

import com.nasportfolio.clicktoeat.data.restaurant.Restaurant
import com.nasportfolio.clicktoeat.utils.Resource

interface RestaurantDao {
    suspend fun getAllRestaurants(): Resource<List<Restaurant>>
}