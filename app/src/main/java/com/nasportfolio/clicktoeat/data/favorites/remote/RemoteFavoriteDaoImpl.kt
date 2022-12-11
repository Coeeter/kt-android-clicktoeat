package com.nasportfolio.clicktoeat.data.favorites.remote

import com.nasportfolio.clicktoeat.data.common.Authorization
import com.nasportfolio.clicktoeat.data.common.OkHttpDao
import com.nasportfolio.clicktoeat.data.common.converter.JsonConverter
import com.nasportfolio.clicktoeat.data.common.delegations.AuthorizationImpl
import com.nasportfolio.clicktoeat.data.common.delegations.OkHttpDaoImpl
import com.nasportfolio.clicktoeat.data.common.dtos.DefaultMessageDto
import com.nasportfolio.clicktoeat.data.utils.tryWithIoHandling
import com.nasportfolio.clicktoeat.domain.restaurant.Restaurant
import com.nasportfolio.clicktoeat.domain.user.User
import com.nasportfolio.clicktoeat.domain.utils.Resource
import com.nasportfolio.clicktoeat.domain.utils.ResourceError
import com.nasportfolio.clicktoeat.utils.Constants.UNABLE_GET_BODY_ERROR_MESSAGE
import com.nasportfolio.clicktoeat.utils.toJson
import okhttp3.OkHttpClient
import javax.inject.Inject

class RemoteFavoriteDaoImpl @Inject constructor(
    okHttpClient: OkHttpClient,
    jsonConverter: JsonConverter
) : RemoteFavoriteDao,
    Authorization by AuthorizationImpl(),
    OkHttpDao by OkHttpDaoImpl(
        okHttpClient = okHttpClient,
        converter = jsonConverter,
        path = "/api/favorites"
    ) {

    override suspend fun getFavoriteRestaurantsOfUser(userId: String): Resource<List<Restaurant>> =
        tryWithIoHandling {
            val response = get(endpoint = "/users/$userId")
            val json = response.body?.toJson() ?: return@tryWithIoHandling Resource.Failure(
                ResourceError.Default(UNABLE_GET_BODY_ERROR_MESSAGE)
            )
            return@tryWithIoHandling when (response.code) {
                200 -> Resource.Success(
                    converter.fromJson(json)
                )
                else -> Resource.Failure(
                    converter.fromJson<ResourceError.Default>(json)
                )
            }
        }

    override suspend fun getUsersWhoFavoriteRestaurant(restaurantId: String): Resource<List<User>> =
        tryWithIoHandling {
            val response = get(endpoint = "/restaurants/$restaurantId")
            val json = response.body?.toJson() ?: return@tryWithIoHandling Resource.Failure(
                ResourceError.Default(UNABLE_GET_BODY_ERROR_MESSAGE)
            )
            return@tryWithIoHandling when (response.code) {
                200 -> Resource.Success(
                    converter.fromJson(json)
                )
                else -> Resource.Failure(
                    converter.fromJson<ResourceError.Default>(json)
                )
            }
        }

    override suspend fun addFavorite(
        token: String,
        restaurantId: String
    ): Resource<String> = tryWithIoHandling {
        val response = post(
            endpoint = "/$restaurantId",
            body = emptyMap<Unit, Unit>(),
            headers = createAuthorizationHeader(token)
        )
        val json = response.body?.toJson() ?: return@tryWithIoHandling Resource.Failure(
            ResourceError.Default(UNABLE_GET_BODY_ERROR_MESSAGE)
        )
        return@tryWithIoHandling when (response.code) {
            200 -> Resource.Success(
                converter.fromJson<DefaultMessageDto>(json).message
            )
            else -> Resource.Failure(
                converter.fromJson<ResourceError.Default>(json)
            )
        }
    }

    override suspend fun removeFavorite(
        token: String,
        restaurantId: String
    ): Resource<String> = tryWithIoHandling {
        val response = delete<Unit>(
            endpoint = "/$restaurantId",
            headers = createAuthorizationHeader(token)
        )
        val json = response.body?.toJson() ?: return@tryWithIoHandling Resource.Failure(
            ResourceError.Default(UNABLE_GET_BODY_ERROR_MESSAGE)
        )
        return@tryWithIoHandling when (response.code) {
            200 -> Resource.Success(
                converter.fromJson<DefaultMessageDto>(json).message
            )
            else -> Resource.Failure(
                converter.fromJson<ResourceError.Default>(json)
            )
        }
    }
}