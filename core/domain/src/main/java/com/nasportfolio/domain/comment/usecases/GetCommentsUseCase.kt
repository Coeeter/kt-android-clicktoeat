package com.nasportfolio.domain.comment.usecases

import com.nasportfolio.domain.comment.Comment
import com.nasportfolio.domain.comment.CommentRepository
import com.nasportfolio.domain.likesdislikes.dislike.DislikeRepository
import com.nasportfolio.domain.likesdislikes.like.LikeRepository
import com.nasportfolio.domain.user.User
import com.nasportfolio.domain.utils.Resource
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetCommentsUseCase @Inject constructor(
    private val commentRepository: CommentRepository,
    private val likeRepository: LikeRepository,
    private val dislikeRepository: DislikeRepository
) {
    operator fun invoke(restaurantId: String) = flow<Resource<List<Comment>>> {
        emit(Resource.Loading(isLoading = true))
        val commentResource = commentRepository.getCommentsByRestaurant(
            restaurantId = restaurantId
        )
        when (commentResource) {
            is Resource.Success -> emit(transformComment(commentResource))
            else -> emit(commentResource)
        }
    }

    fun byUser(userId: String) = flow<Resource<List<Comment>>> {
        emit(Resource.Loading(isLoading = true))
        val commentResource = commentRepository.getCommentsByUser(
            userId = userId
        )
        when (commentResource) {
            is Resource.Success -> emit(transformComment(commentResource))
            else -> emit(commentResource)
        }
    }

    fun byId(commentId: String) = flow<Resource<Comment>> {
        emit(Resource.Loading(isLoading = true))
        val comment = coroutineScope {
            val deferredComment = async {
                commentRepository.getCommentById(id = commentId)
            }
            val deferredLikes = async {
                likeRepository.getUsersWhoLikedComment(commentId = commentId)
            }
            val deferredDislikes = async {
                dislikeRepository.getUsersWhoDislikedComments(commentId = commentId)
            }
            val comment = deferredComment.await()
            val likes = deferredLikes.await()
            val dislikes = deferredDislikes.await()
            if (comment !is Resource.Success<Comment>) {
                return@coroutineScope Resource.Failure(
                    (likes as Resource.Failure).error
                )
            }
            if (likes !is Resource.Success<List<User>>) {
                return@coroutineScope Resource.Failure(
                    (likes as Resource.Failure).error
                )
            }
            if (dislikes !is Resource.Success<List<User>>) {
                return@coroutineScope Resource.Failure(
                    (dislikes as Resource.Failure).error
                )
            }
            Resource.Success(
                result = Comment(
                    id = comment.result.id,
                    review = comment.result.review,
                    rating = comment.result.rating,
                    parentComment = comment.result.parentComment,
                    createdAt = comment.result.createdAt,
                    updatedAt = comment.result.updatedAt,
                    user = comment.result.user,
                    restaurant = comment.result.restaurant,
                    likes = likes.result,
                    dislikes = dislikes.result
                )
            )
        }
        emit(comment)
    }

    private suspend fun transformComment(
        commentResource: Resource.Success<List<Comment>>
    ): Resource<List<Comment>> = coroutineScope {
        val likes = commentResource.result.map {
            async { likeRepository.getUsersWhoLikedComment(it.id) }
        }.awaitAll()
        val dislikes = commentResource.result.map {
            async { dislikeRepository.getUsersWhoDislikedComments(it.id) }
        }.awaitAll()
        val comments = commentResource.result.mapIndexed { index, comment ->
            val likesOfComment = likes[index]
            val dislikesOfComment = dislikes[index]
            if (likesOfComment !is Resource.Success<List<User>>) {
                return@coroutineScope Resource.Failure(
                    (likesOfComment as Resource.Failure).error
                )
            }
            if (dislikesOfComment !is Resource.Success<List<User>>) {
                return@coroutineScope Resource.Failure(
                    (dislikesOfComment as Resource.Failure).error
                )
            }
            Comment(
                id = comment.id,
                review = comment.review,
                rating = comment.rating,
                parentComment = comment.parentComment,
                createdAt = comment.createdAt,
                updatedAt = comment.updatedAt,
                user = comment.user,
                restaurant = comment.restaurant,
                likes = likesOfComment.result,
                dislikes = dislikesOfComment.result
            )
        }
        Resource.Success(comments)
    }
}