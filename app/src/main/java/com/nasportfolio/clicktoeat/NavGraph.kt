package com.nasportfolio.clicktoeat

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.nasportfolio.auth.navigation.authScreenComposable
import com.nasportfolio.common.navigation.splashScreenRoute
import com.nasportfolio.restaurant.navigation.restaurantComposable

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = splashScreenRoute
    ) {
        authScreenComposable(navController = navController)
        restaurantComposable(navController = navController)
    }
}