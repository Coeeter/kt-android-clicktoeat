package com.nasportfolio.clicktoeat.di

import com.google.gson.Gson
import com.nasportfolio.clicktoeat.data.restaurant.remote.RestaurantDao
import com.nasportfolio.clicktoeat.data.restaurant.remote.RestaurantDaoImpl
import com.nasportfolio.clicktoeat.data.user.remote.UserDao
import com.nasportfolio.clicktoeat.data.user.remote.UserDaoImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DaoModule {

    @Singleton
    @Provides
    fun providesRestaurantDao(
        okHttpClient: OkHttpClient,
        gson: Gson
    ): RestaurantDao = RestaurantDaoImpl(okHttpClient, gson)

    @Singleton
    @Provides
    fun providesUserDao(
        okHttpClient: OkHttpClient,
        gson: Gson
    ): UserDao = UserDaoImpl(okHttpClient, gson)

}