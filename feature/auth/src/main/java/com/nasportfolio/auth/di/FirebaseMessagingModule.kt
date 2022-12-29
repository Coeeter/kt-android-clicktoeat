package com.nasportfolio.auth.di

import com.google.firebase.messaging.FirebaseMessaging
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object FirebaseMessagingModule {
    @Provides
    fun providesFirebaseMessaging() = FirebaseMessaging.getInstance()
}