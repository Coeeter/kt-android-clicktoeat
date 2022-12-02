package com.nasportfolio.clicktoeat.di

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.nasportfolio.clicktoeat.domain.common.exceptions.NoNetworkException
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideNetworkInterceptor(
        @ApplicationContext context: Context
    ): Interceptor = object : Interceptor {
        private fun isConnected(): Boolean {
            val connectivityManager = context.getSystemService(
                Context.CONNECTIVITY_SERVICE
            ) as ConnectivityManager
            val network = connectivityManager.activeNetwork
            val connection = connectivityManager.getNetworkCapabilities(network)
            return connection != null && (connection.hasTransport(
                NetworkCapabilities.TRANSPORT_WIFI
            ) || connection.hasTransport(
                NetworkCapabilities.TRANSPORT_CELLULAR
            ))
        }

        override fun intercept(chain: Interceptor.Chain): Response {
            val originalRequest = chain.request()
            if (!isConnected()) throw NoNetworkException()
            return chain.proceed(originalRequest)
        }
    }

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

}