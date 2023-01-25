package com.nasportfolio.common.components.buttons

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.nasportfolio.common.theme.mediumOrange

class SpeedDialState {
    var isExpanded: Boolean by mutableStateOf(false)
}

@Composable
fun rememberSpeedDialState() = remember {
    SpeedDialState()
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CltSpeedDialFab(
    testTag: String = "",
    state: SpeedDialState,
    buttons: @Composable ColumnScope.() -> Unit,
    expandedChild: @Composable () -> Unit,
    collapsedChild: @Composable () -> Unit
) {
    val density = LocalDensity.current

    Column(
        horizontalAlignment = Alignment.End
    ) {
        AnimatedVisibility(
            visible = state.isExpanded,
            enter = fadeIn(
                animationSpec = tween(
                    durationMillis = 200
                )
            ) + slideIn(
                animationSpec = tween(
                    durationMillis = 200,
                ),
                initialOffset = {
                    IntOffset(
                        x = with(density) { (-32).dp.toPx().toInt() },
                        y = with(density) { 25.dp.toPx().toInt() }
                    )
                }
            ),
            exit = fadeOut(
                animationSpec = tween(
                    durationMillis = 200
                )
            ) + slideOut(
                animationSpec = tween(
                    durationMillis = 200
                ),
                targetOffset = {
                    IntOffset(
                        x = with(density) { (-32).dp.toPx().toInt() },
                        y = with(density) { 25.dp.toPx().toInt() }
                    )
                }
            )
        ) {
            Column {
                buttons()
            }
        }
        FloatingActionButton(
            modifier = Modifier.testTag(testTag),
            backgroundColor = mediumOrange,
            onClick = {
                state.isExpanded = !state.isExpanded
            }
        ) {
            AnimatedContent(
                targetState = state.isExpanded,
                transitionSpec = {
                    fadeIn() with fadeOut()
                },
            ) { isExpanded ->
                if (isExpanded) expandedChild()
                else collapsedChild()
            }
        }
    }
}

@Composable
fun CltSpeedDialFabItem(
    testTag: String = "",
    hint: String,
    backgroundColor: Color = MaterialTheme.colors.secondary,
    onClick: () -> Unit,
    component: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = 10.dp,
                bottom = 10.dp,
                end = 8.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        Surface(
            modifier = Modifier.clickable { onClick() },
            elevation = 10.dp,
            shape = RoundedCornerShape(10.dp)
        ) {
            Box(
                modifier = Modifier.padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = hint)
            }
        }
        Spacer(modifier = Modifier.width(5.dp))
        FloatingActionButton(
            backgroundColor = backgroundColor,
            onClick = onClick,
            modifier = Modifier
                .size(46.dp)
                .testTag(testTag)
        ) {
            component()
        }
    }
}