package com.nasportfolio.clicktoeat.data.user.remote

import com.google.gson.Gson
import com.nasportfolio.clicktoeat.data.common.dtos.DefaultMessageDto
import com.nasportfolio.clicktoeat.data.user.remote.dtos.LoginDto
import com.nasportfolio.clicktoeat.data.user.remote.dtos.SignUpDto
import com.nasportfolio.clicktoeat.data.user.remote.dtos.TokenDto
import com.nasportfolio.clicktoeat.data.user.remote.dtos.UpdateAccountDto
import com.nasportfolio.clicktoeat.data.utils.tryWithIoExceptionHandling
import com.nasportfolio.clicktoeat.domain.user.User
import com.nasportfolio.clicktoeat.domain.utils.Resource
import com.nasportfolio.clicktoeat.domain.utils.ResourceError
import com.nasportfolio.clicktoeat.utils.Constants.UNABLE_GET_BODY_ERROR_MESSAGE
import com.nasportfolio.clicktoeat.utils.decodeFromJson
import com.nasportfolio.clicktoeat.utils.toJson
import okhttp3.OkHttpClient
import javax.inject.Inject

class RemoteUserDaoImpl @Inject constructor(
    okHttpClient: OkHttpClient,
    gson: Gson,
) : RemoteUserDao(okHttpClient, gson) {
    companion object {
        const val VALIDATE_TOKEN_ENDPOINT = "/validate-token"
        const val FORGOT_PASSWORD_ENDPOINT = "/forget-password"
        const val VALIDATE_CREDENTIAL_ENDPOINT = "/validate-credential"
        const val LOGIN_ENDPOINT = "/login"
        const val SIGN_UP_ENDPOINT = "/create-account"
    }

    override suspend fun getAllUsers(): Resource<List<User>> =
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

    override suspend fun getUserById(id: String): Resource<User> =
        tryWithIoExceptionHandling {
            val response = get(endpoint = "/$id")
            val json = response.body?.toJson()
                ?: return@tryWithIoExceptionHandling Resource.Failure(
                    ResourceError.Default("No user with id $id found")
                )
            return@tryWithIoExceptionHandling Resource.Success(
                gson.decodeFromJson(json)
            )
        }

    override suspend fun validateToken(token: String): Resource<User> =
        tryWithIoExceptionHandling {
            val response = get(
                endpoint = VALIDATE_TOKEN_ENDPOINT,
                headers = mapOf(AUTHORIZATION to BEARER + token)
            )
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

    override suspend fun forgotPassword(email: String): Resource<String> =
        tryWithIoExceptionHandling {
            val response = post(
                endpoint = FORGOT_PASSWORD_ENDPOINT,
                body = mapOf("email" to email),
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

    override suspend fun validateCredential(e: String, c: String): Resource<String> =
        tryWithIoExceptionHandling {
            val response = post(
                endpoint = VALIDATE_CREDENTIAL_ENDPOINT,
                body = mapOf("email" to e, "credential" to c)
            )
            val json = response.body?.toJson()
                ?: return@tryWithIoExceptionHandling Resource.Failure(
                    ResourceError.Default(UNABLE_GET_BODY_ERROR_MESSAGE)
                )
            return@tryWithIoExceptionHandling when (response.code) {
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
        }

    override suspend fun updateAccount(
        token: String,
        updateAccountDto: UpdateAccountDto
    ): Resource<String> = tryWithIoExceptionHandling {
        val response = put(
            body = updateAccountDto.copy(image = null),
            file = updateAccountDto.image,
            requestName = "image",
            headers = mapOf(AUTHORIZATION to BEARER + token)
        )
        val json = response.body?.toJson()
            ?: return@tryWithIoExceptionHandling Resource.Failure(
                ResourceError.Default(UNABLE_GET_BODY_ERROR_MESSAGE)
            )
        return@tryWithIoExceptionHandling when (response.code) {
            200 -> Resource.Success(
                gson.decodeFromJson<TokenDto>(json).token
            )
            else -> Resource.Failure(
                gson.decodeFromJson<ResourceError.Default>(json)
            )
        }
    }

    override suspend fun deleteAccount(
        token: String,
        password: String
    ): Resource<String> = tryWithIoExceptionHandling {
        val response = delete(
            body = mapOf("password" to password),
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

    override suspend fun login(loginDto: LoginDto): Resource<String> =
        tryWithIoExceptionHandling {
            val response = post(
                endpoint = LOGIN_ENDPOINT,
                body = loginDto
            )
            val json = response.body?.toJson()
                ?: return@tryWithIoExceptionHandling Resource.Failure(
                    ResourceError.Default(UNABLE_GET_BODY_ERROR_MESSAGE)
                )
            return@tryWithIoExceptionHandling when (response.code) {
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
        }

    override suspend fun signUp(signUpDto: SignUpDto): Resource<String> =
        tryWithIoExceptionHandling {
            val response = post(
                endpoint = SIGN_UP_ENDPOINT,
                body = signUpDto.copy(image = null),
                file = signUpDto.image,
                requestName = "image"
            )
            val json = response.body?.toJson()
                ?: return@tryWithIoExceptionHandling Resource.Failure(
                    ResourceError.Default(UNABLE_GET_BODY_ERROR_MESSAGE)
                )
            return@tryWithIoExceptionHandling when (response.code) {
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
        }

}