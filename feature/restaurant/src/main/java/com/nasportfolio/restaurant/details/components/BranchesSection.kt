package com.nasportfolio.restaurant.details.components

import android.location.Location
import android.view.MotionEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.nasportfolio.common.components.CltHeading
import com.nasportfolio.common.components.CltShimmer
import com.nasportfolio.common.theme.mediumOrange
import com.nasportfolio.restaurant.details.RestaurantsDetailState
import kotlinx.coroutines.delay


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BranchesSection(
    state: RestaurantsDetailState,
    setIsScrollEnabled: (Boolean) -> Unit,
    setIsAnimationDone: (Boolean) -> Unit
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
        CltHeading(
            text = "Branches",
            textAlign = TextAlign.Start,
            fontSize = 30.sp,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(5.dp))
        state.currentLocation?.let { currentLocation ->
            Map(
                setIsScrollEnabled = setIsScrollEnabled,
                cameraPositionState = cameraPositionState,
                state = state,
                currentLocation = currentLocation,
                setIsMapLoaded = { isMapLoaded = it }
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
}
