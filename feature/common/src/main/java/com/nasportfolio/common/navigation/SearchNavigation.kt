package com.nasportfolio.common.navigation

import androidx.navigation.NavHostController

const val searchScreenRoute = "/search"

fun NavHostController.navigateToSearchScreen(
    popUpTo: String?
) {
    navigate(searchScreenRoute) {
        popUpTo ?: return@navigate
        popUpTo(popUpTo) {
            inclusive = true
        }
    }
}