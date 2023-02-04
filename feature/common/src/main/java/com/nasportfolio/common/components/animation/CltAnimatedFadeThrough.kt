package com.nasportfolio.common.components.animation

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CltAnimatedFadeThrough(
    visibleState: MutableTransitionState<Boolean>,
    component: @Composable () -> Unit
) {
    AnimatedVisibility(
        visibleState = visibleState,
        enter = fadeIn(
            animationSpec = tween(
                durationMillis = 300 - (300 * 0.35f).toInt(),
                delayMillis = (300 * 0.35f).toInt(),
                easing = LinearOutSlowInEasing
            )
        ) + scaleIn(
            animationSpec = tween(
                durationMillis = 300 - (300 * 0.35f).toInt(),
                delayMillis = (300 * 0.35f).toInt(),
                easing = LinearOutSlowInEasing
            ),
            initialScale = 0.92f
        ),
        exit = fadeOut(
            animationSpec = tween(
                durationMillis = (300 * 0.35f).toInt(),
                delayMillis = 0,
                easing = FastOutLinearInEasing
            )
        )
    ) {
        component()
    }
}