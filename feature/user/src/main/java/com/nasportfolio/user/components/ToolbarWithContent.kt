package com.nasportfolio.user.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.nasportfolio.user.UserProfileState

@Composable
fun ToolbarWithContent(
    state: UserProfileState,
    navController: NavHostController,
    content: @Composable ColumnScope.() -> Unit
) {
    val density = LocalDensity.current
    val scrollState = rememberScrollState()
    val appBarHeight by remember {
        mutableStateOf(250.dp)
    }
    var progress by rememberSaveable {
        mutableStateOf(1f)
    }

    LaunchedEffect(scrollState.value) {
        val appBarHeightInPx = with(density) {
            appBarHeight.toPx() - 56.dp.toPx()
        }
        if (scrollState.value == 0) {
            progress = 1f
            return@LaunchedEffect
        }
        if (scrollState.value > appBarHeightInPx) {
            progress = 0f
            return@LaunchedEffect
        }
        progress = 1 - scrollState.value / (appBarHeightInPx)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        UserProfileToolbar(
            username = state.user?.username ?: "Loading...",
            imageUrl = state.user?.image?.url,
            progress = progress,
            arrowShown = !state.fromNav,
            height = appBarHeight,
            navController = navController
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = scrollState)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(appBarHeight)
            )
            content()
        }
    }
}