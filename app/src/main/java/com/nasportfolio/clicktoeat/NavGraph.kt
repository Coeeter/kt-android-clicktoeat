package com.nasportfolio.clicktoeat

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.nasportfolio.auth.navigation.authScreenComposable
import com.nasportfolio.auth.navigation.splashScreenRoute
import com.nasportfolio.clicktoeat.screens.home.HomeScreen
import com.nasportfolio.common.navigation.homeScreenRoute

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = splashScreenRoute
    ) {
        authScreenComposable(navController = navController)
        composable(homeScreenRoute) {
            HomeScreen(navController = navController)
        }
    }
}