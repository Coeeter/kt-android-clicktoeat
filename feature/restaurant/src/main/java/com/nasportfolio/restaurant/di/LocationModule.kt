package com.nasportfolio.restaurant.di

import android.content.Context
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ViewModelComponent::class)
object LocationModule {
    @Provides
    fun providesFusedLocationClient(
        @ApplicationContext context: Context
    ) = LocationServices.getFusedLocationProviderClient(context)
}