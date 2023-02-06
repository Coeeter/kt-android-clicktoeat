package com.nasportfolio.restaurant.home.components

import android.view.MotionEvent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.nasportfolio.common.components.loading.CltShimmer
import com.nasportfolio.common.components.typography.CltHeading
import com.nasportfolio.common.modifier.gradientBackground
import com.nasportfolio.common.navigation.navigateToRestaurantDetails
import com.nasportfolio.common.theme.lightOrange
import com.nasportfolio.common.theme.mediumOrange
import com.nasportfolio.domain.branch.Branch
import com.nasportfolio.restaurant.home.HomeState
import kotlinx.coroutines.delay

@Composable
@OptIn(ExperimentalComposeUiApi::class)
fun RestaurantsNearYouSection(
    navController: NavHostController,
    state: HomeState,
    setIsScrollEnabled: (Boolean) -> Unit
) {
    var isAnimationDone by rememberSaveable {
        mutableStateOf(false)
    }
    var isMapLoaded by remember {
        mutableStateOf(false)
    }
    var selectedBranch by remember {
        mutableStateOf<Branch?>(null)
    }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(1.3610, 103.8200),
            10.25f
        )
    }

    LaunchedEffect(state.currentLocation, isMapLoaded, state.isRefreshing) {
        if (state.isRefreshing) {
            isAnimationDone = false
            return@LaunchedEffect
        }
        if (isAnimationDone || state.isLoading) return@LaunchedEffect
        if (!isMapLoaded) {
            delay(2000L)
            isMapLoaded = true
            return@LaunchedEffect
        }
        state.currentLocation ?: return@LaunchedEffect
        val position = CameraPosition.fromLatLngZoom(
            state.currentLocation,
            16f
        )
        cameraPositionState.animate(
            CameraUpdateFactory.newCameraPosition(position)
        )
        delay(2000L)
        cameraPositionState.animate(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(1.3610, 103.8200),
                10.25f
            )
        )
        isAnimationDone = true
    }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 5.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CltHeading(text = "Restaurants near you")
            AnimatedVisibility(
                visible = selectedBranch != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                IconButton(
                    modifier = Modifier.size(30.dp),
                    onClick = {
                        navController.navigateToRestaurantDetails(
                            restaurantId = selectedBranch!!.restaurant.id
                        )
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Storefront,
                        contentDescription = null,
                        modifier = Modifier.gradientBackground(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    lightOrange,
                                    mediumOrange
                                )
                            )
                        )
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(5.dp))
        if (!state.isRefreshing && !state.isLoading) GoogleMap(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 5.dp)
                .aspectRatio(1f)
                .border(2.dp, mediumOrange)
                .motionEventSpy {
                    when (it.action) {
                        MotionEvent.ACTION_DOWN -> {
                            setIsScrollEnabled(false)
                            selectedBranch = null
                        }
                        MotionEvent.ACTION_UP -> {
                            setIsScrollEnabled(true)
                        }
                    }
                },
            cameraPositionState = cameraPositionState,
            onMapLoaded = { isMapLoaded = true }
        ) {
            repeat(state.branches.size) { index ->
                val branch = state.branches[index]
                val markerState = MarkerState(
                    position = LatLng(
                        branch.latitude,
                        branch.longitude
                    )
                )
                MarkerInfoWindow(
                    state = markerState,
                    onClick = {
                        selectedBranch = branch
                        false
                    }
                ) {
                    Column(
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colors.background,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = branch.restaurant.name)
                        Text(text = branch.address)
                    }
                }
            }
            state.currentLocation?.let {
                Marker(
                    state = MarkerState(
                        position = state.currentLocation
                    ),
                    title = "Your location"
                )
            }
        }
        if (state.isRefreshing || state.isLoading) CltShimmer(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 5.dp)
                .aspectRatio(1f)
                .border(2.dp, mediumOrange)
                .zIndex(100f)
        )
    }
}