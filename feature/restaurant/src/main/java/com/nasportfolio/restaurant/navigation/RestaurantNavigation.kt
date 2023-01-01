package com.nasportfolio.restaurant.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.nasportfolio.common.navigation.createRestaurantScreenRoute
import com.nasportfolio.common.navigation.homeScreenRoute
import com.nasportfolio.restaurant.create.CreateRestaurantScreen
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
}