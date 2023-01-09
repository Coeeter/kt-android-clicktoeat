package com.nasportfolio.common.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.nasportfolio.common.theme.lightOrange
import com.nasportfolio.common.theme.mediumOrange

@Composable
fun CltButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    shape: Shape = RoundedCornerShape(10.dp),
    enabled: Boolean = true,
    disabledLightModeColor: Color = Color.LightGray,
    disabledDarkModeColor: Color = Color.DarkGray,
    gradient: Brush = Brush.horizontalGradient(
        colors = listOf(lightOrange, mediumOrange)
    ),
    content: @Composable () -> Unit,
) {
    var boxModifier = Modifier.fillMaxWidth()
    val disabledColor = if (isSystemInDarkTheme()) disabledDarkModeColor
    else disabledLightModeColor
    if (enabled) boxModifier = boxModifier.background(gradient, shape = shape)
    if (!enabled) boxModifier = boxModifier.background(disabledColor, shape = shape)

    Button(
        modifier = modifier,
        enabled = enabled,
        contentPadding = PaddingValues(0.dp),
        onClick = onClick,
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Transparent,
        ),
    ) {
        Box(
            modifier = boxModifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .then(modifier),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CltButton(
    modifier: Modifier = Modifier,
    text: String,
    withLoading: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    shape: Shape = RoundedCornerShape(10.dp),
    disabledLightModeColor: Color = Color.LightGray,
    disabledDarkModeColor: Color = Color.DarkGray,
    gradient: Brush = Brush.horizontalGradient(
        colors = listOf(lightOrange, mediumOrange)
    ),
) {
    var boxModifier = Modifier.fillMaxWidth()
    val disabledColor = if (isSystemInDarkTheme()) disabledDarkModeColor
    else disabledLightModeColor
    if (enabled) boxModifier = boxModifier.background(gradient, shape = shape)
    if (!enabled) boxModifier = boxModifier.background(disabledColor, shape = shape)

    Button(
        modifier = modifier,
        enabled = enabled,
        contentPadding = PaddingValues(0.dp),
        onClick = onClick,
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Transparent,
        ),
    ) {
        Box(
            modifier = boxModifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .then(modifier),
            contentAlignment = Alignment.Center
        ) {
            if (!withLoading) Text(
                text = text,
                color = Color.White,
                style = MaterialTheme.typography.h6
            )
            if (withLoading) AnimatedContent(targetState = !enabled) { isLoading ->
                if (isLoading)
                    return@AnimatedContent CircularProgressIndicator(
                        modifier = Modifier.size(26.dp),
                        strokeWidth = 3.dp
                    )
                Text(
                    text = text,
                    color = Color.White,
                    style = MaterialTheme.typography.h6
                )
            }
        }
    }
}