package com.nasportfolio.data.dislike.remote

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nasportfolio.data.common.DefaultMessageDto
import com.nasportfolio.data.utils.Constants.NO_RESPONSE
import com.nasportfolio.data.utils.tryWithIoHandling
import com.nasportfolio.domain.comment.Comment
import com.nasportfolio.domain.likesdislikes.dislike.Dislike
import com.nasportfolio.domain.user.User
import com.nasportfolio.domain.utils.Resource
import com.nasportfolio.domain.utils.ResourceError
import com.nasportfolio.network.Authorization
import com.nasportfolio.network.OkHttpDao
import com.nasportfolio.network.delegations.AuthorizationImpl
import com.nasportfolio.network.delegations.OkHttpDaoImpl
import okhttp3.OkHttpClient
import javax.inject.Inject


class RemoteDislikeDaoImpl @Inject constructor(
    okHttpClient: OkHttpClient,
    gson: Gson
) : RemoteDislikeDao,
    Authorization by AuthorizationImpl(),
    OkHttpDao by OkHttpDaoImpl(
        okHttpClient = okHttpClient,
        gson = gson,
        path = "/api/dislikes"
    ) {

    override suspend fun getAllDislikes(): Resource<List<Dislike>> = tryWithIoHandling {
        val (json, code) = get()
        json ?: return@tryWithIoHandling Resource.Failure(
            ResourceError.DefaultError(NO_RESPONSE)
        )
        return@tryWithIoHandling when (code) {
            200 -> Resource.Success(
                gson.fromJson(
                    json,
                    object : TypeToken<List<Dislike>>() {}.type
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

    override suspend fun getDislikedCommentsOfUser(userId: String): Resource<List<Comment>> =
        tryWithIoHandling {
            val (json, code) = get(endpoint = "?user=$userId")
            json ?: return@tryWithIoHandling Resource.Failure(
                ResourceError.DefaultError(NO_RESPONSE)
            )
            return@tryWithIoHandling when (code) {
                200 -> Resource.Success(
                    gson.fromJson(
                        json,
                        object : TypeToken<List<Comment>>() {}.type
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

    override suspend fun getUsersWhoDislikedComments(commentId: String): Resource<List<User>> =
        tryWithIoHandling {
            val (json, code) = get(endpoint = "?commentEntity=$commentId")
            json ?: return@tryWithIoHandling Resource.Failure(
                ResourceError.DefaultError(NO_RESPONSE)
            )
            return@tryWithIoHandling when (code) {
                200 -> Resource.Success(
                    gson.fromJson(
                        json,
                        object : TypeToken<List<User>>() {}.type
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

    override suspend fun createDislike(
        token: String,
        commentId: String
    ): Resource<String> = tryWithIoHandling {
        val (json, code) = post(
            endpoint = "/$commentId",
            body = emptyMap<Unit, Unit>(),
            headers = createAuthorizationHeader(token)
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

    override suspend fun deleteDislike(
        token: String,
        commentId: String
    ): Resource<String> = tryWithIoHandling {
        val (json, code) = delete<Unit>(
            endpoint = "/$commentId",
            headers = createAuthorizationHeader(token)
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