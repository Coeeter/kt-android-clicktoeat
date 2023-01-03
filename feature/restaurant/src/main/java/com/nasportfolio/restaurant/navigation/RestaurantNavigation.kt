package com.nasportfolio.restaurant.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.nasportfolio.common.navigation.createBranchScreenRoute
import com.nasportfolio.common.navigation.createRestaurantScreenRoute
import com.nasportfolio.common.navigation.homeScreenRoute
import com.nasportfolio.restaurant.create.branch.CreateBranchScreen
import com.nasportfolio.restaurant.create.restaurant.CreateRestaurantScreen
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
        CreateBranchScreen(navController = navController)
    }
}