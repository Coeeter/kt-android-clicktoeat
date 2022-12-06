package com.nasportfolio.clicktoeat.data.comment.remote.dtos

data class CreateCommentDto(
    val review: String,
    val rating: Int,
    val parentComment: String? = null
)
