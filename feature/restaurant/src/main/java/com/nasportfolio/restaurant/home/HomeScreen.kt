package com.nasportfolio.restaurant.home

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.nasportfolio.common.components.buttons.CltFloatingActionButton
import com.nasportfolio.common.components.typography.CltHeading
import com.nasportfolio.common.modifier.scrollEnabled
import com.nasportfolio.common.navigation.homeScreenRoute
import com.nasportfolio.common.navigation.navigateToAuthScreen
import com.nasportfolio.common.navigation.navigateToCreateRestaurant
import com.nasportfolio.common.navigation.navigateToRestaurantDetails
import com.nasportfolio.common.theme.lightOrange
import com.nasportfolio.common.theme.mediumOrange
import com.nasportfolio.restaurant.home.components.LoadingRestaurantCard
import com.nasportfolio.restaurant.home.components.RestaurantCard
import kotlinx.coroutines.launch

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
    var permissionError by remember {
        mutableStateOf<String?>(null)
    }
    val width = remember {
        ((config.screenWidthDp - 30) / 2).dp
    }

    val requestPermissions = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) {
        val hasPermissions = it[Manifest.permission.ACCESS_FINE_LOCATION] == true &&
                it[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (hasPermissions) return@rememberLauncherForActivityResult
        permissionError = "This app needs location permissions to run properly"
    }

    LaunchedEffect(true) {
        val hasFinePermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val hasCoarsePermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (!hasFinePermission || !hasCoarsePermission) requestPermissions.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
        lifecycleOwner.lifecycleScope.launch {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.errorChannel.collect {
                    scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
                    scaffoldState.snackbarHostState.showSnackbar(it, "Okay")
                }
            }
        }
    }

    LaunchedEffect(permissionError) {
        permissionError ?: return@LaunchedEffect
        scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
        scaffoldState.snackbarHostState.showSnackbar(permissionError!!, "Okay")
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
            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing = state.isRefreshing),
                onRefresh = { homeViewModel.refreshPage() }
            ) {
                if (state.isLoading || state.restaurantList.isNotEmpty()) LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .scrollEnabled(enabled = !state.isLoading),
                    contentPadding = PaddingValues(5.dp)
                ) {
                    item {
                        CltHeading(
                            text = "Your Favorites",
                            modifier = Modifier.padding(start = 5.dp)
                        )
                    }
                    favoriteRestaurantSection(
                        state = state,
                        width = width,
                        homeViewModel = homeViewModel,
                        navController = navController,
                        config = config
                    )
                    item {
                        CltHeading(
                            text = "Featured Restaurants",
                            modifier = Modifier.padding(start = 5.dp)
                        )
                    }
                    featuredRestaurantsSection(
                        state = state,
                        homeViewModel = homeViewModel,
                        navController = navController,
                        width = width
                    )
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
                if (!state.isLoading && state.restaurantList.isEmpty()) LazyColumn {
                    item {
                        EmptyRestaurants()
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyRestaurants() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            modifier = Modifier
                .size(250.dp)
                .graphicsLayer(alpha = 0.99f)
                .drawWithCache {
                    onDrawWithContent {
                        drawContent()
                        drawRect(
                            Brush.linearGradient(
                                colors = listOf(
                                    lightOrange,
                                    mediumOrange,
                                    lightOrange
                                )
                            ),
                            blendMode = BlendMode.SrcAtop
                        )
                    }
                },
            imageVector = Icons.Outlined.Info,
            contentDescription = null,
        )
        Spacer(modifier = Modifier.height(10.dp))
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

private fun LazyListScope.allRestaurantsSection(
    state: HomeState,
    homeViewModel: HomeViewModel,
    navController: NavHostController
) {
    if (state.isLoading) items(10) {
        Row {
            repeat(2) {
                LoadingRestaurantCard(
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
    if (!state.isLoading) items(state.restaurantList.chunked(2)) { chunk ->
        Row {
            repeat(chunk.size) {
                RestaurantCard(
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

private fun LazyListScope.featuredRestaurantsSection(
    state: HomeState,
    homeViewModel: HomeViewModel,
    navController: NavHostController,
    width: Dp
) {
    if (state.isLoading) item {
        Row(
            modifier = Modifier.horizontalScroll(
                state = rememberScrollState(),
                enabled = false
            )
        ) {
            repeat(5) {
                LoadingRestaurantCard(
                    modifier = Modifier.width(width)
                )
            }
        }
    }
    if (!state.isLoading) item {
        Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
            repeat(state.featuredRestaurants.size) {
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

private fun LazyListScope.favoriteRestaurantSection(
    state: HomeState,
    width: Dp,
    homeViewModel: HomeViewModel,
    navController: NavHostController,
    config: Configuration
) {
    if (state.isLoading) item {
        Row(
            modifier = Modifier.horizontalScroll(
                state = rememberScrollState(),
                enabled = false
            )
        ) {
            repeat(5) {
                LoadingRestaurantCard(
                    modifier = Modifier.width(width)
                )
            }
        }
    }
    if (!state.isLoading) item {
        Box {
            Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                repeat(state.favRestaurants.size) {
                    val index = state.favRestaurants[it]

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
            if (state.favRestaurants.isEmpty()) Column(
                modifier = Modifier.width(config.screenWidthDp.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(30.dp))
                Row {
                    repeat(2) {
                        LoadingRestaurantCard(
                            modifier = Modifier
                                .width(width)
                                .offset(
                                    x = if (it == 0) 60.dp else (-60).dp,
                                    y = if (it == 0) (-20).dp else 20.dp
                                ),
                            shimmer = false,
                            elevation = if (isSystemInDarkTheme()) {
                                if (it == 0) 4.dp else 20.dp
                            } else {
                                if (it == 0) 10.dp else 12.dp
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(30.dp))
                Text(
                    text = buildAnnotatedString {
                        withStyle(ParagraphStyle(textAlign = TextAlign.Center)) {
                            withStyle(SpanStyle(fontSize = 24.sp)) {
                                append("No favorite restaurants yet...\n")
                            }
                            withStyle(
                                SpanStyle(
                                    color = MaterialTheme.colors.onBackground.copy(
                                        alpha = if (isSystemInDarkTheme()) {
                                            0.5f
                                        } else {
                                            0.7f
                                        }
                                    )
                                )
                            ) {
                                append("Try adding one now!")
                            }
                        }
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}