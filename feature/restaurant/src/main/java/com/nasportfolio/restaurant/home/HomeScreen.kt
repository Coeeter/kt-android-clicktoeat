package com.nasportfolio.restaurant.home

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import com.nasportfolio.common.components.CltFloatingActionButton
import com.nasportfolio.common.modifier.scrollEnabled
import com.nasportfolio.common.navigation.homeScreenRoute
import com.nasportfolio.common.navigation.navigateToAuthScreen
import com.nasportfolio.common.navigation.navigateToCreateRestaurant
import com.nasportfolio.common.navigation.navigateToRestaurantDetails
import com.nasportfolio.common.theme.lightOrange
import com.nasportfolio.restaurant.home.components.LoadingRestaurantCard
import com.nasportfolio.restaurant.home.components.RestaurantCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val state by homeViewModel.state.collectAsState()
    val scaffoldState = rememberScaffoldState()

    LaunchedEffect(true) {
        lifecycleOwner.lifecycleScope.launch {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.errorChannel.collect {
                    scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
                    scaffoldState.snackbarHostState.showSnackbar(it, "Okay")
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text(text = "Home") },
                actions = {
                    IconButton(
                        onClick = {
                            homeViewModel.logout()
                            navController.navigateToAuthScreen(
                                popUpTo = homeScreenRoute
                            )
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            CltFloatingActionButton(
                onClick = navController::navigateToCreateRestaurant
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyVerticalGrid(
                modifier = Modifier
                    .fillMaxSize()
                    .scrollEnabled(enabled = !state.isLoading),
                cells = GridCells.Fixed(2),
                contentPadding = PaddingValues(5.dp)
            ) {
                if (state.isLoading)
                    items(10) {
                        LoadingRestaurantCard()
                    }
                if (!state.isLoading)
                    items(state.restaurantList) {
                        RestaurantCard(
                            restaurant = it,
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
            if (!state.isLoading)
                state.restaurantList.firstOrNull() ?: Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        modifier = Modifier.size(250.dp),
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        tint = lightOrange
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "Wow such empty...",
                        style = MaterialTheme.typography.h5
                    )
                    Text(
                        text = "Try creating a restaurant!",
                        style = MaterialTheme.typography.h6.copy(
                            fontWeight = FontWeight.Light
                        )
                    )
                }
        }
    }
}