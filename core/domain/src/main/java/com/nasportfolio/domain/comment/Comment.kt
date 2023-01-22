package com.nasportfolio.domain.comment

import com.google.gson.annotations.SerializedName
import com.nasportfolio.domain.restaurant.Restaurant
import com.nasportfolio.domain.user.User
import java.util.*

data class Comment(
    val id: String,
    val review: String,
    val rating: Int,
    val parentComment: String? = null,
    @SerializedName("created_at") val createdAt: Date,
    @SerializedName("updated_at") val updatedAt: Date,
    val user: User,
    val restaurant: Restaurant,
    val likes: List<User> = emptyList(),
    val dislikes: List<User> = emptyList()
)
