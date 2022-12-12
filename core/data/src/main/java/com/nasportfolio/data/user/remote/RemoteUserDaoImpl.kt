package com.nasportfolio.data.user.remote

import com.nasportfolio.data.common.DefaultMessageDto
import com.nasportfolio.data.user.remote.dtos.LoginDto
import com.nasportfolio.data.user.remote.dtos.SignUpDto
import com.nasportfolio.data.user.remote.dtos.TokenDto
import com.nasportfolio.data.user.remote.dtos.UpdateAccountDto
import com.nasportfolio.data.utils.Constants.NO_RESPONSE
import com.nasportfolio.data.utils.tryWithIoHandling
import com.nasportfolio.domain.user.User
import com.nasportfolio.domain.utils.Resource
import com.nasportfolio.domain.utils.ResourceError
import com.nasportfolio.network.Authorization
import com.nasportfolio.network.JsonConverter
import com.nasportfolio.network.OkHttpDao
import com.nasportfolio.network.delegations.AuthorizationImpl
import com.nasportfolio.network.delegations.OkHttpDaoImpl
import okhttp3.OkHttpClient
import javax.inject.Inject

class RemoteUserDaoImpl @Inject constructor(
    okHttpClient: OkHttpClient,
    converter: JsonConverter,
) : RemoteUserDao,
    Authorization by AuthorizationImpl(),
    OkHttpDao by OkHttpDaoImpl(
        converter = converter,
        okHttpClient = okHttpClient,
        path = "/api/users"
    ) {

    companion object {
        const val VALIDATE_TOKEN_ENDPOINT = "/validate-token"
        const val FORGOT_PASSWORD_ENDPOINT = "/forget-password"
        const val VALIDATE_CREDENTIAL_ENDPOINT = "/validate-credential"
        const val LOGIN_ENDPOINT = "/login"
        const val SIGN_UP_ENDPOINT = "/create-account"
    }

    override suspend fun getAllUsers(): Resource<List<User>> =
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

    override suspend fun getUserById(id: String): Resource<User> =
        tryWithIoHandling {
            val (json, code) = get(endpoint = "/$id")
            json ?: return@tryWithIoHandling Resource.Failure(
                ResourceError.DefaultError("No user with id $id found")
            )
            return@tryWithIoHandling Resource.Success(
                converter.fromJson(json)
            )
        }

    override suspend fun validateToken(token: String): Resource<User> =
        tryWithIoHandling {
            val (json, code) = get(
                endpoint = VALIDATE_TOKEN_ENDPOINT,
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

    override suspend fun forgotPassword(email: String): Resource<String> =
        tryWithIoHandling {
            val (json, code) = post(
                endpoint = FORGOT_PASSWORD_ENDPOINT,
                body = mapOf("email" to email),
            )
            json ?: return@tryWithIoHandling Resource.Failure(
                ResourceError.DefaultError(NO_RESPONSE)
            )
            return@tryWithIoHandling when (code) {
                200 -> Resource.Success(
                    converter.fromJson<DefaultMessageDto>(json).message
                )
                400 -> Resource.Failure(
                    converter.fromJson<ResourceError.FieldError>(json)
                )
                else -> Resource.Failure(
                    converter.fromJson<ResourceError.DefaultError>(json)
                )
            }
        }

    override suspend fun validateCredential(e: String, c: String): Resource<String> =
        tryWithIoHandling {
            val (json, code) = post(
                endpoint = VALIDATE_CREDENTIAL_ENDPOINT,
                body = mapOf("email" to e, "credential" to c)
            )
            json ?: return@tryWithIoHandling Resource.Failure(
                ResourceError.DefaultError(NO_RESPONSE)
            )
            return@tryWithIoHandling when (code) {
                200 -> Resource.Success(
                    converter.fromJson<TokenDto>(json).token
                )
                400 -> Resource.Failure(
                    converter.fromJson<ResourceError.FieldError>(json)
                )
                else -> Resource.Failure(
                    converter.fromJson<ResourceError.DefaultError>(json)
                )
            }
        }

    override suspend fun updateAccount(
        token: String,
        updateAccountDto: UpdateAccountDto
    ): Resource<String> = tryWithIoHandling {
        val (json, code) = put(
            body = updateAccountDto.copy(image = null),
            file = updateAccountDto.image,
            requestName = "image",
            headers = createAuthorizationHeader(token)
        )
        json ?: return@tryWithIoHandling Resource.Failure(
            ResourceError.DefaultError(NO_RESPONSE)
        )
        return@tryWithIoHandling when (code) {
            200 -> Resource.Success(
                converter.fromJson<TokenDto>(json).token
            )
            else -> Resource.Failure(
                converter.fromJson<ResourceError.DefaultError>(json)
            )
        }
    }

    override suspend fun deleteAccount(
        token: String,
        password: String
    ): Resource<String> = tryWithIoHandling {
        val (json, code) = delete(
            body = mapOf("password" to password),
            headers = createAuthorizationHeader(token)
        )
        json ?: return@tryWithIoHandling Resource.Failure(
            ResourceError.DefaultError(NO_RESPONSE)
        )
        return@tryWithIoHandling when (code) {
            200 -> Resource.Success(
                converter.fromJson<DefaultMessageDto>(json).message
            )
            400 -> Resource.Failure(
                converter.fromJson<ResourceError.FieldError>(json)
            )
            else -> Resource.Failure(
                converter.fromJson<ResourceError.DefaultError>(json)
            )
        }
    }

    override suspend fun login(loginDto: LoginDto): Resource<String> =
        tryWithIoHandling {
            val (json, code) = post(
                endpoint = LOGIN_ENDPOINT,
                body = loginDto
            )
            json ?: return@tryWithIoHandling Resource.Failure(
                ResourceError.DefaultError(NO_RESPONSE)
            )
            return@tryWithIoHandling when (code) {
                200 -> Resource.Success(
                    converter.fromJson<TokenDto>(json).token
                )
                400 -> Resource.Failure(
                    converter.fromJson<ResourceError.FieldError>(json)
                )
                else -> Resource.Failure(
                    converter.fromJson<ResourceError.DefaultError>(json)
                )
            }
        }

    override suspend fun signUp(signUpDto: SignUpDto): Resource<String> =
        tryWithIoHandling {
            val (json, code) = post(
                endpoint = SIGN_UP_ENDPOINT,
                body = signUpDto.copy(image = null),
                file = signUpDto.image,
                requestName = "image"
            )
            json ?: return@tryWithIoHandling Resource.Failure(
                ResourceError.DefaultError(NO_RESPONSE)
            )
            return@tryWithIoHandling when (code) {
                200 -> Resource.Success(
                    converter.fromJson<TokenDto>(json).token
                )
                400 -> Resource.Failure(
                    converter.fromJson<ResourceError.FieldError>(json)
                )
                else -> Resource.Failure(
                    converter.fromJson<ResourceError.DefaultError>(json)
                )
            }
        }

}