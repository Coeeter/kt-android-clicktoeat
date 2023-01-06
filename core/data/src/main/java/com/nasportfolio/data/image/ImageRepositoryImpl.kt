package com.nasportfolio.data.image

import android.graphics.Bitmap
import com.nasportfolio.data.image.local.LocalImageDao
import com.nasportfolio.data.image.remote.RemoteImageDao
import com.nasportfolio.domain.image.ImageRepository
import javax.inject.Inject

class ImageRepositoryImpl @Inject constructor(
    private val remoteImageDao: RemoteImageDao,
    private val localImageDao: LocalImageDao
) : ImageRepository {
    override fun getImage(url: String): Bitmap = localImageDao.getImageFromCache(url) ?: run {
        remoteImageDao.downloadImageFromUrl(url).also {
            localImageDao.saveImageToCache(url, it)
        }
    }

    override fun removeImageFromCache(url: String) {
        localImageDao.removeImageFromCache(url)
    }
}