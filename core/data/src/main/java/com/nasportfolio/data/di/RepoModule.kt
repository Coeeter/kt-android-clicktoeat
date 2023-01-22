package com.nasportfolio.data.di

import com.nasportfolio.data.branch.BranchRepositoryImpl
import com.nasportfolio.data.comment.CommentRepositoryImpl
import com.nasportfolio.data.dislike.DislikeRepositoryImpl
import com.nasportfolio.data.favorite.FavoriteRepositoryImpl
import com.nasportfolio.data.like.LikeRepositoryImpl
import com.nasportfolio.data.restaurant.RestaurantRepositoryImpl
import com.nasportfolio.data.user.UserRepositoryImpl
import com.nasportfolio.domain.branch.BranchRepository
import com.nasportfolio.domain.comment.CommentRepository
import com.nasportfolio.domain.likesdislikes.dislike.DislikeRepository
import com.nasportfolio.domain.favorites.FavoriteRepository
import com.nasportfolio.domain.likesdislikes.like.LikeRepository
import com.nasportfolio.domain.restaurant.RestaurantRepository
import com.nasportfolio.domain.user.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
abstract class RepoModule {

    @Binds
    abstract fun bindsRestaurantRepository(
        restaurantRepositoryImpl: RestaurantRepositoryImpl
    ): RestaurantRepository

    @Binds
    abstract fun bindsUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    @Binds
    abstract fun bindsBranchRepository(
        branchRepositoryImpl: BranchRepositoryImpl
    ): BranchRepository

    @Binds
    abstract fun bindsCommentRepository(
        commentRepositoryImpl: CommentRepositoryImpl
    ): CommentRepository

    @Binds
    abstract fun bindsFavoriteRepository(
        favoriteRepositoryImpl: FavoriteRepositoryImpl
    ): FavoriteRepository

    @Binds
    abstract fun bindsLikeRepository(
        likeRepositoryImpl: LikeRepositoryImpl
    ): LikeRepository

    @Binds
    abstract fun bindsDislikeRepository(
        dislikeRepositoryImpl: DislikeRepositoryImpl
    ): DislikeRepository

}