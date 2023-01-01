package com.nasportfolio.domain.user

import com.nasportfolio.domain.utils.Resource

interface UserRepository {
    fun getToken(): Resource<String>
    fun saveToken(token: String)
    fun removeToken()

    suspend fun getAllUsers(): Resource<List<User>>
    suspend fun getUserById(id: String): Resource<User>
    suspend fun validateToken(token: String): Resource<User>
    suspend fun forgotPassword(email: String): Resource<String>
    suspend fun validateCredential(tokenizedEmail: String, credential: String): Resource<String>
    suspend fun deleteAccount(token: String, password: String): Resource<String>

    suspend fun updateAccount(
        token: String, username: String? = null,
        email: String? = null,
        password: String? = null,
        image: ByteArray? = null,
        fcmToken: String? = null,
        deleteImage: Boolean? = null,
    ): Resource<String>

    suspend fun login(
        email: String,
        password: String
    ): Resource<String>

    suspend fun signUp(
        username: String,
        email: String,
        password: String,
        fcmToken: String,
        image: ByteArray? = null,
    ): Resource<String>
}