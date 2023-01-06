package com.nasportfolio.common.components

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.nasportfolio.domain.image.ImageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

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
    colorFilter: ColorFilter? = null,
    cltImageViewModel: CltImageViewModel = hiltViewModel()
) {
    var isLoading by remember {
        mutableStateOf(true)
    }
    var image by remember {
        mutableStateOf<ImageBitmap?>(null)
    }

    LaunchedEffect(true) {
        isLoading = true
        withContext(Dispatchers.IO) {
            image = cltImageViewModel.getImage(url).asImageBitmap()
            isLoading = false
        }
    }

    Box(modifier = modifier,) {
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
        }
        if (isLoading)
            placeholder()
    }
}

@HiltViewModel
class CltImageViewModel @Inject constructor(
    private val imageRepository: ImageRepository
) : ViewModel() {
    fun getImage(url: String) = imageRepository.getImage(url)
}