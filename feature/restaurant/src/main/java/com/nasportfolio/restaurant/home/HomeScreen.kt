package com.nasportfolio.restaurant.home

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Logout
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.nasportfolio.common.components.buttons.CltFloatingActionButton
import com.nasportfolio.common.components.effects.CltLaunchFlowCollector
import com.nasportfolio.common.components.typography.CltHeading
import com.nasportfolio.common.modifier.scrollEnabled
import com.nasportfolio.common.navigation.homeScreenRoute
import com.nasportfolio.common.navigation.navigateToAuthScreen
import com.nasportfolio.common.navigation.navigateToCreateRestaurant
import com.nasportfolio.restaurant.home.components.*
import com.nasportfolio.test.tags.TestTags

@OptIn(ExperimentalAnimationApi::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val config = LocalConfiguration.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val scaffoldState = rememberScaffoldState()
    val state by homeViewModel.state.collectAsState()
    var isScrollEnabled by remember {
        mutableStateOf(true)
    }
    var permissionError by remember {
        mutableStateOf<String?>(null)
    }
    val width = remember {
        ((config.screenWidthDp - 30) / 2).dp
    }

    val requestPermissions = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) {
        val hasPermissions = it[ACCESS_FINE_LOCATION] == true && it[ACCESS_COARSE_LOCATION] == true
        if (hasPermissions) return@rememberLauncherForActivityResult
        permissionError = "This app needs location permissions to run properly"
    }

    CltLaunchFlowCollector(
        lifecycleOwner = lifecycleOwner,
        flow = homeViewModel.errorChannel
    ) {
        scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
        scaffoldState.snackbarHostState.showSnackbar(it, "Okay")
    }

    LaunchedEffect(true) {
        val hasFinePermission = ContextCompat.checkSelfPermission(
            context,
            ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val hasCoarsePermission = ContextCompat.checkSelfPermission(
            context,
            ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (!hasFinePermission || !hasCoarsePermission) requestPermissions.launch(
            arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION)
        )
    }

    LaunchedEffect(permissionError) {
        permissionError ?: return@LaunchedEffect
        scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
        scaffoldState.snackbarHostState.showSnackbar(permissionError!!, "Okay")
    }

    LaunchedEffect(state.isLoading) {
        isScrollEnabled = !state.isLoading
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = state.currentUserUsername?.let {
                            "Welcome, $it"
                        } ?: "Loading..."
                    )
                },
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
                modifier = Modifier.testTag(TestTags.ADD_RESTAURANT_FAB),
                onClick = navController::navigateToCreateRestaurant
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        }
    ) { padding ->
        SwipeRefresh(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            state = rememberSwipeRefreshState(isRefreshing = state.isRefreshing),
            onRefresh = { homeViewModel.refreshPage() }
        ) {
            if (state.isLoading || state.restaurantList.isNotEmpty()) LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .scrollEnabled(isScrollEnabled)
                    .testTag(TestTags.LAZY_COLUMN),
                contentPadding = PaddingValues(5.dp)
            ) {
                item {
                    CltHeading(
                        text = "Your Favorites",
                        modifier = Modifier.padding(start = 5.dp)
                    )
                }
                item {
                    FavoriteRestaurantSection(
                        state = state,
                        width = width,
                        homeViewModel = homeViewModel,
                        navController = navController,
                        config = config
                    )
                }
                item {
                    CltHeading(
                        text = "Featured Restaurants",
                        modifier = Modifier.padding(start = 5.dp)
                    )
                }
                item {
                    FeaturedRestaurantsSection(
                        state = state,
                        homeViewModel = homeViewModel,
                        navController = navController,
                        width = width
                    )
                }
                item {
                    RestaurantsNearYouSection(
                        navController = navController,
                        state = state,
                        setIsScrollEnabled = {
                            isScrollEnabled = it
                        }
                    )
                }
                item {
                    CltHeading(
                        text = "All Restaurants",
                        modifier = Modifier.padding(start = 5.dp)
                    )
                }
                allRestaurantsSection(
                    state = state,
                    homeViewModel = homeViewModel,
                    navController = navController
                )
            }
            if (!state.isLoading && state.restaurantList.isEmpty()) Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                EmptyRestaurants()
            }
        }
    }
}