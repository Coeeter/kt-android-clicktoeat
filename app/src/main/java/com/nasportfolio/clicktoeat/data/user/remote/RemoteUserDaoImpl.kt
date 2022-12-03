package com.nasportfolio.clicktoeat.data.user.remote

import com.google.gson.Gson
import com.nasportfolio.clicktoeat.data.common.dtos.DefaultMessageDto
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

class RemoteUserDaoImpl @Inject constructor(
    okHttpClient: OkHttpClient,
    gson: Gson,
) : RemoteUserDao(okHttpClient, gson) {
    companion object {
        const val VALIDATE_TOKEN_ENDPOINT = "/validate-token"
        const val FORGOT_PASSWORD_ENDPOINT = "/forget-password"
        const val VALIDATE_CREDENTIAL_ENDPOINT = "/validate-credential"
    }

    override suspend fun getAllUsers(): Resource<List<User>> {
        try {
            val response = get()
            val json = response.body?.toJson()
            json ?: return Resource.Failure(
                ResourceError.Default(UNABLE_GET_BODY_ERROR_MESSAGE)
            )
            return when (response.code) {
                200 -> Resource.Success(
                    gson.decodeFromJson(json)
                )
                else -> Resource.Failure(
                    gson.decodeFromJson<ResourceError.Default>(json)
                )
            }
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

    override suspend fun validateToken(token: String): Resource<User> {
        try {
            val response = get(
                endpoint = VALIDATE_TOKEN_ENDPOINT,
                headers = mapOf(AUTHORIZATION to BEARER + token)
            )
            val json = response.body?.toJson() ?: return Resource.Failure(
                ResourceError.Default(UNABLE_GET_BODY_ERROR_MESSAGE)
            )
            return when (response.code) {
                200 -> Resource.Success(
                    gson.decodeFromJson(json)
                )
                else -> Resource.Failure(
                    gson.decodeFromJson<ResourceError.Default>(json)
                )
            }
        } catch (e: IOException) {
            return Resource.Failure(
                ResourceError.Default(e.message.toString())
            )
        }
    }

    override suspend fun forgotPassword(email: String): Resource<String> {
        try {
            val response = post(
                endpoint = FORGOT_PASSWORD_ENDPOINT,
                body = mapOf("email" to email),
            )
            val json = response.body?.toJson() ?: return Resource.Failure(
                ResourceError.Default(UNABLE_GET_BODY_ERROR_MESSAGE)
            )
            return when (response.code) {
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
        } catch (e: IOException) {
            return Resource.Failure(
                ResourceError.Default(e.message.toString())
            )
        }
    }

    override suspend fun validateCredential(e: String, c: String): Resource<String> {
        try {
            val response = post(
                endpoint = VALIDATE_CREDENTIAL_ENDPOINT,
                body = mapOf("email" to e, "credential" to c)
            )
            val json = response.body?.toJson() ?: return Resource.Failure(
                ResourceError.Default(UNABLE_GET_BODY_ERROR_MESSAGE)
            )
            return when (response.code) {
                200 -> Resource.Success(
                    gson.decodeFromJson<TokenDto>(json).token
                )
                400 -> Resource.Failure(
                    gson.decodeFromJson<ResourceError.Field>(json)
                )
                else -> Resource.Failure(
                    gson.decodeFromJson<ResourceError.Default>(json)
                )
            }
        } catch (e: IOException) {
            return Resource.Failure(
                ResourceError.Default(e.message.toString())
            )
        }
    }

    override suspend fun updateAccount(
        token: String,
        updateAccountDto: UpdateAccountDto
    ): Resource<String> {
        try {
            val response = put(
                body = updateAccountDto.copy(image = null),
                file = updateAccountDto.image,
                requestName = "image",
                headers = mapOf(AUTHORIZATION to BEARER + token)
            )
            val json = response.body?.toJson() ?: return Resource.Failure(
                ResourceError.Default(UNABLE_GET_BODY_ERROR_MESSAGE)
            )
            return when (response.code) {
                200 -> Resource.Success(
                    gson.decodeFromJson<TokenDto>(json).token
                )
                else -> Resource.Failure(
                    gson.decodeFromJson<ResourceError.Default>(json)
                )
            }
        } catch (e: IOException) {
            return Resource.Failure(
                ResourceError.Default(e.message.toString())
            )
        }
    }

    override suspend fun deleteAccount(
        token: String,
        password: String
    ): Resource<String> {
        try {
            val response = delete(
                body = mapOf("password" to password),
                headers = mapOf(AUTHORIZATION to BEARER + token)
            )
            val json = response.body?.toJson() ?: return Resource.Failure(
                ResourceError.Default(UNABLE_GET_BODY_ERROR_MESSAGE)
            )
            return when (response.code) {
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
        } catch (e: IOException) {
            return Resource.Failure(
                ResourceError.Default(e.message.toString())
            )
        }
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
            return when (response.code) {
                200 -> Resource.Success(
                    gson.decodeFromJson<TokenDto>(json).token
                )
                400 -> Resource.Failure(
                    gson.decodeFromJson<ResourceError.Field>(json)
                )
                else -> Resource.Failure(
                    gson.decodeFromJson<ResourceError.Default>(json)
                )
            }
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
                file = signUpDto.image,
                requestName = "image"
            )
            val json = response.body?.toJson() ?: return Resource.Failure(
                ResourceError.Default(UNABLE_GET_BODY_ERROR_MESSAGE)
            )
            return when (response.code) {
                200 -> Resource.Success(
                    gson.decodeFromJson<TokenDto>(json).token
                )
                400 -> Resource.Failure(
                    gson.decodeFromJson<ResourceError.Field>(json)
                )
                else -> Resource.Failure(
                    gson.decodeFromJson<ResourceError.Default>(json)
                )
            }
        } catch (e: IOException) {
            return Resource.Failure(
                ResourceError.Default(e.message.toString())
            )
        }
    }
}