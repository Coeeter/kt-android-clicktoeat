package com.nasportfolio.common.navigation

import androidx.navigation.NavHostController

const val userProfileScreen = "/users"
const val updateUserScreen = "/update/user"

fun NavHostController.navigateToUserProfile(
    userId: String,
    fromNav: Boolean = false,
    popUpTo: String? = null
) {
    navigate("$userProfileScreen/$userId/$fromNav") {
        popUpTo ?: return@navigate
        popUpTo(popUpTo) {
            inclusive = true
        }
    }
}

fun NavHostController.navigateToUpdateUser(
    popUpTo: String? = null
) {
    navigate(updateUserScreen) {
        popUpTo ?: return@navigate
        popUpTo(popUpTo) {
            inclusive = true
        }
    }
}