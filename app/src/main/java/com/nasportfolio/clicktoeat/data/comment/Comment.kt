package com.nasportfolio.clicktoeat.data.comment

import com.google.gson.annotations.SerializedName
import com.nasportfolio.clicktoeat.data.restaurant.Restaurant
import com.nasportfolio.clicktoeat.data.user.User
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
