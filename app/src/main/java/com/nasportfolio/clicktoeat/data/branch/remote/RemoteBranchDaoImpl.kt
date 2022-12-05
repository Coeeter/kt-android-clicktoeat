package com.nasportfolio.clicktoeat.data.branch.remote

import com.google.gson.Gson
import com.nasportfolio.clicktoeat.data.branch.remote.dtos.CreateBranchDto
import com.nasportfolio.clicktoeat.data.common.dtos.DefaultMessageDto
import com.nasportfolio.clicktoeat.data.common.dtos.EntityCreatedDto
import com.nasportfolio.clicktoeat.data.utils.tryWithIoExceptionHandling
import com.nasportfolio.clicktoeat.domain.branch.Branch
import com.nasportfolio.clicktoeat.domain.utils.Resource
import com.nasportfolio.clicktoeat.domain.utils.ResourceError
import com.nasportfolio.clicktoeat.utils.Constants.UNABLE_GET_BODY_ERROR_MESSAGE
import com.nasportfolio.clicktoeat.utils.decodeFromJson
import com.nasportfolio.clicktoeat.utils.toJson
import okhttp3.OkHttpClient
import javax.inject.Inject

class RemoteBranchDaoImpl @Inject constructor(
    okHttpClient: OkHttpClient,
    gson: Gson
) : RemoteBranchDao(okHttpClient, gson) {
    override suspend fun getAllBranches(): Resource<List<Branch>> =
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

    override suspend fun createBranch(
        token: String,
        restaurantId: String,
        createBranchDto: CreateBranchDto
    ): Resource<String> = tryWithIoExceptionHandling {
        val response = post(
            endpoint = "/$restaurantId",
            body = createBranchDto,
            headers = mapOf(AUTHORIZATION to BEARER + token)
        )
        val json = response.body?.toJson()
            ?: return@tryWithIoExceptionHandling Resource.Failure(
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

    override suspend fun deleteBranch(
        token: String,
        branchId: String,
        restaurantId: String
    ): Resource<String> = tryWithIoExceptionHandling {
        val response = delete(
            endpoint = "/$branchId",
            body = mapOf("restaurantId" to restaurantId),
            headers = mapOf(AUTHORIZATION to BEARER + token)
        )
        val json = response.body?.toJson()
            ?: return@tryWithIoExceptionHandling Resource.Failure(
                ResourceError.Default(UNABLE_GET_BODY_ERROR_MESSAGE)
            )
        return@tryWithIoExceptionHandling when (response.code) {
            200 -> Resource.Success(
                gson.decodeFromJson<DefaultMessageDto>(json).message
            )
            400 -> Resource.Failure(
                gson.decodeFromJson<ResourceError.Field>(json)
            )
            else -> Resource.Failure(
                gson.decodeFromJson<ResourceError.Default>(json)
            )
        }
    }
}