package com.nasportfolio.clicktoeat.data.restaurant.remote

import com.google.gson.Gson
import com.nasportfolio.clicktoeat.data.restaurant.remote.dtos.CreateRestaurantDto
import com.nasportfolio.clicktoeat.data.restaurant.remote.dtos.UpdateRestaurantDto
import com.nasportfolio.clicktoeat.domain.restaurant.Restaurant
import com.nasportfolio.clicktoeat.domain.utils.Resource
import com.nasportfolio.clicktoeat.domain.utils.ResourceError
import com.nasportfolio.clicktoeat.utils.Constants.UNABLE_GET_BODY_ERROR_MESSAGE
import com.nasportfolio.clicktoeat.utils.decodeFromJson
import com.nasportfolio.clicktoeat.utils.toJson
import okhttp3.OkHttpClient
import java.io.IOException
import javax.inject.Inject

class RestaurantDaoImpl @Inject constructor(
    okHttpClient: OkHttpClient,
    gson: Gson,
) : RestaurantDao(okHttpClient, gson) {
    override suspend fun getAllRestaurants(): Resource<List<Restaurant>> {
        try {
            val response = get()
            val json = response.body?.toJson() ?: return Resource.Failure(
                ResourceError.Default(UNABLE_GET_BODY_ERROR_MESSAGE)
            )
            if (response.code == 200) return Resource.Success(
                gson.decodeFromJson(json)
            )
            return Resource.Failure(
                gson.decodeFromJson<ResourceError.Default>(json)
            )
        } catch (e: IOException) {
            return Resource.Failure(
                ResourceError.Default(e.message.toString())
            )
        }
    }

    override suspend fun getRestaurantById(id: String): Resource<Restaurant> {
        try {
            val response = get(endpoint = "/$id")
            val json = response.body?.toJson() ?: return Resource.Failure(
                ResourceError.Default(UNABLE_GET_BODY_ERROR_MESSAGE)
            )
            if (response.code == 200) return Resource.Success(
                gson.decodeFromJson(json)
            )
            return Resource.Failure(
                gson.decodeFromJson<ResourceError.Default>(json)
            )
        } catch (e: IOException) {
            return Resource.Failure(
                ResourceError.Default(e.message.toString())
            )
        }
    }

    override suspend fun createRestaurant(
        createRestaurantDto: CreateRestaurantDto
    ): Resource<String> {
        TODO("Not yet implemented")
    }

    override suspend fun updateRestaurant(
        updateRestaurantDto: UpdateRestaurantDto
    ): Resource<Restaurant> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteRestaurant(id: String): Resource<Unit> {
        TODO("Not yet implemented")
    }
}