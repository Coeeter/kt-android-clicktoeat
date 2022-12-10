package com.nasportfolio.clicktoeat.data.restaurant.remote

import com.nasportfolio.clicktoeat.data.restaurant.remote.dtos.CreateRestaurantDto
import com.nasportfolio.clicktoeat.data.restaurant.remote.dtos.UpdateRestaurantDto
import com.nasportfolio.clicktoeat.domain.restaurant.Restaurant
import com.nasportfolio.clicktoeat.domain.utils.Resource

interface RemoteRestaurantDao {
    suspend fun getAllRestaurants(): Resource<List<Restaurant>>

    suspend fun getRestaurantById(id: String): Resource<Restaurant>

    suspend fun createRestaurant(
        token: String,
        createRestaurantDto: CreateRestaurantDto
    ): Resource<String>

    suspend fun updateRestaurant(
        token: String,
        id: String,
        updateRestaurantDto: UpdateRestaurantDto
    ): Resource<Restaurant>

    suspend fun deleteRestaurant(token: String, id: String): Resource<String>
}