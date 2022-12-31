package com.nasportfolio.clicktoeat

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.nasportfolio.auth.navigation.authScreenComposable
import com.nasportfolio.common.navigation.homeScreenRoute
import com.nasportfolio.common.navigation.splashScreenRoute
import com.nasportfolio.restaurant.home.HomeScreen

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