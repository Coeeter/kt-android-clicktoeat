package com.nasportfolio.clicktoeat.data.restaurant

import com.nasportfolio.clicktoeat.data.restaurant.remote.RemoteRestaurantDao
import com.nasportfolio.clicktoeat.domain.restaurant.RestaurantRepository
import javax.inject.Inject

class RestaurantRepositoryImpl @Inject constructor(
    private val restaurantDao: RemoteRestaurantDao
) : RestaurantRepository {
    override suspend fun getAllRestaurants() = restaurantDao.getAllRestaurants()
}