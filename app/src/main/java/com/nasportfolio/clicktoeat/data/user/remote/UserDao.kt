package com.nasportfolio.clicktoeat.data.user.remote

import com.nasportfolio.clicktoeat.data.user.User
import com.nasportfolio.clicktoeat.data.user.remote.dto.SignUpDto
import com.nasportfolio.clicktoeat.data.user.remote.dto.UpdateAccountDto
import com.nasportfolio.clicktoeat.utils.Resource

interface UserDao {
    suspend fun getAllUsers(): Resource<List<User>>
    suspend fun getUserById(id: String): Resource<User>
    suspend fun validateToken(): Resource<User>
    suspend fun forgotPassword(email: String): Resource<String>
    suspend fun validateCredential(e: String, c: String): Resource<String>
    suspend fun updateAccount(updateAccountDto: UpdateAccountDto): Resource<String>
    suspend fun deleteAccount(password: String): Resource<String>
    suspend fun login(email: String, password: String): Resource<String>
    suspend fun signUp(signUpDto: SignUpDto): Resource<String>
}