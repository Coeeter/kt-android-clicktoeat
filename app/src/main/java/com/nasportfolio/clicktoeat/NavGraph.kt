package com.nasportfolio.clicktoeat

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.nasportfolio.clicktoeat.screens.auth.AuthScreen
import com.nasportfolio.clicktoeat.screens.auth.ResetPasswordFromEmailScreen
import com.nasportfolio.clicktoeat.screens.home.HomeScreen
import com.nasportfolio.clicktoeat.screens.splash.SplashScreen
import com.nasportfolio.clicktoeat.utils.Screen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.SplashScreen.route
    ) {
        composable(Screen.SplashScreen.route) {
            SplashScreen(navController = navController)
        }
        composable(Screen.AuthScreen.route) {
            AuthScreen(navController = navController)
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