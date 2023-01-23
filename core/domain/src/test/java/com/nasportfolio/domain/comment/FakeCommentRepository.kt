package com.nasportfolio.domain.comment

import com.nasportfolio.domain.image.Image
import com.nasportfolio.domain.restaurant.Restaurant
import com.nasportfolio.domain.user.User
import com.nasportfolio.domain.utils.Resource
import com.nasportfolio.domain.utils.ResourceError
import java.util.*

class FakeCommentRepository : CommentRepository {
    var comments: List<Comment> = emptyList()

    init {
        comments = comments.toMutableList().apply {
            repeat(10) {
                val comment = Comment(
                    id = it.toString(),
                    review = "review $it",
                    rating = it % 5,
                    restaurant = Restaurant(
                        id = it.toString(),
                        name = "name $it",
                        description = "description $it",
                        image = Image(id = it, key = "key $it", url = "url $it")
                    ),
                    user = User(
                        id = it.toString(),
                        username = "username $it",
                        email = "email$it@gmail.com",
                        image = null
                    ),
                    createdAt = Date(),
                    updatedAt = Date()
                )
                add(comment)
            }
        }
    }

    override suspend fun getAllComments(): Resource<List<Comment>> {
        return Resource.Success(comments)
    }

    override suspend fun getCommentById(id: String): Resource<Comment> {
        val comment = comments.find { it.id == id } ?: return Resource.Failure(
            ResourceError.DefaultError("Unable to find comment with id $id")
        )
        return Resource.Success(comment)
    }

    override suspend fun getCommentsByUser(userId: String): Resource<List<Comment>> {
        return Resource.Success(
            comments.filter { it.user.id == userId }
        )
    }

    override suspend fun getCommentsByRestaurant(restaurantId: String): Resource<List<Comment>> {
        return Resource.Success(
            comments.filter { it.restaurant.id == restaurantId }
        )
    }

    override suspend fun createComment(
        token: String,
        restaurantId: String,
        review: String,
        rating: Int,
        parentComment: String?
    ): Resource<String> {
        val index = comments.size
        val comment = Comment(
            id = index.toString(),
            review = review,
            rating = rating,
            restaurant = Restaurant(
                id = index.toString(),
                name = "name $index",
                description = "description $index",
                image = Image(id = index, key = "key $index", url = "url $index")
            ),
            user = User(
                id = index.toString(),
                username = "username $index",
                email = "email$index@gmail.com",
                image = null
            ),
            createdAt = Date(),
            updatedAt = Date()
        )
        comments = comments.toMutableList().apply {
            add(comment)
        }
        return Resource.Success(comment.id)
    }

    override suspend fun updateComment(
        token: String,
        commentId: String,
        review: String?,
        rating: Int?
    ): Resource<Comment> {
        var comment = comments.find { it.id == commentId } ?: return Resource.Failure(
            ResourceError.DefaultError("Unable to find comment with id $commentId")
        )
        comment = comment.copy(
            review = review ?: comment.review,
            rating = rating ?: comment.rating
        )
        comments = comments.toMutableList().apply {
            val index = map { it.id }.indexOf(commentId)
            set(index, comment)
        }
        return Resource.Success(comment)
    }

    override suspend fun deleteComment(token: String, commentId: String): Resource<String> {
        val comment = comments.find { it.id == commentId } ?: return Resource.Failure(
            ResourceError.DefaultError("Unable to find comment with id $commentId")
        )
        comments = comments.filter { it.id != comment.id }
        return Resource.Success("Successfully deleted comment with id $commentId")
    }
}