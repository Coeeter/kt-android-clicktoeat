package com.nasportfolio.user.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.nasportfolio.user.UserProfileState

@Composable
fun ToolbarWithContent(
    state: UserProfileState,
    navController: NavHostController,
    isRefreshing: Boolean,
    refresh: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    val scrollState = rememberScrollState()
    val appBarHeight = remember { 250.dp }
    val progress = rememberScrollProgress(
        appBarHeight = appBarHeight,
        scrollState = scrollState
    )

    SwipeRefresh(
        state = SwipeRefreshState(isRefreshing = isRefreshing),
        onRefresh = refresh
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            UserProfileToolbar(
                username = state.user?.username ?: "Loading...",
                imageUrl = state.user?.image?.url,
                progress = progress,
                arrowShown = !state.fromNav,
                appBarHeight = appBarHeight,
                navController = navController,
                isCurrentUser = state.loggedInUserId == state.user?.id,
                isLoading = state.isUserLoading,
                uploadPhoto = {},
                removePhoto = {}
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
}

@Composable
private fun rememberScrollProgress(
    appBarHeight: Dp,
    scrollState: ScrollState
): Float {
    val density = LocalDensity.current
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

    return progress
}