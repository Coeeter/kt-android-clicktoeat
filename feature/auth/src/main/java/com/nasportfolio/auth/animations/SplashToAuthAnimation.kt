package com.nasportfolio.auth.animations

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.nasportfolio.auth.R
import com.nasportfolio.common.theme.FreeStyleScript
import com.nasportfolio.common.theme.lightOrange
import com.nasportfolio.common.theme.mediumOrange
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@Composable
internal fun SplashToAuthAnimation(
    modifier: Modifier = Modifier,
    content: @Composable (Alignment) -> Unit
) {
    ForegroundAnimation(modifier = modifier) {
        FormAnimation(
            modifier = modifier,
            content = content
        )
    }
}

@Composable
private fun ForegroundAnimation(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val screenHeight = configuration.screenHeightDp
    val height = remember {
        Animatable(initialValue = screenHeight.toFloat())
    }
    val corner = remember {
        Animatable(initialValue = 0f)
    }

    LaunchedEffect(true) {
        val animationSpec = tween<Float>(
            durationMillis = 750,
            easing = FastOutSlowInEasing
        )
        launch {
            height.animateTo(
                targetValue = screenWidth.toFloat(),
                animationSpec = animationSpec
            )
        }
        launch {
            corner.animateTo(
                targetValue = 20f,
                animationSpec = animationSpec
            )
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height = height.value.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(lightOrange, mediumOrange),
                    ),
                    shape = RoundedCornerShape(
                        bottomEndPercent = corner.value.toInt(),
                        bottomStartPercent = corner.value.toInt()
                    )
                ),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = R.drawable.ic_clicktoeat_icon),
                    contentDescription = "Logo of ClickToEat",
                    modifier = Modifier.size(175.dp),
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "ClickToEat",
                    fontSize = MaterialTheme.typography.h2.fontSize,
                    color = Color.White,
                    fontFamily = FreeStyleScript
                )
            }
        }
        content()
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun FormAnimation(
    modifier: Modifier,
    content: @Composable (Alignment) -> Unit
) {
    var parentSize by remember {
        mutableStateOf(IntSize(0, 0))
    }

    AnimatedVisibility(
        visibleState = remember { MutableTransitionState(false) }
            .apply { targetState = true },
        modifier = modifier.fillMaxSize(),
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(
                durationMillis = 750,
                easing = FastOutSlowInEasing
            )
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .onGloballyPositioned {
                    parentSize = it.size
                }
        ) {
            content(
                Alignment { size, space, layoutDirection ->
                    IntOffset(
                        x = (parentSize.width - size.width) / 2,
                        y = (parentSize.height * 0.45).toInt()
                    )
                }
            )
        }
    }
}