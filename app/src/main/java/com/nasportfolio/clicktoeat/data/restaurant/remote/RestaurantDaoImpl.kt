package com.nasportfolio.clicktoeat.data.restaurant.remote

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nasportfolio.clicktoeat.data.restaurant.Restaurant
import com.nasportfolio.clicktoeat.domain.common.exceptions.NoNetworkException
import com.nasportfolio.clicktoeat.utils.Constants.BASE_URL
import com.nasportfolio.clicktoeat.utils.Resource
import com.nasportfolio.clicktoeat.utils.await
import com.nasportfolio.clicktoeat.utils.toJson
import okhttp3.OkHttpClient
import okhttp3.Request

class RestaurantDaoImpl(
    private val okHttpClient: OkHttpClient
) : RestaurantDao {
    override suspend fun getAllRestaurants(): Resource<List<Restaurant>> {
        val request = Request.Builder().url("$BASE_URL/api/restaurants").build()
        return try {
            val response = okHttpClient.newCall(request).await()
            Resource.Success(
                Gson().fromJson(
                    response.body?.toJson(),
                    object : TypeToken<List<Restaurant>>() {}.type
                )
            )
        } catch (e: NoNetworkException) {
            Resource.Failure(e.message.toString())
        }
    }
}