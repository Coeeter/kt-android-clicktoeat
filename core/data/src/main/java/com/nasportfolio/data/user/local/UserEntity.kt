package com.nasportfolio.data.user.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nasportfolio.domain.image.Image
import com.nasportfolio.domain.user.User

@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey
    val userId: String,
    val username: String,
    val email: String,
    @Embedded
    val image: Image? = null
)

fun UserEntity.toExternalUser() = User(
    id = userId,
    username = username,
    email = email,
    image = image
)

fun User.toUserEntity() = UserEntity(
    userId = id,
    username = username,
    email = email,
    image = image
)