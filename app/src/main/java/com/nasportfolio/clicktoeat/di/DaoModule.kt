package com.nasportfolio.clicktoeat.di

import com.nasportfolio.clicktoeat.data.restaurant.remote.RestaurantDao
import com.nasportfolio.clicktoeat.data.restaurant.remote.RestaurantDaoImpl
import com.nasportfolio.clicktoeat.data.user.local.SharedPreferenceDao
import com.nasportfolio.clicktoeat.data.user.local.SharedPreferenceDaoImpl
import com.nasportfolio.clicktoeat.data.user.remote.RemoteUserDao
import com.nasportfolio.clicktoeat.data.user.remote.RemoteUserDaoImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DaoModule {

    @Singleton
    @Binds
    abstract fun bindsRestaurantDao(
        restaurantDaoImpl: RestaurantDaoImpl
    ): RestaurantDao

    @Singleton
    @Binds
    abstract fun bindsRemoteUserDao(
        userDaoImpl: RemoteUserDaoImpl
    ): RemoteUserDao

    @Binds
    abstract fun bindsSharedPreferenceDao(
        sharedPreferenceDaoImpl: SharedPreferenceDaoImpl
    ): SharedPreferenceDao

}