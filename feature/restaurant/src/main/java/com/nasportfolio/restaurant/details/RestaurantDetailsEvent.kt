package com.nasportfolio.restaurant.details

sealed class RestaurantDetailsEvent {
    class AnimationOverEvent(val isAnimationDone: Boolean) : RestaurantDetailsEvent()
    class OnReviewChangedEvent(val review: String) : RestaurantDetailsEvent()
    class OnRatingChangedEvent(val rating: Int) : RestaurantDetailsEvent()
    object ToggleFavoriteEvent : RestaurantDetailsEvent()
    object OnSubmit : RestaurantDetailsEvent()
}