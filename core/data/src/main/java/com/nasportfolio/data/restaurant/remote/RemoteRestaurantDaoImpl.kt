package com.nasportfolio.data.restaurant.remote

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nasportfolio.data.common.DefaultMessageDto
import com.nasportfolio.data.common.EntityCreatedDto
import com.nasportfolio.data.restaurant.remote.dtos.CreateRestaurantDto
import com.nasportfolio.data.restaurant.remote.dtos.UpdateRestaurantDto
import com.nasportfolio.data.utils.Constants.NO_RESPONSE
import com.nasportfolio.data.utils.tryWithIoHandling
import com.nasportfolio.domain.restaurant.Restaurant
import com.nasportfolio.domain.utils.Resource
import com.nasportfolio.domain.utils.ResourceError
import com.nasportfolio.network.Authorization
import com.nasportfolio.network.OkHttpDao
import com.nasportfolio.network.delegations.AuthorizationImpl
import com.nasportfolio.network.delegations.OkHttpDaoImpl
import okhttp3.OkHttpClient
import javax.inject.Inject

class RemoteRestaurantDaoImpl @Inject constructor(
    okHttpClient: OkHttpClient,
    gson: Gson,
) : RemoteRestaurantDao,
    Authorization by AuthorizationImpl(),
    OkHttpDao by OkHttpDaoImpl(
        gson = gson,
        okHttpClient = okHttpClient,
        path = "/api/restaurants"
    ) {

    override suspend fun getAllRestaurants(): Resource<List<Restaurant>> =
        tryWithIoHandling {
            val (json, code) = get()
            json ?: return@tryWithIoHandling Resource.Failure(
                ResourceError.DefaultError(NO_RESPONSE)
            )
            return@tryWithIoHandling when (code) {
                200 -> Resource.Success(
                    gson.fromJson(
                        json,
                        object : TypeToken<List<Restaurant>>() {}.type
                    )
                )
                else -> Resource.Failure(
                    gson.fromJson<ResourceError.DefaultError>(
                        json,
                        object : TypeToken<ResourceError.DefaultError>() {}.type
                    )
                )
            }
        }

    override suspend fun getRestaurantById(id: String): Resource<Restaurant> =
        tryWithIoHandling {
            val (json, code) = get(endpoint = "/$id")
            json ?: return@tryWithIoHandling Resource.Failure(
                ResourceError.DefaultError(NO_RESPONSE)
            )
            return@tryWithIoHandling when (code) {
                200 -> Resource.Success(
                    gson.fromJson(
                        json,
                        object : TypeToken<Restaurant>() {}.type
                    )
                )
                else -> Resource.Failure(
                    gson.fromJson<ResourceError.DefaultError>(
                        json,
                        object : TypeToken<ResourceError.DefaultError>() {}.type
                    )
                )
            }
        }

    override suspend fun createRestaurant(
        token: String,
        createRestaurantDto: CreateRestaurantDto
    ): Resource<String> = tryWithIoHandling {
        val (json, code) = post(
            body = createRestaurantDto.copy(image = null),
            headers = createAuthorizationHeader(token),
            file = createRestaurantDto.image,
            requestName = "brandImage"
        )
        json ?: return@tryWithIoHandling Resource.Failure(
            ResourceError.DefaultError(NO_RESPONSE)
        )
        return@tryWithIoHandling when (code) {
            200 -> Resource.Success(
                gson.fromJson<EntityCreatedDto>(
                    json,
                    object : TypeToken<EntityCreatedDto>() {}.type
                ).insertId
            )
            400 -> Resource.Failure(
                gson.fromJson<ResourceError.FieldError>(
                    json,
                    object : TypeToken<ResourceError.FieldError>() {}.type
                )
            )
            else -> Resource.Failure(
                gson.fromJson<ResourceError.DefaultError>(
                    json,
                    object : TypeToken<ResourceError.DefaultError>() {}.type
                )
            )
        }
    }

    override suspend fun updateRestaurant(
        token: String,
        id: String,
        updateRestaurantDto: UpdateRestaurantDto
    ): Resource<Restaurant> = tryWithIoHandling {
        val (json, code) = put(
            endpoint = "/$id",
            body = updateRestaurantDto.copy(image = null),
            file = updateRestaurantDto.image,
            requestName = "brandImage",
            headers = createAuthorizationHeader(token)
        )
        json ?: return@tryWithIoHandling Resource.Failure(
            ResourceError.DefaultError(NO_RESPONSE)
        )
        return@tryWithIoHandling when (code) {
            200 -> Resource.Success(
                gson.fromJson(
                    json,
                    object : TypeToken<Restaurant>() {}.type
                )
            )
            else -> Resource.Failure(
                gson.fromJson<ResourceError.DefaultError>(
                    json,
                    object : TypeToken<ResourceError.DefaultError>() {}.type
                )
            )
        }
    }

    override suspend fun deleteRestaurant(
        token: String,
        id: String
    ): Resource<String> = tryWithIoHandling {
        val (json, code) = delete<Unit>(
            endpoint = "/$id",
            headers = createAuthorizationHeader(token),
        )
        json ?: return@tryWithIoHandling Resource.Failure(
            ResourceError.DefaultError(NO_RESPONSE)
        )
        return@tryWithIoHandling when (code) {
            200 -> Resource.Success(
                gson.fromJson<DefaultMessageDto>(
                    json,
                    object : TypeToken<DefaultMessageDto>() {}.type
                ).message
            )
            else -> Resource.Failure(
                gson.fromJson<ResourceError.DefaultError>(
                    json,
                    object : TypeToken<ResourceError.DefaultError>() {}.type
                )
            )
        }
    }
}