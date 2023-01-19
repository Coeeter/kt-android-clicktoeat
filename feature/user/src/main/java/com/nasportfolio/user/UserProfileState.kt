package com.nasportfolio.user

import com.nasportfolio.domain.comment.Comment
import com.nasportfolio.domain.restaurant.TransformedRestaurant
import com.nasportfolio.domain.user.User

data class UserProfileState(
    val fromNav: Boolean = true,
    val loggedInUserId: String? = null,
    val isRefreshing: Boolean = false,
    val user: User? = null,
    val favRestaurants: List<TransformedRestaurant> = emptyList(),
    val comments: List<Comment> = emptyList(),
    val isRestaurantLoading: Boolean = true,
    val isCommentLoading: Boolean = true,
    val isUserLoading: Boolean = true,
)