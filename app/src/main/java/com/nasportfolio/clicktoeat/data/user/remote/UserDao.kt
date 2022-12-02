package com.nasportfolio.clicktoeat.data.user.remote

import com.google.gson.Gson
import com.nasportfolio.clicktoeat.data.common.OkHttpDao
import com.nasportfolio.clicktoeat.data.user.remote.dto.SignUpDto
import com.nasportfolio.clicktoeat.data.user.remote.dto.UpdateAccountDto
import com.nasportfolio.clicktoeat.domain.user.User
import com.nasportfolio.clicktoeat.domain.utils.Resource
import okhttp3.OkHttpClient

abstract class UserDao(
    okHttpClient: OkHttpClient,
    gson: Gson
) : OkHttpDao(okHttpClient, gson) {
    abstract suspend fun getAllUsers(): Resource<List<User>>
    abstract suspend fun getUserById(id: String): Resource<User>
    abstract suspend fun validateToken(): Resource<User>
    abstract suspend fun forgotPassword(email: String): Resource<String>
    abstract suspend fun validateCredential(e: String, c: String): Resource<String>
    abstract suspend fun updateAccount(updateAccountDto: UpdateAccountDto): Resource<String>
    abstract suspend fun deleteAccount(password: String): Resource<String>
    abstract suspend fun login(email: String, password: String): Resource<String>
    abstract suspend fun signUp(signUpDto: SignUpDto): Resource<String>
}