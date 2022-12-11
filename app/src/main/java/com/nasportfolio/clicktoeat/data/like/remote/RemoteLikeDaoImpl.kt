package com.nasportfolio.clicktoeat.data.like.remote

import com.nasportfolio.clicktoeat.data.common.Authorization
import com.nasportfolio.clicktoeat.data.common.OkHttpDao
import com.nasportfolio.clicktoeat.data.common.converter.JsonConverter
import com.nasportfolio.clicktoeat.data.common.delegations.AuthorizationImpl
import com.nasportfolio.clicktoeat.data.common.delegations.OkHttpDaoImpl
import com.nasportfolio.clicktoeat.data.common.dtos.DefaultMessageDto
import com.nasportfolio.clicktoeat.data.utils.tryWithIoHandling
import com.nasportfolio.clicktoeat.domain.comment.Comment
import com.nasportfolio.clicktoeat.domain.user.User
import com.nasportfolio.clicktoeat.domain.utils.Resource
import com.nasportfolio.clicktoeat.domain.utils.ResourceError
import com.nasportfolio.clicktoeat.utils.Constants.UNABLE_GET_BODY_ERROR_MESSAGE
import com.nasportfolio.clicktoeat.utils.toJson
import okhttp3.OkHttpClient
import javax.inject.Inject

class RemoteLikeDaoImpl @Inject constructor(
    okHttpClient: OkHttpClient,
    jsonConverter: JsonConverter
) : RemoteLikeDao,
    Authorization by AuthorizationImpl(),
    OkHttpDao by OkHttpDaoImpl(
        okHttpClient = okHttpClient,
        converter = jsonConverter,
        path = "/api/likes"
    ) {

    override suspend fun getLikedCommentsOfUser(userId: String): Resource<List<Comment>> =
        tryWithIoHandling {
            val response = get(endpoint = "?user=$userId")
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

    override suspend fun getUsersWhoLikedComment(commentId: String): Resource<List<User>> =
        tryWithIoHandling {
            val response = get(endpoint = "?comment=$commentId")
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

    override suspend fun createLike(
        token: String,
        commentId: String
    ): Resource<String> = tryWithIoHandling {
        val response = post(
            endpoint = "/$commentId",
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

    override suspend fun deleteLike(
        token: String,
        commentId: String
    ): Resource<String> = tryWithIoHandling {
        val response = delete<Unit>(
            endpoint = "/$commentId",
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