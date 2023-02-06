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
    val favoriteUsers: List<User>,
) {
    val favoriteSize: Int
        get() = favoriteUsers.size

    val averageRating: Double
        get() {
            if (ratingCount == 0) return 0.0
            return comments.sumOf { it.rating } / ratingCount.toDouble()
        }

    val ratingCount: Int
        get() = comments.size
}