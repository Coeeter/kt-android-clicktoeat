package com.nasportfolio.clicktoeat.data.branch.remote

import com.nasportfolio.clicktoeat.data.branch.remote.dtos.CreateBranchDto
import com.nasportfolio.clicktoeat.data.common.Authorization
import com.nasportfolio.clicktoeat.data.common.converter.JsonConverter
import com.nasportfolio.clicktoeat.data.common.OkHttpDao
import com.nasportfolio.clicktoeat.data.common.delegations.AuthorizationImpl
import com.nasportfolio.clicktoeat.data.common.delegations.OkHttpDaoImpl
import com.nasportfolio.clicktoeat.data.common.dtos.DefaultMessageDto
import com.nasportfolio.clicktoeat.data.common.dtos.EntityCreatedDto
import com.nasportfolio.clicktoeat.data.utils.tryWithIoHandling
import com.nasportfolio.clicktoeat.domain.branch.Branch
import com.nasportfolio.clicktoeat.domain.utils.Resource
import com.nasportfolio.clicktoeat.domain.utils.ResourceError
import com.nasportfolio.clicktoeat.utils.Constants.UNABLE_GET_BODY_ERROR_MESSAGE
import com.nasportfolio.clicktoeat.utils.toJson
import okhttp3.OkHttpClient
import javax.inject.Inject

class RemoteBranchDaoImpl @Inject constructor(
    okHttpClient: OkHttpClient,
    converter: JsonConverter,
) : RemoteBranchDao,
    Authorization by AuthorizationImpl(),
    OkHttpDao by OkHttpDaoImpl(
        converter = converter,
        okHttpClient = okHttpClient,
        path = "/api/branches"
    ) {

    override suspend fun getAllBranches(): Resource<List<Branch>> =
        tryWithIoHandling {
            val response = get()
            val json = response.body?.toJson()
                ?: return@tryWithIoHandling Resource.Failure(
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

    override suspend fun createBranch(
        token: String,
        restaurantId: String,
        createBranchDto: CreateBranchDto
    ): Resource<String> = tryWithIoHandling {
        val response = post(
            endpoint = "/$restaurantId",
            body = createBranchDto,
            headers = createAuthorizationHeader(token)
        )
        val json = response.body?.toJson()
            ?: return@tryWithIoHandling Resource.Failure(
                ResourceError.Default(UNABLE_GET_BODY_ERROR_MESSAGE)
            )
        return@tryWithIoHandling when (response.code) {
            200 -> Resource.Success(
                converter.fromJson<EntityCreatedDto>(json).insertId
            )
            400 -> Resource.Failure(
                converter.fromJson<ResourceError.Field>(json)
            )
            else -> Resource.Failure(
                converter.fromJson<ResourceError.Default>(json)
            )
        }
    }

    override suspend fun deleteBranch(
        token: String,
        branchId: String,
        restaurantId: String
    ): Resource<String> = tryWithIoHandling {
        val response = delete(
            endpoint = "/$branchId",
            body = mapOf("restaurantId" to restaurantId),
            headers = createAuthorizationHeader(token)
        )
        val json = response.body?.toJson()
            ?: return@tryWithIoHandling Resource.Failure(
                ResourceError.Default(UNABLE_GET_BODY_ERROR_MESSAGE)
            )
        return@tryWithIoHandling when (response.code) {
            200 -> Resource.Success(
                converter.fromJson<DefaultMessageDto>(json).message
            )
            400 -> Resource.Failure(
                converter.fromJson<ResourceError.Field>(json)
            )
            else -> Resource.Failure(
                converter.fromJson<ResourceError.Default>(json)
            )
        }
    }
}