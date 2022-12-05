package com.nasportfolio.clicktoeat.di

import com.nasportfolio.clicktoeat.data.branch.BranchRepositoryImpl
import com.nasportfolio.clicktoeat.data.restaurant.RestaurantRepositoryImpl
import com.nasportfolio.clicktoeat.data.user.UserRepositoryImpl
import com.nasportfolio.clicktoeat.domain.branch.BranchRepository
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

    @Singleton
    @Binds
    abstract fun bindsRestaurantRepository(
        restaurantRepositoryImpl: RestaurantRepositoryImpl
    ): RestaurantRepository

    @Singleton
    @Binds
    abstract fun bindsUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    @Singleton
    @Binds
    abstract fun bindsBranchRepository(
        branchRepositoryImpl: BranchRepositoryImpl
    ): BranchRepository

}