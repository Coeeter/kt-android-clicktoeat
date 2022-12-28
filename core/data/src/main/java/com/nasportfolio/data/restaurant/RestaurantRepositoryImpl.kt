package com.nasportfolio.data.restaurant

import com.nasportfolio.data.restaurant.remote.RemoteRestaurantDao
import com.nasportfolio.data.restaurant.remote.dtos.CreateRestaurantDto
import com.nasportfolio.data.restaurant.remote.dtos.UpdateRestaurantDto
import com.nasportfolio.domain.restaurant.Restaurant
import com.nasportfolio.domain.restaurant.RestaurantRepository
import com.nasportfolio.domain.utils.Resource
import java.io.File
import javax.inject.Inject

class RestaurantRepositoryImpl @Inject constructor(
    private val remoteRestaurantDao: RemoteRestaurantDao
) : RestaurantRepository {
    override suspend fun getAllRestaurants(): Resource<List<Restaurant>> =
        remoteRestaurantDao.getAllRestaurants()

    override suspend fun getRestaurantById(id: String): Resource<Restaurant> =
        remoteRestaurantDao.getRestaurantById(id = id)

    override suspend fun createRestaurant(
        token: String,
        name: String,
        description: String,
        image: File
    ): Resource<String> = remoteRestaurantDao.createRestaurant(
        token = token,
        createRestaurantDto = CreateRestaurantDto(
            name = name,
            description = description,
            image = image
        )
    )

    override suspend fun updateRestaurant(
        token: String,
        restaurantId: String,
        name: String?,
        description: String?,
        image: File?
    ): Resource<Restaurant> = remoteRestaurantDao.updateRestaurant(
        token = token,
        id = restaurantId,
        updateRestaurantDto = UpdateRestaurantDto(
            name = name,
            description = description,
            image = image,
        )
    )

    override suspend fun deleteRestaurant(
        token: String,
        restaurantId: String
    ): Resource<String> = remoteRestaurantDao.deleteRestaurant(
        token = token,
        id = restaurantId
    )
}