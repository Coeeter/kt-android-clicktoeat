package com.nasportfolio.restaurant.comments

sealed class CommentsScreenEvent {
    class OnReviewChangedEvent(val review: String) : CommentsScreenEvent()
    class OnRatingChangedEvent(val rating: Int) : CommentsScreenEvent()
    object OnCreate : CommentsScreenEvent()

    class OpenEditCommentDialog(val index: Int) : CommentsScreenEvent()
    object OnCloseEditCommentDialog : CommentsScreenEvent()

    class OnEditReview(val review: String) : CommentsScreenEvent()
    class OnEditRating(val rating: Int) : CommentsScreenEvent()
    object OnCompleteEdit : CommentsScreenEvent()

    class OnDeleteComment(val index: Int) : CommentsScreenEvent()

    object RefreshPage : CommentsScreenEvent()

    class LikeComment(val index: Int): CommentsScreenEvent()
    class DislikeComment(val index: Int): CommentsScreenEvent()
}