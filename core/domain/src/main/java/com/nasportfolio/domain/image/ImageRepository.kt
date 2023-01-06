package com.nasportfolio.domain.image

import android.graphics.Bitmap

interface ImageRepository {
    fun getImage(url: String): Bitmap
    fun removeImageFromCache(url: String)
}