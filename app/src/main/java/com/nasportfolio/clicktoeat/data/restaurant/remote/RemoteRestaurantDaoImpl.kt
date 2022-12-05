package com.nasportfolio.clicktoeat.data.restaurant.remote

import com.google.gson.Gson
import com.nasportfolio.clicktoeat.data.common.dtos.DefaultMessageDto
import com.nasportfolio.clicktoeat.data.common.dtos.EntityCreatedDto
import com.nasportfolio.clicktoeat.data.restaurant.remote.dtos.CreateRestaurantDto
import com.nasportfolio.clicktoeat.data.restaurant.remote.dtos.UpdateRestaurantDto
import com.nasportfolio.clicktoeat.data.utils.tryWithIoExceptionHandling
import com.nasportfolio.clicktoeat.domain.restaurant.Restaurant
import com.nasportfolio.clicktoeat.domain.utils.Resource
import com.nasportfolio.clicktoeat.domain.utils.ResourceError
import com.nasportfolio.clicktoeat.utils.Constants.UNABLE_GET_BODY_ERROR_MESSAGE
import com.nasportfolio.clicktoeat.utils.decodeFromJson
import com.nasportfolio.clicktoeat.utils.toJson
import okhttp3.OkHttpClient
import javax.inject.Inject

class RemoteRestaurantDaoImpl @Inject constructor(
    okHttpClient: OkHttpClient,
    gson: Gson,
) : RemoteRestaurantDao(okHttpClient, gson) {
    override suspend fun getAllRestaurants(): Resource<List<Restaurant>> =
        tryWithIoExceptionHandling {
            val response = get()
            val json = response.body?.toJson()
                ?: return@tryWithIoExceptionHandling Resource.Failure(
                    ResourceError.Default(UNABLE_GET_BODY_ERROR_MESSAGE)
                )
            return@tryWithIoExceptionHandling when (response.code) {
                200 -> Resource.Success(
                    gson.decodeFromJson(json)
                )
                else -> Resource.Failure(
                    gson.decodeFromJson<ResourceError.Default>(json)
                )
            }
        }

    override suspend fun getRestaurantById(id: String): Resource<Restaurant> =
        tryWithIoExceptionHandling {
            val response = get(endpoint = "/$id")
            val json = response.body?.toJson()
                ?: return@tryWithIoExceptionHandling Resource.Failure(
                    ResourceError.Default(UNABLE_GET_BODY_ERROR_MESSAGE)
                )
            return@tryWithIoExceptionHandling when (response.code) {
                200 -> Resource.Success(
                    gson.decodeFromJson(json)
                )
                else -> Resource.Failure(
                    gson.decodeFromJson<ResourceError.Default>(json)
                )
            }
        }

    override suspend fun createRestaurant(
        token: String,
        createRestaurantDto: CreateRestaurantDto
    ): Resource<String> = tryWithIoExceptionHandling {
        val response = post(
            body = createRestaurantDto.copy(image = null),
            headers = mapOf(AUTHORIZATION to BEARER + token),
            file = createRestaurantDto.image,
            requestName = "brandImage"
        )
        val json = response.body?.toJson() ?: return@tryWithIoExceptionHandling Resource.Failure(
            ResourceError.Default(UNABLE_GET_BODY_ERROR_MESSAGE)
        )
        return@tryWithIoExceptionHandling when (response.code) {
            200 -> Resource.Success(
                gson.decodeFromJson<EntityCreatedDto>(json).insertId
            )
            400 -> Resource.Failure(
                gson.decodeFromJson<ResourceError.Field>(json)
            )
            else -> Resource.Failure(
                gson.decodeFromJson<ResourceError.Default>(json)
            )
        }
    }

    override suspend fun updateRestaurant(
        token: String,
        id: String,
        updateRestaurantDto: UpdateRestaurantDto
    ): Resource<Restaurant> = tryWithIoExceptionHandling {
        val response = put(
            endpoint = "/$id",
            body = updateRestaurantDto.copy(image = null),
            file = updateRestaurantDto.image,
            requestName = "brandImage",
            headers = mapOf(AUTHORIZATION to BEARER + token)
        )
        val json = response.body?.toJson()
            ?: return@tryWithIoExceptionHandling Resource.Failure(
                ResourceError.Default(UNABLE_GET_BODY_ERROR_MESSAGE)
            )
        return@tryWithIoExceptionHandling when (response.code) {
            200 -> Resource.Success(
                gson.decodeFromJson(json)
            )
            else -> Resource.Failure(
                gson.decodeFromJson<ResourceError.Default>(json)
            )
        }
    }

    override suspend fun deleteRestaurant(token: String, id: String): Resource<String> =
        tryWithIoExceptionHandling {
            val response = delete<Unit>(
                endpoint = "/$id",
                headers = mapOf(AUTHORIZATION to BEARER + token),
            )
            val json = response.body?.toJson()
                ?: return@tryWithIoExceptionHandling Resource.Failure(
                    ResourceError.Default(UNABLE_GET_BODY_ERROR_MESSAGE)
                )
            return@tryWithIoExceptionHandling when (response.code) {
                200 -> Resource.Success(
                    gson.decodeFromJson<DefaultMessageDto>(json).message
                )
                else -> Resource.Failure(
                    gson.decodeFromJson<ResourceError.Default>(json)
                )
            }
        }
}