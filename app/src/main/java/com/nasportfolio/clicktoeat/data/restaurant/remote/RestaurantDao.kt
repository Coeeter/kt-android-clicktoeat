package com.nasportfolio.clicktoeat.data.restaurant.remote

import com.google.gson.Gson
import com.nasportfolio.clicktoeat.data.common.OkHttpDao
import com.nasportfolio.clicktoeat.data.restaurant.remote.dtos.CreateRestaurantDto
import com.nasportfolio.clicktoeat.data.restaurant.remote.dtos.UpdateRestaurantDto
import com.nasportfolio.clicktoeat.domain.restaurant.Restaurant
import com.nasportfolio.clicktoeat.domain.utils.Resource
import okhttp3.OkHttpClient

abstract class RestaurantDao(
    okHttpClient: OkHttpClient,
    gson: Gson
) : OkHttpDao(okHttpClient, gson, "/api/restaurants") {
    abstract suspend fun getAllRestaurants(): Resource<List<Restaurant>>
    abstract suspend fun getRestaurantById(id: String): Resource<Restaurant>

    abstract suspend fun createRestaurant(
        token: String,
        createRestaurantDto: CreateRestaurantDto
    ): Resource<String>

    abstract suspend fun updateRestaurant(
        token: String,
        id: String,
        updateRestaurantDto: UpdateRestaurantDto
    ): Resource<Restaurant>

    abstract suspend fun deleteRestaurant(token: String, id: String): Resource<String>
}