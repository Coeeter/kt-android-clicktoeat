package com.nasportfolio.user

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.nasportfolio.common.navigation.updateUserScreen
import com.nasportfolio.common.navigation.userProfileScreen
import com.nasportfolio.user.profile.UserProfileScreen
import com.nasportfolio.user.update.UpdateUserScreen

fun NavGraphBuilder.userComposable(navController: NavHostController) {
    composable(
        route = "$userProfileScreen/{userId}/{fromNav}",
        arguments = listOf(
            navArgument(name = "userId") {
                type = NavType.StringType
                nullable = false
                defaultValue = ""
            },
            navArgument(name = "fromNav") {
                type = NavType.BoolType
                nullable = false
                defaultValue = false
            }
        )
    ) {
        UserProfileScreen(navController = navController)
    }
    composable(route = updateUserScreen,) {
        UpdateUserScreen(navController = navController)
    }
}