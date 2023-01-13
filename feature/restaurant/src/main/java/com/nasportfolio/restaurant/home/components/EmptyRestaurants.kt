package com.nasportfolio.restaurant.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nasportfolio.common.theme.lightOrange
import com.nasportfolio.common.theme.mediumOrange

@Composable
fun EmptyRestaurants() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            modifier = Modifier
                .size(250.dp)
                .graphicsLayer(alpha = 0.99f)
                .drawWithCache {
                    onDrawWithContent {
                        drawContent()
                        drawRect(
                            Brush.linearGradient(
                                colors = listOf(
                                    lightOrange,
                                    mediumOrange,
                                    lightOrange
                                )
                            ),
                            blendMode = BlendMode.SrcAtop
                        )
                    }
                },
            imageVector = Icons.Outlined.Info,
            contentDescription = null,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Wow such empty...",
            style = MaterialTheme.typography.h5
        )
        Text(
            text = "Try creating a restaurant!",
            style = MaterialTheme.typography.h6.copy(
                fontWeight = FontWeight.Light
            )
        )
    }
}