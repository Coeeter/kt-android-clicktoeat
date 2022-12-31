package com.nasportfolio.common.navigation

import androidx.navigation.NavHostController

const val homeScreenRoute = "/home"
const val searchScreenRoute = "/search"
const val restaurantDetailsScreenRoute = "/restaurant"
const val userProfileScreen = "/users"
const val settingsScreen = "/settings"

fun NavHostController.navigateToHomeScreen(
    popUpTo: String? = null
) {
    navigate(homeScreenRoute) {
        popUpTo ?: return@navigate
        popUpTo(popUpTo) {
            inclusive = true
        }
    }
}

