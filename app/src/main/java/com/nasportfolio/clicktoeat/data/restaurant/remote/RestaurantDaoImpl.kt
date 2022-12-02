package com.nasportfolio.clicktoeat.data.restaurant.remote

import com.google.gson.Gson
import com.nasportfolio.clicktoeat.data.restaurant.remote.dtos.CreateRestaurantDto
import com.nasportfolio.clicktoeat.data.restaurant.remote.dtos.UpdateRestaurantDto
import com.nasportfolio.clicktoeat.domain.restaurant.Restaurant
import com.nasportfolio.clicktoeat.domain.utils.Resource
import com.nasportfolio.clicktoeat.domain.utils.ResourceError
import com.nasportfolio.clicktoeat.utils.Constants.BASE_URL
import com.nasportfolio.clicktoeat.utils.Constants.UNABLE_GET_BODY_ERROR_MESSAGE
import com.nasportfolio.clicktoeat.utils.await
import com.nasportfolio.clicktoeat.utils.decodeFromJson
import com.nasportfolio.clicktoeat.utils.toJson
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import javax.inject.Inject

class RestaurantDaoImpl @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val gson: Gson,
) : RestaurantDao {
    companion object {
        const val PATH = "/api/restaurants"
    }

    override suspend fun getAllRestaurants(): Resource<List<Restaurant>> {
        val request = Request.Builder()
            .url("$BASE_URL/$PATH")
            .build()
        try {
            val response = okHttpClient.newCall(request).await()
            val json = response.body?.toJson()
            json ?: return Resource.Failure(
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
        val request = Request.Builder()
            .url("$BASE_URL/$PATH/$id")
            .build()
        try {
            val response = okHttpClient.newCall(request).await()
            val json = response.body?.toJson()
            json ?: return Resource.Failure(
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