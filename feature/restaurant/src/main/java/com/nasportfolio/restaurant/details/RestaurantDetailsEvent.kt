package com.nasportfolio.restaurant.details

sealed class RestaurantDetailsEvent {
    class AnimationOverEvent(val isAnimationDone: Boolean) : RestaurantDetailsEvent()
    class OnReviewChangedEvent(val review: String) : RestaurantDetailsEvent()
    class OnRatingChangedEvent(val rating: Int) : RestaurantDetailsEvent()
    object ToggleFavoriteEvent : RestaurantDetailsEvent()
    object OnSubmit : RestaurantDetailsEvent()
    class DeleteComment(val index: Int) : RestaurantDetailsEvent()
    class OpenEditCommentDialog(val index: Int) : RestaurantDetailsEvent()
    object OnCloseEditCommentDialog : RestaurantDetailsEvent()
    class OnEditReview(val review: String) : RestaurantDetailsEvent()
    class OnEditRating(val rating: Int) : RestaurantDetailsEvent()
    object OnCompleteEdit : RestaurantDetailsEvent()
    object DeleteRestaurant: RestaurantDetailsEvent()
    class DeleteBranch(val branchId: String): RestaurantDetailsEvent()
    class LikeComment(val index: Int) : RestaurantDetailsEvent()
    class DislikeComment(val index: Int) : RestaurantDetailsEvent()
}