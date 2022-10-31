package com.nasportfolio.clicktoeat.ui

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.nasportfolio.clicktoeat.ui.screens.auth.AuthScreen
import com.nasportfolio.clicktoeat.ui.screens.auth.ResetPasswordFromEmailScreen
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
        composable(
            route = Screen.ResetPasswordFromLinkScreen.route,
            deepLinks = listOf(
                navDeepLink {
                    uriPattern =
                        "${Screen.ResetPasswordFromLinkScreen.deepLink}?e={email}&c={credential}"
                    action = Intent.ACTION_VIEW
                }
            ),
            arguments = listOf(
                navArgument("email") {
                    defaultValue = ""
                    type = NavType.StringType
                },
                navArgument("credential") {
                    defaultValue = ""
                    type = NavType.StringType
                }
            )
        ) { entry ->
            val email = entry.arguments?.getString("email")
            val credential = entry.arguments?.getString("credential")

            ResetPasswordFromEmailScreen(
                email = email,
                credential = credential,
                navController = navController
            )
        }
        composable(Screen.HomeScreen.route) {
            HomeScreen()
        }
    }
}