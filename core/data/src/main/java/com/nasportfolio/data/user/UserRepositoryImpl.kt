package com.nasportfolio.data.user

import com.nasportfolio.data.CltLocalDatabase
import com.nasportfolio.data.user.local.SharedPreferenceDao
import com.nasportfolio.data.user.local.toExternalUser
import com.nasportfolio.data.user.local.toUserEntity
import com.nasportfolio.data.user.remote.RemoteUserDao
import com.nasportfolio.data.user.remote.dtos.LoginDto
import com.nasportfolio.data.user.remote.dtos.SignUpDto
import com.nasportfolio.data.user.remote.dtos.UpdateAccountDto
import com.nasportfolio.domain.user.User
import com.nasportfolio.domain.user.UserRepository
import com.nasportfolio.domain.utils.Resource
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val remoteUserDao: RemoteUserDao,
    private val sharedPreferenceDao: SharedPreferenceDao,
    cltLocalDatabase: CltLocalDatabase
) : UserRepository {
    private val localUserDao = cltLocalDatabase.getUserDao()

    override fun getToken(): Resource<String> =
        sharedPreferenceDao.getToken()

    override fun saveToken(token: String) =
        sharedPreferenceDao.saveToken(token)

    override fun removeToken() =
        sharedPreferenceDao.removeToken()

    override fun getAllUsers(fetchFromRemote: Boolean): Flow<Resource<List<User>>> = flow {
        val localUsers = localUserDao.getAllUsers()
        if (localUsers.first().isNotEmpty() && !fetchFromRemote) return@flow emitAll(
            localUserDao.getAllUsers()
                .map { list -> list.map { it.toExternalUser() } }
                .map { list -> Resource.Success(list) }
                .conflate()
        )
        val users = remoteUserDao.getAllUsers()
        if (users !is Resource.Success) return@flow emit(users)
        val userEntityList = users.result.map { it.toUserEntity() }
        localUserDao.deleteAllUsers()
        localUserDao.insertUsers(*userEntityList.toTypedArray())
        emitAll(
            localUserDao.getAllUsers()
                .map { list -> list.map { it.toExternalUser() } }
                .map { list -> Resource.Success(list) }
                .conflate()
        )
    }

    override fun getUserById(
        id: String,
        fetchFromRemote: Boolean
    ): Flow<Resource<User>> = localUserDao.getAllUsers()
        .mapNotNull { list -> list.find { it.userId == id } }
        .map { userEntity -> userEntity.toExternalUser() }
        .map { user -> Resource.Success(user) }

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
    ): Resource<String> {
        val user = remoteUserDao.validateToken(token)
        if (user !is Resource.Success) return Resource.Failure(
            (user as Resource.Failure).error
        )
        val deleteResult = remoteUserDao.deleteAccount(
            token = token,
            password = password
        )
        if (deleteResult !is Resource.Success) return deleteResult
        localUserDao.deleteUserById(id = user.result.id)
        return deleteResult
    }

    override suspend fun updateAccount(
        token: String,
        username: String?,
        email: String?,
        password: String?,
        image: ByteArray?,
        fcmToken: String?,
        deleteImage: Boolean?
    ): Resource<String> {
        val updateResult = remoteUserDao.updateAccount(
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
        if (updateResult !is Resource.Success) return Resource.Failure(
            (updateResult as Resource.Failure).error
        )
        val user = remoteUserDao.validateToken(token)
        if (user !is Resource.Success) return Resource.Failure(
            (user as Resource.Failure).error
        )
        localUserDao.insertUsers(user.result.toUserEntity())
        return updateResult
    }

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