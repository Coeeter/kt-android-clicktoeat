package com.nasportfolio.clicktoeat.data.user.remote

import com.google.gson.Gson
import com.nasportfolio.clicktoeat.data.user.remote.dto.LoginDto
import com.nasportfolio.clicktoeat.data.user.remote.dto.SignUpDto
import com.nasportfolio.clicktoeat.data.user.remote.dto.TokenDto
import com.nasportfolio.clicktoeat.data.user.remote.dto.UpdateAccountDto
import com.nasportfolio.clicktoeat.domain.user.User
import com.nasportfolio.clicktoeat.domain.utils.Resource
import com.nasportfolio.clicktoeat.domain.utils.ResourceError
import com.nasportfolio.clicktoeat.utils.Constants.UNABLE_GET_BODY_ERROR_MESSAGE
import com.nasportfolio.clicktoeat.utils.decodeFromJson
import com.nasportfolio.clicktoeat.utils.toJson
import okhttp3.OkHttpClient
import java.io.IOException
import javax.inject.Inject

class UserDaoImpl @Inject constructor(
    okHttpClient: OkHttpClient,
    gson: Gson,
) : UserDao(okHttpClient, gson) {

    override suspend fun getAllUsers(): Resource<List<User>> {
        try {
            val response = get()
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
        try {
            val response = get(endpoint = "/$id")
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

    override suspend fun login(loginDto: LoginDto): Resource<String> {
        try {
            val response = post(
                endpoint = "/login",
                body = loginDto
            )
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
        try {
            val response = post(
                endpoint = "/create-account",
                body = signUpDto.copy(image = null),
                image = signUpDto.image,
                imageName = "image"
            )
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