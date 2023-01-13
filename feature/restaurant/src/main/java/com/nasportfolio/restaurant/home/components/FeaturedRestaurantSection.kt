package com.nasportfolio.restaurant.home.components

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavHostController
import com.nasportfolio.common.navigation.navigateToRestaurantDetails
import com.nasportfolio.restaurant.home.HomeState
import com.nasportfolio.restaurant.home.HomeViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun FeaturedRestaurantsSection(
    state: HomeState,
    homeViewModel: HomeViewModel,
    navController: NavHostController,
    width: Dp
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
        if (isLoading) Row(
            modifier = Modifier.horizontalScroll(
                state = rememberScrollState(),
                enabled = false
            )
        ) {
            repeat(3) {
                LoadingRestaurantCard(
                    modifier = Modifier.width(width)
                )
            }
        }
        if (!isLoading) LazyRow {
            items(state.featuredRestaurants.size) {
                val index = state.featuredRestaurants[it]

                RestaurantCard(
                    modifier = Modifier.width(width),
                    restaurant = state.restaurantList[index],
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
        }
    }
}