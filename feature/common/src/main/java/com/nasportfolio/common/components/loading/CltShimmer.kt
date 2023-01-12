package com.nasportfolio.common.components.loading

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun CltShimmer(
    modifier: Modifier = Modifier,
    lightModeBaseColor: Color = Color.LightGray.copy(0.3f),
    lightModeHighlightColor: Color = Color.LightGray.copy(0.8f),
    darkModeBaseColor: Color = Color.LightGray.copy(0.1f),
    darkModeHighlightColor: Color = Color.LightGray.copy(0.6f),
) {
    val colors = if (isSystemInDarkTheme()) listOf(
        darkModeBaseColor,
        darkModeHighlightColor,
        darkModeBaseColor
    ) else listOf(
        lightModeBaseColor,
        lightModeHighlightColor,
        lightModeBaseColor
    )
    val transition = rememberInfiniteTransition()
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = FastOutLinearInEasing
            ),
        )
    )

    val brush = Brush.linearGradient(
        colors = colors,
        start = Offset.Zero,
        end = Offset(x = translateAnim, y = translateAnim)
    )

    Box(
        modifier = modifier.background(brush = brush)
    )
}