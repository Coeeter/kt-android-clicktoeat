package com.nasportfolio.clicktoeat.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.nasportfolio.clicktoeat.ui.screens.auth.AuthScreen
import com.nasportfolio.clicktoeat.ui.screens.home.HomeScreen
import com.nasportfolio.clicktoeat.ui.utils.Screen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.AuthScreen.route
    ) {
        composable(Screen.AuthScreen.route) {
            AuthScreen()
        }
        composable(Screen.HomeScreen.route) {
            HomeScreen()
        }
    }
}