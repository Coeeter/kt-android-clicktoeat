package com.nasportfolio.common.components

import android.graphics.BitmapFactory
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

private val bitmapCache = HashMap<String, ImageBitmap>()

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
        bitmapCache[url]?.let {
            image = it
            return@LaunchedEffect
        }
        launch(Dispatchers.IO) {
            image = run {
                bitmapCache[url] = BitmapFactory.decodeStream(
                    URL(url).openConnection().getInputStream()
                ).asImageBitmap()
                bitmapCache[url]
            }
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