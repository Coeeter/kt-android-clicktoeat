package com.nasportfolio.data.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.nasportfolio.data.CltLocalDatabase
import com.nasportfolio.data.branch.local.LocalBranchDao
import com.nasportfolio.data.restaurant.local.LocalRestaurantDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    @Provides
    @Singleton
    fun providesDatabase(
        @ApplicationContext context: Context
    ): CltLocalDatabase = Room.databaseBuilder(
        context = context,
        klass = CltLocalDatabase::class.java,
        name = "clt-database"
    ).build()

}