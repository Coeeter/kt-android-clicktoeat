package com.nasportfolio.restaurant.details

import com.google.android.gms.maps.model.LatLng
import com.nasportfolio.domain.comment.Comment
import com.nasportfolio.domain.restaurant.TransformedRestaurant

data class RestaurantsDetailState(
    val restaurant: TransformedRestaurant? = null,
    val isLoading: Boolean = true,
    val shouldNavigateBack: Boolean = false,
    val isUpdated: Boolean = false,
    val currentLocation: LatLng? = null,
    val isAnimationDone: Boolean = false,

    val review: String = "",
    val rating: Int = 0,
    val reviewError: String? = null,
    val ratingError: String? = null,
    val isSubmitting: Boolean = false,
    val currentUserId: String? = null,

    val commentBeingEdited: Comment? = null,
    val editingReviewValue: String = "",
    val editingRatingValue: Int = 0,
    val editingReviewError: String? = null,
    val editingRatingError: String? = null,
    val isEditSubmitting: Boolean = false,

    val isDeleted: Boolean = false,
    val isDeleting: Boolean = false,
)