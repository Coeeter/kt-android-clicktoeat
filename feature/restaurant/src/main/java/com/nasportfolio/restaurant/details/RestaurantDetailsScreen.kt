package com.nasportfolio.restaurant.details

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
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
import com.nasportfolio.common.components.CltHeading
import com.nasportfolio.common.navigation.homeScreenRoute
import com.nasportfolio.common.navigation.navigateToHomeScreen
import com.nasportfolio.restaurant.details.components.BranchesSection
import com.nasportfolio.restaurant.details.components.DataSection
import com.nasportfolio.restaurant.details.components.ParallaxToolbar
import com.nasportfolio.restaurant.details.components.ReviewSection
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

    Scaffold(scaffoldState = scaffoldState) {
        ParallaxToolbar(
            state = state,
            navController = navController,
            isScrollEnabled = isScrollEnabled,
            toggleFavorite = {
                restaurantDetailsViewModel.onEvent(
                    event = RestaurantDetailsEvent.ToggleFavoriteEvent
                )
            }
        ) {
            state.restaurant?.let { restaurant ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    Spacer(modifier = Modifier.height(10.dp))
                    DataSection(restaurant = restaurant)
                    Spacer(modifier = Modifier.height(10.dp))
                    CltHeading(
                        text = "Description",
                        textAlign = TextAlign.Start,
                        fontSize = 30.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = restaurant.description,
                        style = MaterialTheme.typography.h6.copy(
                            fontWeight = FontWeight.Normal
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    BranchesSection(
                        state = state,
                        setIsScrollEnabled = {
                            isScrollEnabled = it
                        },
                        setIsAnimationDone = {
                            restaurantDetailsViewModel.onEvent(
                                event = RestaurantDetailsEvent.AnimationOverEvent(it)
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    ReviewSection(
                        restaurant = restaurant,
                        restaurantDetailsViewModel = restaurantDetailsViewModel
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}