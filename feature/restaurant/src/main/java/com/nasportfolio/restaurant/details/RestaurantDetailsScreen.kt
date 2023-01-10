package com.nasportfolio.restaurant.details

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
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
import com.nasportfolio.restaurant.details.components.*
import kotlinx.coroutines.launch

@SuppressLint("MissingPermission")
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RestaurantDetailsScreen(
    navController: NavHostController,
    restaurantDetailsViewModel: RestaurantDetailsViewModel = hiltViewModel()
) {
    val config = LocalConfiguration.current
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
                Column(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
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
                        ReviewMetaDataSection(
                            restaurant = restaurant,
                            restaurantDetailsViewModel = restaurantDetailsViewModel
                        )
                    }
                    state.currentUserId?.let { userId ->
                        AnimatedVisibility(
                            visible = restaurant.comments.isNotEmpty(),
                            enter = slideInVertically(
                                initialOffsetY = { it * 2 },
                                animationSpec = tween(durationMillis = 500)
                            ),
                            exit = fadeOut()
                        ) {
                            LazyRow {
                                item { Spacer(modifier = Modifier.width(16.dp)) }
                                items(
                                    items = if (restaurant.comments.size <= 3) {
                                        restaurant.comments
                                    } else {
                                        restaurant.comments.subList(0, 3)
                                    },
                                ) {
                                    Row {
                                        CommentCard(
                                            modifier = Modifier.width(config.screenWidthDp.dp - 42.dp),
                                            comment = it,
                                            currentUserId = userId,
                                            editComment = {

                                            },
                                            deleteComment = {

                                            }
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                    }
                                }
                                item { Spacer(modifier = Modifier.width(6.dp)) }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}