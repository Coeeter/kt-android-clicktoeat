package com.nasportfolio.restaurant.comments

import com.nasportfolio.domain.comment.Comment

data class CommentScreenState(
    val restaurantId: String? = null,
    val comments: List<Comment> = emptyList(),
    val currentUserId: String? = null,
    val isLoading: Boolean = true,

    val review: String = "",
    val reviewError: String? = null,
    val rating: Int = 0,
    val ratingError: String? = null,
    val isCreating: Boolean = false,

    val commentBeingEdited: Comment? = null,
    val editingReview: String = "",
    val editingReviewError: String? = null,
    val editingRating: Int = 0,
    val editingRatingError: String? = null,
    val isEditSubmitting: Boolean = false,

    val isUpdated: Boolean = false,

    val isRefreshing: Boolean = false
)