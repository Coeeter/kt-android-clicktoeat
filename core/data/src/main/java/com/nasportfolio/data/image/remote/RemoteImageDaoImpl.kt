package com.nasportfolio.data.image.remote

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.net.URL
import javax.inject.Inject

class RemoteImageDaoImpl @Inject constructor(): RemoteImageDao {
    override fun downloadImageFromUrl(url: String): Bitmap {
        val connection = URL(url).openConnection()
        val stream = connection.getInputStream()
        return BitmapFactory.decodeStream(stream)
    }
}