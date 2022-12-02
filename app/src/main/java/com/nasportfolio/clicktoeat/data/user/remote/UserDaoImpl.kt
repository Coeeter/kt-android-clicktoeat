package com.nasportfolio.clicktoeat.data.user.remote

import com.google.gson.Gson
import com.nasportfolio.clicktoeat.data.common.DefaultErrorDto
import com.nasportfolio.clicktoeat.data.user.User
import com.nasportfolio.clicktoeat.data.user.remote.dto.SignUpDto
import com.nasportfolio.clicktoeat.data.user.remote.dto.UpdateAccountDto
import com.nasportfolio.clicktoeat.domain.common.exceptions.NoNetworkException
import com.nasportfolio.clicktoeat.utils.Constants.BASE_URL
import com.nasportfolio.clicktoeat.utils.Constants.UNABLE_GET_BODY_ERROR_MESSAGE
import com.nasportfolio.clicktoeat.utils.Resource
import com.nasportfolio.clicktoeat.utils.await
import com.nasportfolio.clicktoeat.utils.decodeFromJson
import com.nasportfolio.clicktoeat.utils.toJson
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject

class UserDaoImpl @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val gson: Gson,
) : UserDao {
    companion object {
        const val PATH = "/api/users"
    }

    override suspend fun getAllUsers(): Resource<List<User>> {
        val request = Request.Builder()
            .url("$BASE_URL/$PATH")
            .build()
        try {
            val response = okHttpClient.newCall(request).await()
            val json = response.body?.toJson()
            json ?: return Resource.Failure(UNABLE_GET_BODY_ERROR_MESSAGE)
            if (response.code == 200)
                return Resource.Success(
                    gson.decodeFromJson(json)
                )
            val errorDto = gson.decodeFromJson<DefaultErrorDto>(json)
            return Resource.Failure(errorDto.error)
        } catch (e: NoNetworkException) {
            return Resource.Failure(e.message.toString())
        }
    }

    override suspend fun getUserById(id: String): Resource<User> {
        val request = Request.Builder()
            .url("$BASE_URL/$PATH/$id")
            .build()
        try {
            val response = okHttpClient.newCall(request).await()
            val json = response.body?.toJson() ?: return Resource.Failure(
                "No user with id $id found"
            )
            return Resource.Success(
                gson.decodeFromJson(json)
            )
        } catch (e: NoNetworkException) {
            return Resource.Failure(e.message.toString())
        }
    }

    override suspend fun validateToken(): Resource<User> {
        TODO("Not yet implemented")
    }

    override suspend fun forgotPassword(email: String): Resource<String> {
        TODO("Not yet implemented")
    }

    override suspend fun validateCredential(e: String, c: String): Resource<String> {
        TODO("Not yet implemented")
    }

    override suspend fun updateAccount(updateAccountDto: UpdateAccountDto): Resource<String> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAccount(password: String): Resource<String> {
        TODO("Not yet implemented")
    }

    override suspend fun login(email: String, password: String): Resource<String> {
        TODO("Not yet implemented")
    }

    override suspend fun signUp(signUpDto: SignUpDto): Resource<String> {
        TODO("Not yet implemented")
    }
}