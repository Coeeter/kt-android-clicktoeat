package com.nasportfolio.restaurant.details

import android.view.MotionEvent
import androidx.activity.compose.BackHandler
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
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.nasportfolio.common.components.CltHeading
import com.nasportfolio.common.navigation.homeScreenRoute
import com.nasportfolio.common.navigation.navigateToHomeScreen
import com.nasportfolio.common.theme.mediumOrange
import com.nasportfolio.restaurant.details.components.ParallaxToolbar
import kotlinx.coroutines.launch

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
                                CltHeading(text = it.averageRating.toString())
                                Text(text = "AVG Rating")
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CltHeading(text = it.ratingCount.toString())
                                Text(text = "Reviews")
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CltHeading(text = it.favoriteSize.toString())
                                Text(text = "Favorites")
                            }
                        }

                    }
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
                    CltHeading(
                        text = "Branches",
                        textAlign = TextAlign.Start,
                        fontSize = 30.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    GoogleMap(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .border(2.dp, mediumOrange)
                            .motionEventSpy {
                                when (it.action) {
                                    MotionEvent.ACTION_DOWN -> {
                                        isScrollEnabled = false
                                    }
                                    MotionEvent.ACTION_UP -> {
                                        isScrollEnabled = true
                                    }
                                }
                            }
                    ) {
                        repeat(it.branches.size) { index ->
                            val branch = it.branches[index]
                            Marker(
                                state = MarkerState(
                                    position = LatLng(
                                        branch.latitude,
                                        branch.longitude
                                    )
                                ),
                                title = branch.address
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}
