package com.nasportfolio.common.navigation

import androidx.navigation.NavHostController

const val splashScreenRoute = "/splash"
const val authScreenRoute = "/auth"
const val forgotPasswordRoute = "/forgot-password"
const val resetPasswordFromEmailRoute = "/reset-password"
const val resetPasswordFromEmailDeepLink = "https://clicktoeat.nasportfolio.com/reset-password"

fun NavHostController.navigateToAuthScreen(
    popUpTo: String? = null
) {
    navigate(authScreenRoute) {
        popUpTo ?: return@navigate
        popUpTo(popUpTo) {
            inclusive = true
        }
    }
}

fun NavHostController.navigateToForgetPassword(
    popUpTo: String? = null
) {
    navigate(forgotPasswordRoute) {
        popUpTo ?: return@navigate
        popUpTo(popUpTo) {
            inclusive = true
        }
    }
}