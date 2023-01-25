package com.nasportfolio.clicktoeat.di

import com.nasportfolio.domain.branch.BranchRepository
import com.nasportfolio.domain.comment.CommentRepository
import com.nasportfolio.domain.favorites.FavoriteRepository
import com.nasportfolio.domain.likesdislikes.dislike.DislikeRepository
import com.nasportfolio.domain.likesdislikes.like.LikeRepository
import com.nasportfolio.domain.restaurant.RestaurantRepository
import com.nasportfolio.domain.user.UserRepository
import com.nasportfolio.test.branch.FakeBranchRepository
import com.nasportfolio.test.comment.FakeCommentRepository
import com.nasportfolio.test.favorites.FakeFavoriteRepository
import com.nasportfolio.test.likesdislikes.FakeDislikeRepository
import com.nasportfolio.test.likesdislikes.FakeLikeRepository
import com.nasportfolio.test.restaurant.FakeRestaurantRepository
import com.nasportfolio.test.user.FakeUserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TestRepoModule {
    @Provides
    @Singleton
    fun providesRestaurantRepository() = FakeRestaurantRepository() as RestaurantRepository

    @Provides
    @Singleton
    fun providesUserRepository() = FakeUserRepository() as UserRepository

    @Provides
    @Singleton
    fun providesBranchRepository() = FakeBranchRepository() as BranchRepository

    @Provides
    @Singleton
    fun providesCommentRepository() = FakeCommentRepository() as CommentRepository

    @Provides
    @Singleton
    fun providesFavoriteRepository(
        userRepository: UserRepository,
        restaurantRepository: RestaurantRepository
    ) = FakeFavoriteRepository(
        fakeUserRepository = userRepository as FakeUserRepository,
        fakeRestaurantRepository = restaurantRepository as FakeRestaurantRepository
    ) as FavoriteRepository

    @Provides
    @Singleton
    fun providesLikeRepository(
        commentRepository: CommentRepository,
        userRepository: UserRepository
    ) = FakeLikeRepository(
        fakeUserRepository = userRepository as FakeUserRepository,
        fakeCommentRepository = commentRepository as FakeCommentRepository
    ) as LikeRepository

    @Provides
    @Singleton
    fun providesDislikeRepository(
        commentRepository: CommentRepository,
        userRepository: UserRepository
    ) = FakeDislikeRepository(
        fakeUserRepository = userRepository as FakeUserRepository,
        fakeCommentRepository = commentRepository as FakeCommentRepository
    ) as DislikeRepository
}