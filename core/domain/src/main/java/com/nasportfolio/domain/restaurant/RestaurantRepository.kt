package com.nasportfolio.domain.restaurant

import com.nasportfolio.domain.utils.Resource
import kotlinx.coroutines.flow.Flow

interface RestaurantRepository {
    fun getAllRestaurants(fetchFromRemote: Boolean = false): Flow<Resource<List<Restaurant>>>

    fun getRestaurantById(id: String): Flow<Resource<Restaurant>>

    suspend fun createRestaurant(
        token: String,
        name: String,
        description: String,
        image: ByteArray
    ): Resource<String>

    suspend fun updateRestaurant(
        token: String,
        restaurantId: String,
        name: String? = null,
        description: String? = null,
        image: ByteArray? = null
    ): Resource<Restaurant>

    suspend fun deleteRestaurant(
        token: String,
        restaurantId: String
    ): Resource<String>
}