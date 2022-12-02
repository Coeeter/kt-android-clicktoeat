package com.nasportfolio.clicktoeat.data.restaurant.remote

import com.nasportfolio.clicktoeat.domain.restaurant.Restaurant
import com.nasportfolio.clicktoeat.data.restaurant.remote.dtos.CreateRestaurantDto
import com.nasportfolio.clicktoeat.data.restaurant.remote.dtos.UpdateRestaurantDto
import com.nasportfolio.clicktoeat.domain.utils.ResourceError
import com.nasportfolio.clicktoeat.domain.utils.Resource

interface RestaurantDao {
    suspend fun getAllRestaurants(): Resource<List<Restaurant>>
    suspend fun getRestaurantById(id: String): Resource<Restaurant>
    suspend fun createRestaurant(createRestaurantDto: CreateRestaurantDto): Resource<String>
    suspend fun deleteRestaurant(id: String): Resource<Unit>
    suspend fun updateRestaurant(updateRestaurantDto: UpdateRestaurantDto): Resource<Restaurant>
}