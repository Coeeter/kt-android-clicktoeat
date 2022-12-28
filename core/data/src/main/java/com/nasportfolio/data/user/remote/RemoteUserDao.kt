package com.nasportfolio.data.user.remote

import com.nasportfolio.data.user.remote.dtos.LoginDto
import com.nasportfolio.data.user.remote.dtos.SignUpDto
import com.nasportfolio.data.user.remote.dtos.UpdateAccountDto
import com.nasportfolio.domain.user.User
import com.nasportfolio.domain.utils.Resource

interface RemoteUserDao {
    suspend fun getAllUsers(): Resource<List<User>>
    suspend fun getUserById(id: String): Resource<User>
    suspend fun validateToken(token: String): Resource<User>
    suspend fun forgotPassword(email: String): Resource<String>
    suspend fun validateCredential(e: String, c: String): Resource<String>
    suspend fun updateAccount(token: String, updateAccountDto: UpdateAccountDto): Resource<String>
    suspend fun deleteAccount(token: String, password: String): Resource<String>
    suspend fun login(loginDto: LoginDto): Resource<String>
    suspend fun signUp(signUpDto: SignUpDto): Resource<String>
}