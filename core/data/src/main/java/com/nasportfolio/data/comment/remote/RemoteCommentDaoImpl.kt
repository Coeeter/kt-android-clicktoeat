package com.nasportfolio.data.comment.remote

import com.nasportfolio.data.common.DefaultMessageDto
import com.nasportfolio.data.common.EntityCreatedDto
import com.nasportfolio.data.comment.remote.dtos.CreateCommentDto
import com.nasportfolio.data.comment.remote.dtos.UpdateCommentDto
import com.nasportfolio.data.utils.Constants.NO_RESPONSE
import com.nasportfolio.data.utils.tryWithIoHandling
import com.nasportfolio.domain.comment.Comment
import com.nasportfolio.domain.utils.Resource
import com.nasportfolio.domain.utils.ResourceError
import com.nasportfolio.network.Authorization
import com.nasportfolio.network.JsonConverter
import com.nasportfolio.network.OkHttpDao
import com.nasportfolio.network.delegations.AuthorizationImpl
import com.nasportfolio.network.delegations.OkHttpDaoImpl
import okhttp3.OkHttpClient
import javax.inject.Inject

class RemoteCommentDaoImpl @Inject constructor(
    okHttpClient: OkHttpClient,
    jsonConverter: JsonConverter
) : RemoteCommentDao,
    Authorization by AuthorizationImpl(),
    OkHttpDao by OkHttpDaoImpl(
        okHttpClient = okHttpClient,
        converter = jsonConverter,
        path = "/api/comments"
    ) {

    override suspend fun getAllComments(): Resource<List<Comment>> =
        tryWithIoHandling {
            val (json, code) = get()
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

    override suspend fun getCommentsByUser(userId: String): Resource<List<Comment>> =
        tryWithIoHandling {
            val (json, code) = get(endpoint = "?user=$userId")
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

    override suspend fun getCommentsByRestaurant(restaurantId: String): Resource<List<Comment>> =
        tryWithIoHandling {
            val (json, code) = get(endpoint = "?restaurant=$restaurantId")
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

    override suspend fun createComment(
        token: String,
        restaurantId: String,
        createCommentDto: CreateCommentDto
    ): Resource<String> = tryWithIoHandling {
        val (json, code) = post(
            endpoint = "/$restaurantId",
            body = createCommentDto,
            headers = createAuthorizationHeader(token)
        )
        json ?: return@tryWithIoHandling Resource.Failure(
            ResourceError.DefaultError(NO_RESPONSE)
        )
        return@tryWithIoHandling when (code) {
            200 -> Resource.Success(
                converter.fromJson<EntityCreatedDto>(json).insertId
            )
            400 -> Resource.Failure(
                converter.fromJson<ResourceError.FieldError>(json)
            )
            else -> Resource.Failure(
                converter.fromJson<ResourceError.DefaultError>(json)
            )
        }
    }

    override suspend fun updateComment(
        token: String,
        commentId: String,
        updateCommentDto: UpdateCommentDto
    ): Resource<Comment> = tryWithIoHandling {
        val (json, code) = put(
            endpoint = "/$commentId",
            body = updateCommentDto,
            headers = createAuthorizationHeader(token)
        )
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

    override suspend fun deleteComment(token: String, commentId: String): Resource<String> =
        tryWithIoHandling {
            val (json, code) = delete<Unit>(
                endpoint = "/$commentId",
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