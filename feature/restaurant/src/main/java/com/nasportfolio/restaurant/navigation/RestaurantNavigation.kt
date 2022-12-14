package com.nasportfolio.restaurant.navigation

import androidx.activity.compose.BackHandler
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.nasportfolio.common.navigation.*
import com.nasportfolio.restaurant.create.branch.CreateBranchScreen
import com.nasportfolio.restaurant.create.restaurant.CreateRestaurantScreen
import com.nasportfolio.restaurant.details.RestaurantDetailsScreen
import com.nasportfolio.restaurant.home.HomeScreen

fun NavGraphBuilder.restaurantComposable(
    navController: NavHostController
) {
    composable(homeScreenRoute) {
        HomeScreen(navController = navController)
    }
    composable(createRestaurantScreenRoute) {
        CreateRestaurantScreen(navController = navController)
    }
    composable(
        route = "$createBranchScreenRoute/{restaurantId}",
        arguments = listOf(
            navArgument(name = "restaurantId") {
                defaultValue = ""
                type = NavType.StringType
                nullable = false
            }
        )
    ) {
        BackHandler(enabled = true) {
            navController.navigateToHomeScreen(
                popUpTo = "$createBranchScreenRoute/{restaurantId}"
            )
        }
        CreateBranchScreen(navController = navController)
    }
    composable(
        route = "$restaurantDetailScreenRoute/{restaurantId}",
        arguments = listOf(
            navArgument(name = "restaurantId") {
                defaultValue = ""
                type = NavType.StringType
                nullable = false
            }
        )
    ) {
        RestaurantDetailsScreen(navController = navController)
    }
}