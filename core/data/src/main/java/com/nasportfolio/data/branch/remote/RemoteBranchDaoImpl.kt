package com.nasportfolio.data.branch.remote

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nasportfolio.data.branch.remote.dtos.CreateBranchDto
import com.nasportfolio.data.common.DefaultMessageDto
import com.nasportfolio.data.common.EntityCreatedDto
import com.nasportfolio.data.utils.Constants.NO_RESPONSE
import com.nasportfolio.data.utils.tryWithIoHandling
import com.nasportfolio.domain.branch.Branch
import com.nasportfolio.domain.utils.Resource
import com.nasportfolio.domain.utils.ResourceError
import com.nasportfolio.network.Authorization
import com.nasportfolio.network.OkHttpDao
import com.nasportfolio.network.delegations.AuthorizationImpl
import com.nasportfolio.network.delegations.OkHttpDaoImpl
import okhttp3.OkHttpClient
import javax.inject.Inject

class RemoteBranchDaoImpl @Inject constructor(
    okHttpClient: OkHttpClient,
    gson: Gson
) : RemoteBranchDao,
    Authorization by AuthorizationImpl(),
    OkHttpDao by OkHttpDaoImpl(
        okHttpClient = okHttpClient,
        gson = gson,
        path = "/api/branches"
    ) {

    override suspend fun getAllBranches(): Resource<List<Branch>> =
        tryWithIoHandling {
            val (json, code) = get()
            json ?: return@tryWithIoHandling Resource.Failure(
                ResourceError.DefaultError(NO_RESPONSE)
            )
            return@tryWithIoHandling when (code) {
                200 -> Resource.Success(
                    gson.fromJson(
                        json,
                        object : TypeToken<List<Branch>>() {}.type
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

    override suspend fun createBranch(
        token: String,
        restaurantId: String,
        createBranchDto: CreateBranchDto
    ): Resource<String> = tryWithIoHandling {
        val (json, code) = post(
            endpoint = "/$restaurantId",
            body = createBranchDto,
            headers = createAuthorizationHeader(token)
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

    override suspend fun deleteBranch(
        token: String,
        branchId: String,
        restaurantId: String
    ): Resource<String> = tryWithIoHandling {
        val (json, code) = delete(
            endpoint = "/$branchId",
            body = mapOf("restaurantId" to restaurantId),
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
}