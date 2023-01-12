package com.nasportfolio.common.navigation

import androidx.navigation.NavHostController

const val homeScreenRoute = "/home"
const val restaurantDetailScreenRoute = "/restaurant"
const val createUpdateRestaurantScreenRoute = "create-update/restaurant"
const val createUpdateBranchScreenRoute = "create-update/branch"

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
    navigate("$createUpdateRestaurantScreenRoute/null") {
        popUpTo ?: return@navigate
        popUpTo(popUpTo) {
            inclusive = true
        }
    }
}

fun NavHostController.navigateToUpdateRestaurant(
    restaurantId: String,
    popUpTo: String? = null
) {
    navigate("$createUpdateRestaurantScreenRoute/$restaurantId") {
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
    navigate("$createUpdateBranchScreenRoute/$restaurantId/null") {
        popUpTo ?: return@navigate
        popUpTo(popUpTo) {
            inclusive = true
        }
    }
}

fun NavHostController.navigateToUpdateBranch(
    branchId: String,
    restaurantId: String,
    popUpTo: String? = null,
) {
    navigate("$createUpdateBranchScreenRoute/$restaurantId/$branchId") {
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