package com.nasportfolio.data.image.remote

import android.graphics.Bitmap

interface RemoteImageDao {
    fun downloadImageFromUrl(url: String): Bitmap
}