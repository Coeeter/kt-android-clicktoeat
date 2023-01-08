package com.nasportfolio.restaurant.home

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
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
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val scaffoldState = rememberScaffoldState()
    val state by homeViewModel.state.collectAsState()
    var permissionError by remember {
        mutableStateOf<String?>(null)
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
    }
}