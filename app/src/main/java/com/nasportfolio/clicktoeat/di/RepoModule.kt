package com.nasportfolio.clicktoeat.di

import com.nasportfolio.clicktoeat.data.branch.BranchRepositoryImpl
import com.nasportfolio.clicktoeat.data.comment.CommentRepositoryImpl
import com.nasportfolio.clicktoeat.data.dislike.DislikeRepositoryImpl
import com.nasportfolio.clicktoeat.data.favorite.FavoriteRepositoryImpl
import com.nasportfolio.clicktoeat.data.like.LikeRepositoryImpl
import com.nasportfolio.clicktoeat.data.restaurant.RestaurantRepositoryImpl
import com.nasportfolio.clicktoeat.data.user.UserRepositoryImpl
import com.nasportfolio.clicktoeat.domain.branch.BranchRepository
import com.nasportfolio.clicktoeat.domain.comment.CommentRepository
import com.nasportfolio.clicktoeat.domain.dislike.DislikeRepository
import com.nasportfolio.clicktoeat.domain.favorites.FavoriteRepository
import com.nasportfolio.clicktoeat.domain.like.LikeRepository
import com.nasportfolio.clicktoeat.domain.restaurant.RestaurantRepository
import com.nasportfolio.clicktoeat.domain.user.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

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