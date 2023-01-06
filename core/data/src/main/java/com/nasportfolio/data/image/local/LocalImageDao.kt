package com.nasportfolio.data.image.local

import android.graphics.Bitmap

interface LocalImageDao {
    fun getImageFromCache(url: String): Bitmap?
    fun saveImageToCache(url: String, bitmap: Bitmap, baseSize: Int = 1)
    fun removeImageFromCache(url: String)
}