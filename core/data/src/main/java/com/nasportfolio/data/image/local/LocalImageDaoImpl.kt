package com.nasportfolio.data.image.local

import android.graphics.Bitmap
import android.util.LruCache
import javax.inject.Inject

class LocalImageDaoImpl @Inject constructor(
    private val lruCache: LruCache<String, Bitmap>
) : LocalImageDao {
    override fun getImageFromCache(url: String): Bitmap? = lruCache.get(url)

    override fun saveImageToCache(url: String, bitmap: Bitmap, baseSize: Int) {
        getImageFromCache(url) ?: return
        val aspectRatio = bitmap.width / bitmap.height
        val resizedBitmap = Bitmap.createScaledBitmap(
            bitmap,
            baseSize * aspectRatio,
            baseSize,
            true
        )
        lruCache.put(url, resizedBitmap)
    }

    override fun removeImageFromCache(url: String) {
        lruCache.remove(url)
    }
}