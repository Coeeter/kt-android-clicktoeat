package com.nasportfolio.common.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.FloatingActionButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.nasportfolio.common.theme.mediumOrange

@Composable
fun CltFloatingActionButton(
    modifier: Modifier = Modifier,
    withNavigation: Boolean = true,
    backgroundColor: Color = mediumOrange,
    animatedBackgroundColor: Color = MaterialTheme.colors.background,
    durationMillis: Int = 500,
    easing: Easing = FastOutSlowInEasing,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    val configuration = LocalConfiguration.current
    val (isClicked, setIsClicked) = remember {
        mutableStateOf(false)
    }

    val transition = updateTransition(
        targetState = isClicked,
        label = "fab-clicked",
    )

    val height by transition.animateDp(
        label = "height",
        transitionSpec = {
            tween(
                durationMillis = durationMillis,
                easing = easing
            )
        }
    ) {
        if (it) return@animateDp configuration.screenHeightDp.dp
        return@animateDp 56.dp
    }

    val width by transition.animateDp(
        label = "width",
        transitionSpec = {
            tween(
                durationMillis = durationMillis,
                easing = easing
            )
        }
    ) {
        if (it) return@animateDp configuration.screenWidthDp.dp
        return@animateDp 56.dp
    }

    val corner by transition.animateInt(
        label = "corner",
        transitionSpec = {
            tween(
                durationMillis = durationMillis,
                easing = easing
            )
        }
    ) {
        if (it) return@animateInt 0
        return@animateInt 50
    }

    val offset by transition.animateDp(
        label = "offset",
        transitionSpec = {
            tween(
                durationMillis = durationMillis,
                easing = easing
            )
        }
    ) {
        if (it) return@animateDp 16.dp
        return@animateDp 0.dp
    }

    val color by transition.animateColor(
        label = "color",
        transitionSpec = {
            tween(
                durationMillis = durationMillis,
                easing = easing
            )
        }
    ) {
        if (it) return@animateColor animatedBackgroundColor
        return@animateColor backgroundColor
    }

    val elevation by transition.animateDp(
        label = "elevation",
        transitionSpec = {
            tween(
                durationMillis = durationMillis,
                easing = easing
            )
        }
    ) {
        if (it) return@animateDp 0.dp
        return@animateDp 6.dp
    }

    FloatingActionButton(
        modifier = modifier
            .size(width = width, height = height)
            .offset(x = offset, y = offset),
        backgroundColor = color,
        shape = RoundedCornerShape(corner),
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = elevation
        ),
        onClick = {
            setIsClicked(withNavigation)
            onClick()
        }
    ) {
        AnimatedVisibility(
            visible = !isClicked,
            enter = fadeIn(
                animationSpec = tween(
                    durationMillis = (durationMillis * 0.4).toInt(),
                    easing = easing
                )
            ),
            exit = fadeOut(
                animationSpec = tween(
                    durationMillis = (durationMillis * 0.4).toInt(),
                    easing = easing
                )
            )
        ) {
            content()
        }
    }
}