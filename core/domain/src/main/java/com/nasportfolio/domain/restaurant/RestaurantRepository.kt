package com.nasportfolio.domain.restaurant

import com.nasportfolio.domain.utils.Resource
import java.io.File

interface RestaurantRepository {
    suspend fun getAllRestaurants(): Resource<List<Restaurant>>

    suspend fun getRestaurantById(id: String): Resource<Restaurant>

    suspend fun createRestaurant(
        token: String,
        name: String,
        description: String,
        image: File
    ): Resource<String>

    suspend fun updateRestaurant(
        token: String,
        restaurantId: String,
        name: String? = null,
        description: String? = null,
        image: File? = null
    ): Resource<Restaurant>

    suspend fun deleteRestaurant(
        token: String,
        restaurantId: String
    ): Resource<String>
}