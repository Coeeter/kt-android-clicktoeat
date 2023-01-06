package com.nasportfolio.data.di

import android.graphics.Bitmap
import android.util.LruCache
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object CacheModule {

    @Provides
    fun providesRuntime(): Runtime = Runtime.getRuntime()

    @Provides
    fun providesLruCache(
        runtime: Runtime
    ): LruCache<String, Bitmap> = object : LruCache<String, Bitmap>(
        (runtime.maxMemory().toInt() / 1024) / 2
    ) {
        override fun sizeOf(key: String, value: Bitmap) = value.byteCount / 1024
    }
}