package com.nasportfolio.common.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URL

private val imageUtils = ImageUtils()

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CltImageFromNetwork(
    modifier: Modifier = Modifier,
    url: String,
    placeholder: @Composable () -> Unit,
    contentDescription: String?,
    backgroundColor: Color = Color.White,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null
) {
    var image by remember {
        mutableStateOf<ImageBitmap?>(null)
    }

    LaunchedEffect(true) {
        launch(Dispatchers.IO) {
            image = imageUtils.loadImage(url).asImageBitmap()
        }
    }

    Box(modifier = modifier) {
        image?.let {
            Box(modifier = Modifier.background(backgroundColor)) {
                Image(
                    bitmap = it,
                    modifier = modifier,
                    contentDescription = contentDescription,
                    alignment = alignment,
                    contentScale = contentScale,
                    alpha = alpha,
                    colorFilter = colorFilter
                )
            }
        } ?: placeholder()
    }
}

private class ImageUtils(
    private val lruCache: LruCache<String, Bitmap> = object : LruCache<String, Bitmap>(
        Runtime.getRuntime().maxMemory().toInt() / 1024 / 4
    ) {
        override fun sizeOf(key: String, value: Bitmap): Int {
            return value.byteCount / 1024
        }
    }
) {
    private fun downloadImageFromUrl(url: String): Bitmap {
        val connection = URL(url).openConnection()
        val stream = connection.getInputStream()
        return BitmapFactory.decodeStream(stream)
    }

    private fun saveImageToCache(url: String, bitmap: Bitmap, baseSize: Int) {
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

    private fun getImageFromCache(url: String): Bitmap? = lruCache.get(url)

    fun loadImage(url: String) = getImageFromCache(url) ?: run {
        downloadImageFromUrl(url).also {
            saveImageToCache(url, it, 1)
        }
    }
}