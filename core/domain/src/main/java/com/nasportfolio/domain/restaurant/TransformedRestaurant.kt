package com.nasportfolio.domain.restaurant

import com.nasportfolio.domain.branch.Branch
import com.nasportfolio.domain.comment.Comment
import com.nasportfolio.domain.user.User

data class TransformedRestaurant(
    val id: String,
    val name: String,
    val description: String,
    val imageUrl: String,
    val branches: List<Branch>,
    val comments: List<Comment>,
    val favoriteSize: Int,
    val isFavoriteByCurrentUser: Boolean,
    val averageRating: Double,
    val ratingCount: Int
)