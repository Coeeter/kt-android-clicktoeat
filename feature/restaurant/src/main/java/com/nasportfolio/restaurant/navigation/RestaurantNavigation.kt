package com.nasportfolio.restaurant.navigation

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.runtime.remember
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.nasportfolio.common.components.animation.CltAnimatedFadeThrough
import com.nasportfolio.common.navigation.*
import com.nasportfolio.restaurant.comments.CommentScreen
import com.nasportfolio.restaurant.createUpdate.branch.CreateUpdateBranchScreen
import com.nasportfolio.restaurant.createUpdate.restaurant.CreateUpdateRestaurantScreen
import com.nasportfolio.restaurant.details.RestaurantDetailsScreen
import com.nasportfolio.restaurant.home.HomeScreen
import com.nasportfolio.restaurant.likedislike.LikeDislikeScreen

fun NavGraphBuilder.restaurantComposable(
    navController: NavHostController
) {
    composable(homeScreenRoute) {
        val state = remember(it) {
            MutableTransitionState(false).apply {
                targetState = it.destination.route == homeScreenRoute
            }
        }
        CltAnimatedFadeThrough(visibleState = state) {
            HomeScreen(navController = navController)
        }
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
    composable(
        route = "$commentsScreenRoute/{restaurantId}",
        arguments = listOf(
            navArgument(name = "restaurantId") {
                defaultValue = ""
                type = NavType.StringType
                nullable = false
            }
        )
    ) {
        CommentScreen(navController = navController)
    }
    composable(
        route = "$likeDislikeRoute/{commentId}/{index}",
        arguments = listOf(
            navArgument(name = "commentId") {
                defaultValue = ""
                type = NavType.StringType
                nullable = false
            },
            navArgument(name = "index") {
                defaultValue = 0
                type = NavType.IntType
                nullable = false
            }
        )
    ) {
        LikeDislikeScreen(navController = navController)
    }
}