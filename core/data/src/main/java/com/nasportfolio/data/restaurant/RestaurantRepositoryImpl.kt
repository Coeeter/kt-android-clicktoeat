package com.nasportfolio.data.restaurant

import com.nasportfolio.data.restaurant.remote.RemoteRestaurantDao
import com.nasportfolio.domain.restaurant.RestaurantRepository
import javax.inject.Inject

class RestaurantRepositoryImpl @Inject constructor(
    private val restaurantDao: RemoteRestaurantDao
) : RestaurantRepository {
    override suspend fun getAllRestaurants() = restaurantDao.getAllRestaurants()
}