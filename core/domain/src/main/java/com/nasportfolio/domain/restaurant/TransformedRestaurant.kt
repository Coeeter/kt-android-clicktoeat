package com.nasportfolio.domain.restaurant

import com.nasportfolio.domain.branch.Branch
import com.nasportfolio.domain.comment.Comment

data class TransformedRestaurant(
    val id: String,
    val name: String,
    val description: String,
    val imageUrl: String,
    val branches: List<Branch>,
    val comments: List<Comment>,
    val isFavoriteByCurrentUser: Boolean,
    val averageRating: Double,
    val ratingCount: Int
)