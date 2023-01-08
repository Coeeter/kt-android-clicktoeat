package com.nasportfolio.restaurant.details

import android.annotation.SuppressLint
import android.location.Location
import android.view.MotionEvent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.nasportfolio.common.components.CltHeading
import com.nasportfolio.common.components.CltShimmer
import com.nasportfolio.common.navigation.homeScreenRoute
import com.nasportfolio.common.navigation.navigateToHomeScreen
import com.nasportfolio.common.theme.mediumOrange
import com.nasportfolio.domain.restaurant.TransformedRestaurant
import com.nasportfolio.restaurant.details.components.ParallaxToolbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("MissingPermission")
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RestaurantDetailsScreen(
    navController: NavHostController,
    restaurantDetailsViewModel: RestaurantDetailsViewModel = hiltViewModel()
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val scaffoldState = rememberScaffoldState()
    val state by restaurantDetailsViewModel.state.collectAsState()
    var isScrollEnabled by remember {
        mutableStateOf(true)
    }


    LaunchedEffect(true) {
        lifecycleOwner.lifecycleScope.launch {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                restaurantDetailsViewModel.errorChannel.collect {
                    scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
                    scaffoldState.snackbarHostState.showSnackbar(it, "Okay")
                }
            }
        }
    }

    BackHandler(enabled = true) {
        if (!state.isUpdated) return@BackHandler run {
            navController.popBackStack()
        }
        navController.navigateToHomeScreen(
            popUpTo = homeScreenRoute
        )
    }

    Scaffold(
        scaffoldState = scaffoldState
    ) {
        ParallaxToolbar(
            state = state,
            navController = navController,
            isScrollEnabled = isScrollEnabled,
            toggleFavorite = {
                restaurantDetailsViewModel.toggleFavorite()
            }
        ) {
            state.restaurant?.let {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    Spacer(modifier = Modifier.height(10.dp))
                    DataSection(restaurant = it)
                    Spacer(modifier = Modifier.height(10.dp))
                    CltHeading(
                        text = "Description",
                        textAlign = TextAlign.Start,
                        fontSize = 30.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = it.description,
                        style = MaterialTheme.typography.h6.copy(
                            fontWeight = FontWeight.Normal
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    BranchesSection(
                        state = state,
                        setIsScrollEnabled = {
                            isScrollEnabled = it
                        }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    CltHeading(
                        text = "Reviews",
                        textAlign = TextAlign.Start,
                        fontSize = 30.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                }
            }
        }
    }
}

@Composable
private fun DataSection(restaurant: TransformedRestaurant) {
    Surface(
        elevation = 4.dp,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp, horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CltHeading(text = restaurant.averageRating.toString())
                Text(text = "AVG Rating")
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CltHeading(text = restaurant.ratingCount.toString())
                Text(text = "Reviews")
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CltHeading(text = restaurant.favoriteSize.toString())
                Text(text = "Favorites")
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun BranchesSection(
    state: RestaurantsDetailState,
    setIsScrollEnabled: (Boolean) -> Unit,
) {
    var isMapLoaded by remember {
        mutableStateOf(false)
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(1.35, 103.87),
            10f
        )
    }

    LaunchedEffect(state.currentLocation, isMapLoaded) {
        state.currentLocation ?: return@LaunchedEffect
        if (!isMapLoaded) return@LaunchedEffect
        val position = CameraPosition.fromLatLngZoom(
            state.currentLocation,
            16f
        )
        cameraPositionState.animate(
            CameraUpdateFactory.newCameraPosition(position)
        )
        delay(2000L)
        val distances = state.restaurant!!.branches.map {
            val results = FloatArray(1)
            Location.distanceBetween(
                state.currentLocation.latitude,
                state.currentLocation.longitude,
                it.latitude,
                it.longitude,
                results
            )
            results[0]
        }
        val smallest = distances.minByOrNull { it } ?: return@LaunchedEffect
        val index = distances.indexOf(smallest)
        if (index == -1) return@LaunchedEffect
        val branch = state.restaurant.branches[index]
        cameraPositionState.animate(
            CameraUpdateFactory.newLatLng(
                LatLng(branch.latitude, branch.longitude)
            )
        )
    }

    CltHeading(
        text = "Branches",
        textAlign = TextAlign.Start,
        fontSize = 30.sp,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(5.dp))
    state.currentLocation?.let { currentLocation ->
        GoogleMap(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .border(2.dp, mediumOrange)
                .motionEventSpy {
                    when (it.action) {
                        MotionEvent.ACTION_DOWN -> {
                            setIsScrollEnabled(false)
                        }
                        MotionEvent.ACTION_UP -> {
                            setIsScrollEnabled(true)
                        }
                    }
                },
            cameraPositionState = cameraPositionState,
            onMapLoaded = { isMapLoaded = true }
        ) {
            repeat(state.restaurant!!.branches.size) { index ->
                val branch = state.restaurant.branches[index]
                val markerState = MarkerState(
                    position = LatLng(
                        branch.latitude,
                        branch.longitude
                    )
                )
                MarkerInfoWindow(state = markerState) { marker ->
                    Column(
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colors.background,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = state.restaurant.name)
                        Text(text = branch.address)
                    }
                }
            }
            Marker(
                state = MarkerState(
                    position = currentLocation
                ),
                title = "Your location"
            )
        }
    } ?: CltShimmer(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .border(2.dp, mediumOrange)
    )
}
