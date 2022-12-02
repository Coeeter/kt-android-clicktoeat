package com.nasportfolio.clicktoeat.data.user.remote

import com.google.gson.Gson
import com.nasportfolio.clicktoeat.data.user.remote.dto.SignUpDto
import com.nasportfolio.clicktoeat.data.user.remote.dto.TokenDto
import com.nasportfolio.clicktoeat.data.user.remote.dto.UpdateAccountDto
import com.nasportfolio.clicktoeat.domain.user.User
import com.nasportfolio.clicktoeat.domain.utils.Resource
import com.nasportfolio.clicktoeat.domain.utils.ResourceError
import com.nasportfolio.clicktoeat.utils.Constants.BASE_URL
import com.nasportfolio.clicktoeat.utils.Constants.UNABLE_GET_BODY_ERROR_MESSAGE
import com.nasportfolio.clicktoeat.utils.await
import com.nasportfolio.clicktoeat.utils.decodeFromJson
import com.nasportfolio.clicktoeat.utils.toJson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import javax.inject.Inject

class UserDaoImpl @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val gson: Gson,
) : UserDao {
    companion object {
        const val PATH = "/apiF/users"
        val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaTypeOrNull()
        val IMAGE_MEDIA_TYPE = "image/*".toMediaTypeOrNull()
    }

    override suspend fun getAllUsers(): Resource<List<User>> {
        val request = Request.Builder()
            .url("$BASE_URL/$PATH")
            .build()
        try {
            val response = okHttpClient.newCall(request).await()
            val json = response.body?.toJson()
            json ?: return Resource.Failure(
                ResourceError.Default(UNABLE_GET_BODY_ERROR_MESSAGE)
            )
            if (response.code == 200) return Resource.Success(
                gson.decodeFromJson(json)
            )
            return Resource.Failure(
                gson.decodeFromJson<ResourceError.Default>(json)
            )
        } catch (e: IOException) {
            return Resource.Failure(
                ResourceError.Default(e.message.toString())
            )
        }
    }

    override suspend fun getUserById(id: String): Resource<User> {
        val request = Request.Builder()
            .url("$BASE_URL/$PATH/$id")
            .build()
        try {
            val response = okHttpClient.newCall(request).await()
            val json = response.body?.toJson() ?: return Resource.Failure(
                ResourceError.Default("No user with id $id found")
            )
            return Resource.Success(
                gson.decodeFromJson(json)
            )
        } catch (e: IOException) {
            return Resource.Failure(
                ResourceError.Default(e.message.toString())
            )
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
        val map = hashMapOf("email" to email, "password" to password)
        val body = gson.toJson(map).toRequestBody(JSON_MEDIA_TYPE)
        val request = Request.Builder()
            .url("$BASE_URL/$PATH/login")
            .post(body)
            .build()
        try {
            val response = okHttpClient.newCall(request).await()
            val json = response.body?.toJson() ?: return Resource.Failure(
                ResourceError.Default(UNABLE_GET_BODY_ERROR_MESSAGE)
            )
            if (response.code == 200) return Resource.Success(
                gson.decodeFromJson<TokenDto>(json).token
            )
            if (response.code == 400) return Resource.Failure(
                gson.decodeFromJson<ResourceError.Field>(json)
            )
            return Resource.Failure(
                gson.decodeFromJson<ResourceError.Default>(json)
            )
        } catch (e: IOException) {
            return Resource.Failure(
                ResourceError.Default(e.message.toString())
            )
        }
    }

    override suspend fun signUp(signUpDto: SignUpDto): Resource<String> {
        val body = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("username", signUpDto.username)
            .addFormDataPart("email", signUpDto.email)
            .addFormDataPart("fcmToken", signUpDto.fcmToken)
            .addFormDataPart("password", signUpDto.password)
        signUpDto.image?.let {
            body.addFormDataPart(
                "image",
                it.name,
                it.asRequestBody(IMAGE_MEDIA_TYPE)
            )
        }
        val request = Request.Builder()
            .url("$BASE_URL/$PATH/create-account")
            .post(body.build())
            .build()
        try {
            val response = okHttpClient.newCall(request).await()
            val json = response.body?.toJson() ?: return Resource.Failure(
                ResourceError.Default(UNABLE_GET_BODY_ERROR_MESSAGE)
            )
            if (response.code == 200) return Resource.Success(
                gson.decodeFromJson<TokenDto>(json).token
            )
            if (response.code == 400) return Resource.Failure(
                gson.decodeFromJson<ResourceError.Field>(json)
            )
            return Resource.Failure(
                gson.decodeFromJson<ResourceError.Default>(json)
            )
        } catch (e: IOException) {
            return Resource.Failure(
                ResourceError.Default(e.message.toString())
            )
        }
    }
}