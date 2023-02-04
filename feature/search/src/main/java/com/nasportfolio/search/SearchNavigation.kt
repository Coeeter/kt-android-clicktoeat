package com.nasportfolio.search

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.runtime.remember
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.nasportfolio.common.components.animation.CltAnimatedFadeThrough
import com.nasportfolio.common.navigation.searchScreenRoute

fun NavGraphBuilder.searchComposable(
    navController: NavHostController
) {
    composable(searchScreenRoute) {
        val state = remember(it) {
            MutableTransitionState(false).apply {
                targetState = it.destination.route == searchScreenRoute
            }
        }
        CltAnimatedFadeThrough(visibleState = state) {
            SearchScreen(navController = navController)
        }
    }
}