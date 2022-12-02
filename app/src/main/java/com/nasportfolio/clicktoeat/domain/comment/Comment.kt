package com.nasportfolio.clicktoeat.domain.comment

import com.google.gson.annotations.SerializedName
import com.nasportfolio.clicktoeat.domain.restaurant.Restaurant
import com.nasportfolio.clicktoeat.domain.user.User
import java.util.*

data class Comment(
    val id: String,
    val review: String,
    val rating: Int,
    val parentComment: String,
    @SerializedName("created_at") val createdAt: Date,
    @SerializedName("updated_at") val updatedAt: Date,
    val user: User,
    val restaurant: Restaurant
)
