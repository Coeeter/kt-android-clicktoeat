package com.nasportfolio.restaurant.home.components

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.nasportfolio.common.components.CltRestaurantCard
import com.nasportfolio.common.components.loading.CltLoadingRestaurantCard
import com.nasportfolio.common.navigation.navigateToRestaurantDetails
import com.nasportfolio.restaurant.home.HomeState
import com.nasportfolio.restaurant.home.HomeViewModel

@OptIn(ExperimentalAnimationApi::class, ExperimentalFoundationApi::class)
fun LazyListScope.allRestaurantsSection(
    state: HomeState,
    homeViewModel: HomeViewModel,
    navController: NavHostController
) {
    if (state.isLoading) items(3) {
        Row {
            repeat(2) {
                CltLoadingRestaurantCard(
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
    if (!state.isLoading) items(state.restaurantList.chunked(2)) { chunk ->
        Row {
            repeat(chunk.size) {
                CltRestaurantCard(
                    modifier = Modifier.weight(1f),
                    restaurant = chunk[it],
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
            if (chunk.size == 1) Box(modifier = Modifier.weight(1f))
        }
    }
}