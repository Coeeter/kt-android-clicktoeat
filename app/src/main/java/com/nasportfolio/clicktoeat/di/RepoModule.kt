package com.nasportfolio.clicktoeat.di

import com.nasportfolio.clicktoeat.data.restaurant.RestaurantRepositoryImpl
import com.nasportfolio.clicktoeat.data.restaurant.remote.RestaurantDao
import com.nasportfolio.clicktoeat.domain.restaurant.RestaurantRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepoModule {
    @Singleton
    @Provides
    fun providesRestaurantRepository(
        restaurantDao: RestaurantDao
    ): RestaurantRepository = RestaurantRepositoryImpl(restaurantDao)
}