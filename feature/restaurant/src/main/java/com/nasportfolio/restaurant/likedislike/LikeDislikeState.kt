package com.nasportfolio.restaurant.likedislike

import com.nasportfolio.domain.comment.Comment

data class LikeDislikeState(
    val comment: Comment? = null,
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val initialIndex: Int = 0
)