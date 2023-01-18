package com.nasportfolio.common.navigation

import androidx.navigation.NavHostController

const val userProfileScreen = "/users"
const val settingsScreen = "/settings"

fun NavHostController.navigateToUserProfile(
    userId: String,
    popUpTo: String? = null
) {
    navigate("$userProfileScreen/$userId/false") {
        popUpTo ?: return@navigate
        popUpTo(popUpTo) {
            inclusive = true
        }
    }
}