package com.nasportfolio.clicktoeat.data.restaurant

import com.nasportfolio.clicktoeat.data.restaurant.remote.RestaurantDao
import com.nasportfolio.clicktoeat.domain.restaurant.RestaurantRepository

class RestaurantRepositoryImpl(
    private val restaurantDao: RestaurantDao
) : RestaurantRepository {
    override suspend fun getAllRestaurants() = restaurantDao.getAllRestaurants()
}