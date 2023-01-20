package com.nasportfolio.common.components.form

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.nasportfolio.common.components.buttons.CltButton
import com.nasportfolio.common.theme.mediumOrange

@Composable
fun rememberImagePicker(
    onValueChange: (Bitmap) -> Unit
): ManagedActivityResultLauncher<String, Uri> {
    val context = LocalContext.current

    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.decodeBitmap(
                ImageDecoder.createSource(
                    context.contentResolver,
                    uri!!
                )
            )
        } else {
            MediaStore.Images.Media.getBitmap(
                context.contentResolver,
                uri
            )
        }
        onValueChange(bitmap)
    }
}

@Composable
fun CltImagePicker(
    modifier: Modifier = Modifier,
    value: Bitmap?,
    onValueChange: (Bitmap) -> Unit,
    error: String?,
) {
    val density = LocalDensity.current
    var imageWidth by remember {
        mutableStateOf(0.dp)
    }
    val pickImage = rememberImagePicker(
        onValueChange = onValueChange
    )

    Surface(
        modifier = modifier.onGloballyPositioned {
            imageWidth = with(density) {
                it.size.width.toDp()
            }
        },
        elevation = 10.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            value?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } ?: Icon(
                modifier = Modifier.fillMaxSize(0.5f),
                imageVector = Icons.Default.PhotoCamera,
                contentDescription = null,
                tint = mediumOrange
            )
        }
    }
    AnimatedVisibility(
        visible = error != null,
        enter = fadeIn() + slideInHorizontally(animationSpec = spring()),
    ) {
        Column {
            Spacer(modifier = Modifier.height(10.dp))
            error?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colors.error,
                    style = MaterialTheme.typography.body1,
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(10.dp))
    CltButton(
        modifier = Modifier.width(imageWidth),
        text = value?.let { "Change picture" } ?: "Choose picture",
        withLoading = true,
        enabled = true,
        onClick = { pickImage.launch("image/*") }
    )
}