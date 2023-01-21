package com.nasportfolio.auth.navigation

import android.content.Intent
import androidx.navigation.*
import androidx.navigation.compose.composable
import com.nasportfolio.auth.AuthScreen
import com.nasportfolio.auth.forgotPassword.ForgotPasswordScreen
import com.nasportfolio.auth.resetpassword.ResetPasswordFromEmailScreen
import com.nasportfolio.auth.splash.SplashScreen
import com.nasportfolio.common.navigation.*

fun NavGraphBuilder.authScreenComposable(navController: NavHostController) {
    composable(splashScreenRoute) {
        SplashScreen(navController = navController)
    }
    composable(authScreenRoute) {
        AuthScreen(navController = navController)
    }
    composable(route = forgotPasswordRoute) {
        ForgotPasswordScreen(navController = navController)
    }
    composable(
        route = resetPasswordFromEmailRoute,
        deepLinks = listOf(
            navDeepLink {
                uriPattern = "${resetPasswordFromEmailDeepLink}?e={email}&c={credential}"
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
}

