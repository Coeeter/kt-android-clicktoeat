package com.nasportfolio.common.navigation

import androidx.navigation.NavHostController

const val homeScreenRoute = "/home"
const val createRestaurantScreenRoute = "/create"

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

fun NavHostController.navigateToCreateRestaurant(
    popUpTo: String? = null
) {
    navigate(createRestaurantScreenRoute) {
        popUpTo ?: return@navigate
        popUpTo(popUpTo) {
            inclusive = true
        }
    }
}