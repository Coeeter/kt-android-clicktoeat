package com.nasportfolio.data.favorite.remote

import com.nasportfolio.data.common.DefaultMessageDto
import com.nasportfolio.data.utils.Constants.NO_RESPONSE
import com.nasportfolio.data.utils.tryWithIoHandling
import com.nasportfolio.domain.restaurant.Restaurant
import com.nasportfolio.domain.user.User
import com.nasportfolio.domain.utils.Resource
import com.nasportfolio.domain.utils.ResourceError
import com.nasportfolio.network.Authorization
import com.nasportfolio.network.JsonConverter
import com.nasportfolio.network.OkHttpDao
import com.nasportfolio.network.delegations.AuthorizationImpl
import com.nasportfolio.network.delegations.OkHttpDaoImpl
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
            val (json, code) = get(endpoint = "/users/$userId")
            json ?: return@tryWithIoHandling Resource.Failure(
                ResourceError.DefaultError(NO_RESPONSE)
            )
            return@tryWithIoHandling when (code) {
                200 -> Resource.Success(
                    converter.fromJson(json)
                )
                else -> Resource.Failure(
                    converter.fromJson<ResourceError.DefaultError>(json)
                )
            }
        }

    override suspend fun getUsersWhoFavoriteRestaurant(restaurantId: String): Resource<List<User>> =
        tryWithIoHandling {
            val (json, code) = get(endpoint = "/restaurants/$restaurantId")
            json ?: return@tryWithIoHandling Resource.Failure(
                ResourceError.DefaultError(NO_RESPONSE)
            )
            return@tryWithIoHandling when (code) {
                200 -> Resource.Success(
                    converter.fromJson(json)
                )
                else -> Resource.Failure(
                    converter.fromJson<ResourceError.DefaultError>(json)
                )
            }
        }

    override suspend fun addFavorite(
        token: String,
        restaurantId: String
    ): Resource<String> = tryWithIoHandling {
        val (json, code) = post(
            endpoint = "/$restaurantId",
            body = emptyMap<Unit, Unit>(),
            headers = createAuthorizationHeader(token)
        )
        json ?: return@tryWithIoHandling Resource.Failure(
            ResourceError.DefaultError(NO_RESPONSE)
        )
        return@tryWithIoHandling when (code) {
            200 -> Resource.Success(
                converter.fromJson<DefaultMessageDto>(json).message
            )
            else -> Resource.Failure(
                converter.fromJson<ResourceError.DefaultError>(json)
            )
        }
    }

    override suspend fun removeFavorite(
        token: String,
        restaurantId: String
    ): Resource<String> = tryWithIoHandling {
        val (json, code) = delete<Unit>(
            endpoint = "/$restaurantId",
            headers = createAuthorizationHeader(token)
        )
        json ?: return@tryWithIoHandling Resource.Failure(
            ResourceError.DefaultError(NO_RESPONSE)
        )
        return@tryWithIoHandling when (code) {
            200 -> Resource.Success(
                converter.fromJson<DefaultMessageDto>(json).message
            )
            else -> Resource.Failure(
                converter.fromJson<ResourceError.DefaultError>(json)
            )
        }
    }

}