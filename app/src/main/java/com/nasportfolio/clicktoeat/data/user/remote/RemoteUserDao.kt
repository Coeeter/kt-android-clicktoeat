package com.nasportfolio.clicktoeat.data.user.remote

import com.google.gson.Gson
import com.nasportfolio.clicktoeat.data.common.OkHttpDao
import com.nasportfolio.clicktoeat.data.user.remote.dto.LoginDto
import com.nasportfolio.clicktoeat.data.user.remote.dto.SignUpDto
import com.nasportfolio.clicktoeat.data.user.remote.dto.UpdateAccountDto
import com.nasportfolio.clicktoeat.domain.user.User
import com.nasportfolio.clicktoeat.domain.utils.Resource
import okhttp3.OkHttpClient

abstract class RemoteUserDao(
    okHttpClient: OkHttpClient,
    gson: Gson
) : OkHttpDao(okHttpClient, gson, "/api/users") {
    abstract suspend fun getAllUsers(): Resource<List<User>>
    abstract suspend fun getUserById(id: String): Resource<User>
    abstract suspend fun validateToken(token: String): Resource<User>
    abstract suspend fun forgotPassword(email: String): Resource<String>
    abstract suspend fun validateCredential(e: String, c: String): Resource<String>
    abstract suspend fun updateAccount(token: String, updateAccountDto: UpdateAccountDto): Resource<String>
    abstract suspend fun deleteAccount(token: String, password: String): Resource<String>
    abstract suspend fun login(loginDto: LoginDto): Resource<String>
    abstract suspend fun signUp(signUpDto: SignUpDto): Resource<String>
}