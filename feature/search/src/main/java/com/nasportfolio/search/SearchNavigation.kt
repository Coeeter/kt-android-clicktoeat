package com.nasportfolio.search

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.nasportfolio.common.navigation.searchScreenRoute

fun NavGraphBuilder.searchComposable(
    navController: NavHostController
) {
    composable(searchScreenRoute) {
        SearchScreen(navController = navController)
    }
}