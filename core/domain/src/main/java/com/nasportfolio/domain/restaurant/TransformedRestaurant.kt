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
    val favoriteSize: Int,
    val isFavoriteByCurrentUser: Boolean,
) {
    val averageRating: Double
        get() {
            if (ratingCount == 0) return 0.0
            return comments.sumOf { it.rating } / ratingCount.toDouble()
        }

    val ratingCount: Int
        get() = comments.size
}