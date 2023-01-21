package com.nasportfolio.user.update.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.nasportfolio.common.components.form.rememberImagePicker
import com.nasportfolio.common.components.loading.CltShimmer
import com.nasportfolio.common.modifier.gradientBackground
import com.nasportfolio.common.theme.lightOrange
import com.nasportfolio.common.theme.mediumOrange
import com.nasportfolio.user.update.UpdateUserEvent
import com.nasportfolio.user.update.UpdateUserState
import com.nasportfolio.user.update.UpdateUserViewModel

@Composable
fun ProfileImagePicker(
    state: UpdateUserState,
    updatePhoto: (Bitmap) -> Unit,
    deletePhoto: () -> Unit
) {
    val pickImage = rememberImagePicker(updatePhoto)

    var isMenuExpanded by remember {
        mutableStateOf(false)
    }

    Box(contentAlignment = Alignment.BottomEnd) {
        Surface(
            modifier = Modifier.size(150.dp),
            shape = CircleShape,
            elevation = 4.dp
        ) {
            if (state.isImageSubmitting || state.isLoading) CltShimmer(
                modifier = Modifier.fillMaxSize()
            )
            if (!state.isImageSubmitting && !state.isLoading) state.image?.let {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    bitmap = it.asImageBitmap(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
            } ?: Icon(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                imageVector = Icons.Default.Person,
                contentDescription = null
            )
        }
        if (!state.isImageSubmitting && !state.isLoading) Box(
            modifier = Modifier.padding(
                end = 8.dp,
                bottom = 8.dp
            )
        ) {
            Surface(
                elevation = 10.dp,
                shape = RoundedCornerShape(5.dp),
            ) {
                Box(
                    modifier = Modifier
                        .clickable { isMenuExpanded = true }
                        .padding(5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = null,
                        modifier = Modifier.gradientBackground(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    lightOrange,
                                    mediumOrange
                                )
                            ),
                        )
                    )
                }
            }
            DropdownMenu(
                expanded = isMenuExpanded,
                onDismissRequest = { isMenuExpanded = false },
            ) {
                DropdownMenuItem(
                    onClick = {
                        isMenuExpanded = false
                        pickImage.launch("image/*")
                    }
                ) {
                    Text(text = "Upload a photo")
                }
                DropdownMenuItem(
                    enabled = state.image != null,
                    onClick = {
                        isMenuExpanded = false
                        deletePhoto()
                    }
                ) {
                    Text(text = "Remove photo")
                }
            }
        }
    }
}