package com.nasportfolio.clicktoeat.data.comment

import com.google.gson.GsonBuilder

data class Comment(
    val id: String,
    val review: String,
    val rating: Int,
    val parentComment: String,
)
