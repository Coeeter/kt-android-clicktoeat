package com.nasportfolio.common.navigation

import androidx.navigation.NavHostController

const val homeScreenRoute = "/home"
const val restaurantDetailScreenRoute = "/restaurant"
const val createRestaurantScreenRoute = "/restaurant/create"
const val createBranchScreenRoute = "/branch/create"

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

fun NavHostController.navigateToCreateBranch(
    restaurantId: String,
    popUpTo: String? = null,
) {
    navigate("$createBranchScreenRoute/$restaurantId") {
        popUpTo ?: return@navigate
        popUpTo(popUpTo) {
            inclusive = true
        }
    }
}

fun NavHostController.navigateToRestaurantDetails(
    restaurantId: String,
    popUpTo: String? = null
) {
    navigate("$restaurantDetailScreenRoute/$restaurantId") {
        popUpTo ?: return@navigate
        popUpTo(popUpTo) {
            inclusive = true
        }
    }
}