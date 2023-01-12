package com.nasportfolio.restaurant.details.components

import android.location.Location
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EditLocation
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.nasportfolio.common.components.CltHeading
import com.nasportfolio.common.components.CltShimmer
import com.nasportfolio.common.modifier.gradientBackground
import com.nasportfolio.common.navigation.navigateToUpdateBranch
import com.nasportfolio.common.theme.lightOrange
import com.nasportfolio.common.theme.mediumOrange
import com.nasportfolio.domain.branch.Branch
import com.nasportfolio.restaurant.details.RestaurantsDetailState
import kotlinx.coroutines.delay


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BranchesSection(
    navController: NavHostController,
    state: RestaurantsDetailState,
    setIsScrollEnabled: (Boolean) -> Unit,
    setIsAnimationDone: (Boolean) -> Unit,
    deleteBranch: (String) -> Unit
) {
    var isMapLoaded by remember {
        mutableStateOf(false)
    }
    var selectedBranch by remember {
        mutableStateOf<Branch?>(null)
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(1.35, 103.87),
            10f
        )
    }

    LaunchedEffect(state.currentLocation, isMapLoaded, state.restaurant?.branches) {
        state.restaurant?.branches ?: return@LaunchedEffect
        state.currentLocation ?: return@LaunchedEffect
        if (!isMapLoaded || state.isAnimationDone) return@LaunchedEffect
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
        setIsAnimationDone(true)
    }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CltHeading(
                text = "Branches",
                textAlign = TextAlign.Start,
                fontSize = 30.sp
            )
            AnimatedVisibility(
                visible = selectedBranch != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Row {
                    IconButton(
                        modifier = Modifier.size(40.dp),
                        onClick = {
                            navController.navigateToUpdateBranch(
                                branchId = selectedBranch!!.id,
                                restaurantId = state.restaurant!!.id
                            )
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.EditLocation,
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
                    if (state.restaurant!!.branches.size != 1) IconButton(
                        modifier = Modifier.size(40.dp),
                        onClick = {
                            deleteBranch(selectedBranch!!.id)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
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
        }
        Spacer(modifier = Modifier.height(5.dp))
        state.currentLocation?.let { currentLocation ->
            Map(
                setIsScrollEnabled = setIsScrollEnabled,
                cameraPositionState = cameraPositionState,
                state = state,
                currentLocation = currentLocation,
                setIsMapLoaded = { isMapLoaded = it },
                setIsMarkerClicked = { branch ->
                    selectedBranch = branch
                }
            )
        } ?: CltShimmer(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .border(2.dp, mediumOrange)
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun Map(
    setIsScrollEnabled: (Boolean) -> Unit,
    cameraPositionState: CameraPositionState,
    state: RestaurantsDetailState,
    currentLocation: LatLng,
    setIsMapLoaded: (Boolean) -> Unit,
    setIsMarkerClicked: (Branch?) -> Unit
) {
    GoogleMap(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .border(2.dp, mediumOrange)
            .motionEventSpy {
                when (it.action) {
                    MotionEvent.ACTION_DOWN -> {
                        setIsScrollEnabled(false)
                        setIsMarkerClicked(null)
                    }
                    MotionEvent.ACTION_UP -> {
                        setIsScrollEnabled(true)
                    }
                }
            },
        cameraPositionState = cameraPositionState,
        onMapLoaded = { setIsMapLoaded(true) }
    ) {
        repeat(state.restaurant!!.branches.size) { index ->
            val branch = state.restaurant.branches[index]
            val markerState = MarkerState(
                position = LatLng(
                    branch.latitude,
                    branch.longitude
                )
            )
            MarkerInfoWindow(
                state = markerState,
                onClick = {
                    setIsMarkerClicked(branch)
                    false
                }
            ) { marker ->
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
}
