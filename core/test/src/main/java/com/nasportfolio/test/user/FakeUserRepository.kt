package com.nasportfolio.test.user

import com.nasportfolio.domain.image.Image
import com.nasportfolio.domain.user.User
import com.nasportfolio.domain.user.UserRepository
import com.nasportfolio.domain.utils.Resource
import com.nasportfolio.domain.utils.ResourceError
import java.util.*

class FakeUserRepository : UserRepository {
    var token: String? = UUID.randomUUID().toString()
    var users: List<User> = emptyList()

    init {
        repeat(10) {
            users = users.toMutableList().apply {
                val user = User(
                    id = it.toString(),
                    username = "username$it",
                    email = "$it${10 - it}@gmail.com",
                    image = null
                )
                add(user)
            }
        }
    }

    override fun getToken(): Resource<String> {
        return token?.let {
            Resource.Success(it)
        } ?: Resource.Failure(
            ResourceError.DefaultError(
                error = "Must be logged in to do this task"
            )
        )
    }

    override fun saveToken(token: String) {
        this.token = token
    }

    override fun removeToken() {
        token = null
    }

    override suspend fun getAllUsers(): Resource<List<User>> {
        return Resource.Success(users)
    }

    override suspend fun getUserById(id: String): Resource<User> {
        return users.find { it.id == id }?.let {
            Resource.Success(it)
        } ?: Resource.Failure(
            ResourceError.DefaultError("Cannot find user with id $id")
        )
    }

    override suspend fun validateToken(token: String): Resource<User> {
        if (this.token != token) return Resource.Failure(
            ResourceError.DefaultError("Invalid Token")
        )
        return Resource.Success(users.last())
    }

    override suspend fun forgotPassword(email: String): Resource<String> {
        return Resource.Success("Sent email successfully")
    }

    override suspend fun validateCredential(
        tokenizedEmail: String,
        credential: String
    ): Resource<String> {
        return token?.let {
            Resource.Success(it)
        } ?: Resource.Failure(ResourceError.DefaultError("Invalid token"))
    }

    override suspend fun deleteAccount(token: String, password: String): Resource<String> {
        if (this.token != token) return Resource.Failure(
            ResourceError.DefaultError("Invalid token")
        )
        val account = users.last()
        users = users.filter { it.id != account.id }
        return Resource.Success("Successfully deleted account")
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
        if (this.token != token) return Resource.Failure(
            ResourceError.DefaultError("Invalid token")
        )
        val user = users.last()
        var updatedImage = image?.let {
            Image(id = 0, key = "", url = "")
        } ?: user.image
        if (deleteImage == true) updatedImage = null
        val updated = user.copy(
            username = username ?: user.username,
            email = email ?: user.email,
            image = updatedImage
        )
        users = users.toMutableList().apply {
            val index = map { it.id }.indexOf(updated.id)
            set(index, updated)
        }
        return Resource.Success("Updated account with id ${updated.id}")
    }

    override suspend fun login(email: String, password: String): Resource<String> {
        val account = users.find { it.email == email } ?: return Resource.Failure(
            ResourceError.DefaultError("Account does not exist")
        )
        users = users.toMutableList().apply {
            val index = map { it.id }.indexOf(account.id)
            removeAt(index)
            add(account)
        }
        return Resource.Success(
            UUID.randomUUID().toString()
        )
    }

    override suspend fun signUp(
        username: String,
        email: String,
        password: String,
        fcmToken: String,
        image: ByteArray?
    ): Resource<String> {
        users = users.toMutableList().apply {
            add(
                User(
                    username = username,
                    email = email,
                    image = null,
                    id = users.size.toString()
                )
            )
        }
        return Resource.Success(UUID.randomUUID().toString())
    }
}