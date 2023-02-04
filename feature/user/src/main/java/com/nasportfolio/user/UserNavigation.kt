package com.nasportfolio.user

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.runtime.remember
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.nasportfolio.common.components.animation.CltAnimatedFadeThrough
import com.nasportfolio.common.navigation.deleteUserScreen
import com.nasportfolio.common.navigation.updateUserPasswordScreen
import com.nasportfolio.common.navigation.updateUserScreen
import com.nasportfolio.common.navigation.userProfileScreen
import com.nasportfolio.user.delete.DeleteAccountScreen
import com.nasportfolio.user.profile.UserProfileScreen
import com.nasportfolio.user.update.account.UpdateUserScreen
import com.nasportfolio.user.update.password.UpdatePasswordScreen

fun NavGraphBuilder.userComposable(navController: NavHostController) {
    composable(
        route = "$userProfileScreen/{userId}",
        arguments = listOf(
            navArgument(name = "userId") {
                type = NavType.StringType
                nullable = false
                defaultValue = ""
            }
        )
    ) {
        val visibleState = remember(it) {
            MutableTransitionState(it.arguments?.getString("userId") != "null").apply {
                targetState = it.destination.route == "$userProfileScreen/{userId}"
            }
        }
        CltAnimatedFadeThrough(visibleState = visibleState) {
            UserProfileScreen(navController = navController)
        }
    }
    composable(route = updateUserScreen) {
        UpdateUserScreen(navController = navController)
    }
    composable(route = updateUserPasswordScreen) {
        UpdatePasswordScreen(navController = navController)
    }
    composable(route = deleteUserScreen) {
        DeleteAccountScreen(navController = navController)
    }
}