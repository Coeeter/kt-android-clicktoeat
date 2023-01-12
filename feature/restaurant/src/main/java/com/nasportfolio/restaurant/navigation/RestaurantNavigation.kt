package com.nasportfolio.restaurant.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.nasportfolio.common.navigation.*
import com.nasportfolio.restaurant.createUpdate.branch.CreateUpdateBranchScreen
import com.nasportfolio.restaurant.createUpdate.restaurant.CreateUpdateRestaurantScreen
import com.nasportfolio.restaurant.details.RestaurantDetailsScreen
import com.nasportfolio.restaurant.home.HomeScreen

fun NavGraphBuilder.restaurantComposable(
    navController: NavHostController
) {
    composable(homeScreenRoute) {
        HomeScreen(navController = navController)
    }
    composable(
        route = "$createUpdateRestaurantScreenRoute/{restaurantId}",
        arguments = listOf(
            navArgument(name = "restaurantId") {
                type = NavType.StringType
            }
        )
    ) {
        CreateUpdateRestaurantScreen(navController = navController)
    }
    composable(
        route = "$createUpdateBranchScreenRoute/{restaurantId}/{branchId}",
        arguments = listOf(
            navArgument(name = "restaurantId") {
                defaultValue = ""
                type = NavType.StringType
                nullable = false
            },
            navArgument(name = "branchId") {
                type = NavType.StringType
            }
        )
    ) {
        CreateUpdateBranchScreen(navController = navController)
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