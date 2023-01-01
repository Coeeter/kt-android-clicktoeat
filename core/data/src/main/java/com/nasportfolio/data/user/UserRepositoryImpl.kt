package com.nasportfolio.data.user

import com.nasportfolio.data.user.local.SharedPreferenceDao
import com.nasportfolio.data.user.remote.RemoteUserDao
import com.nasportfolio.data.user.remote.dtos.LoginDto
import com.nasportfolio.data.user.remote.dtos.SignUpDto
import com.nasportfolio.data.user.remote.dtos.UpdateAccountDto
import com.nasportfolio.domain.user.User
import com.nasportfolio.domain.user.UserRepository
import com.nasportfolio.domain.utils.Resource
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val remoteUserDao: RemoteUserDao,
    private val sharedPreferenceDao: SharedPreferenceDao
) : UserRepository {
    override fun getToken(): Resource<String> =
        sharedPreferenceDao.getToken()

    override fun saveToken(token: String) =
        sharedPreferenceDao.saveToken(token)

    override fun removeToken() =
        sharedPreferenceDao.removeToken()

    override suspend fun getAllUsers(): Resource<List<User>> =
        remoteUserDao.getAllUsers()

    override suspend fun getUserById(id: String): Resource<User> =
        remoteUserDao.getUserById(id = id)

    override suspend fun validateToken(token: String): Resource<User> =
        remoteUserDao.validateToken(token = token)

    override suspend fun forgotPassword(email: String): Resource<String> =
        remoteUserDao.forgotPassword(email = email)

    override suspend fun validateCredential(
        tokenizedEmail: String,
        credential: String
    ): Resource<String> = remoteUserDao.validateCredential(
        e = tokenizedEmail,
        c = credential
    )

    override suspend fun deleteAccount(
        token: String,
        password: String
    ): Resource<String> = remoteUserDao.deleteAccount(
        token = token,
        password = password
    )

    override suspend fun updateAccount(
        token: String,
        username: String?,
        email: String?,
        password: String?,
        image: ByteArray?,
        fcmToken: String?,
        deleteImage: Boolean?
    ): Resource<String> = remoteUserDao.updateAccount(
        token = token,
        updateAccountDto = UpdateAccountDto(
            username = username,
            email = email,
            password = password,
            image = image,
            fcmToken = fcmToken,
            deleteImage = deleteImage
        )
    )

    override suspend fun login(
        email: String,
        password: String
    ): Resource<String> = remoteUserDao.login(
        loginDto = LoginDto(
            email = email,
            password = password
        )
    )

    override suspend fun signUp(
        username: String,
        email: String,
        password: String,
        fcmToken: String,
        image: ByteArray?
    ): Resource<String> = remoteUserDao.signUp(
        signUpDto = SignUpDto(
            username = username,
            email = email,
            password = password,
            fcmToken = fcmToken,
            image = image
        )
    )

}