package com.nasportfolio.restaurant.home.components

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.nasportfolio.common.navigation.navigateToRestaurantDetails
import com.nasportfolio.restaurant.home.HomeState
import com.nasportfolio.restaurant.home.HomeViewModel

@OptIn(ExperimentalAnimationApi::class, ExperimentalFoundationApi::class)
@Composable
fun AllRestaurantsSection(
    state: HomeState,
    homeViewModel: HomeViewModel,
    navController: NavHostController
) {
    AnimatedContent(
        targetState = state.isLoading,
        transitionSpec = {
            fadeIn(
                animationSpec = tween(durationMillis = 350)
            ) with fadeOut(
                animationSpec = tween(durationMillis = 350)
            )
        }
    ) { isLoading ->
        Column {
            if (isLoading) repeat(3) {
                Row {
                    repeat(2) {
                        LoadingRestaurantCard(
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            if (!isLoading) repeat(state.restaurantList.chunked(2).size) { chunk ->
                val row = state.restaurantList.chunked(2)[chunk]
                Row {
                    repeat(row.size) {
                        RestaurantCard(
                            modifier = Modifier.weight(1f),
                            restaurant = row[it],
                            toggleFavorite = { restaurantId ->
                                homeViewModel.toggleFavorite(restaurantId)
                            },
                            onClick = { restaurantId ->
                                navController.navigateToRestaurantDetails(
                                    restaurantId = restaurantId
                                )
                            }
                        )
                    }
                    if (row.size == 1) Box(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}