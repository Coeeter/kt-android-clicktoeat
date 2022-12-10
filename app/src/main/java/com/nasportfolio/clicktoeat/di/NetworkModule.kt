package com.nasportfolio.clicktoeat.di

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.nasportfolio.clicktoeat.data.common.converter.JsonConverter
import com.nasportfolio.clicktoeat.data.common.converter.JsonConverterImpl
import com.nasportfolio.clicktoeat.data.common.interceptors.NetworkInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideNetworkInterceptor(
        @ApplicationContext context: Context
    ): Interceptor = NetworkInterceptor(context)

    @Singleton
    @Provides
    fun providesOkHttpClient(
        interceptor: Interceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .build()

    @Singleton
    @Provides
    fun providesGson(): Gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
        .create()

    @Provides
    fun providesJsonConverter(
        gson: Gson
    ): JsonConverter = JsonConverterImpl(gson)

}