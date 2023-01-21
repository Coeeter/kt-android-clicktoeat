package com.nasportfolio.common.navigation

import androidx.navigation.NavHostController

const val userProfileScreen = "/users"
const val updateUserScreen = "/update/user"
const val updateUserPasswordScreen = "/update/user-password"

fun NavHostController.navigateToUserProfile(
    userId: String?,
    popUpTo: String? = null
) {
    navigate("$userProfileScreen/$userId") {
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

fun NavHostController.navigateToUpdatePasswordScreen(
    popUpTo: String? = null
) {
    navigate(updateUserPasswordScreen) {
        popUpTo ?: return@navigate
        popUpTo(popUpTo) {
            inclusive = true
        }
    }
}