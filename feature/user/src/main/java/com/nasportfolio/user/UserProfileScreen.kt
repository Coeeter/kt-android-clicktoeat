package com.nasportfolio.user

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import com.nasportfolio.common.components.CltCommentCard
import com.nasportfolio.common.components.CltRestaurantCard
import com.nasportfolio.common.components.TopBar
import com.nasportfolio.common.components.loading.CltLoadingRestaurantCard
import com.nasportfolio.common.components.loading.CltShimmer
import com.nasportfolio.common.components.typography.CltHeading
import com.nasportfolio.common.navigation.navigateToRestaurantDetails
import com.nasportfolio.common.utils.toStringAsFixed
import com.nasportfolio.user.components.ToolbarWithContent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMotionApi::class)
@Composable
fun UserProfileScreen(
    navController: NavHostController,
    userProfileViewModel: UserProfileViewModel = hiltViewModel()
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val scaffoldState = rememberScaffoldState()
    val state by userProfileViewModel.state.collectAsState()

    LaunchedEffect(true) {
        lifecycleOwner.lifecycleScope.launch {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                userProfileViewModel.errorChannel.collect {
                    scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
                    scaffoldState.snackbarHostState.showSnackbar(it, "Okay")
                }
            }
        }
    }

    Scaffold(scaffoldState = scaffoldState) {
        ToolbarWithContent(
            state = state,
            navController = navController,
            isRefreshing = state.isRefreshing,
            refresh = userProfileViewModel::refresh
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp),
            ) {
                UserStats(state = state)
                Spacer(modifier = Modifier.height(16.dp))
                CltHeading(text = "Favorite Restaurants")
                Spacer(modifier = Modifier.height(5.dp))
                if (state.isRestaurantLoading) repeat(2) {
                    Row {
                        repeat(2) {
                            CltLoadingRestaurantCard(
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
                if (!state.isRestaurantLoading && state.favRestaurants.isEmpty()) EmptyRestaurants()
                if (!state.isRestaurantLoading) repeat(state.favRestaurants.chunked(2).size) { chunk ->
                    val row = state.favRestaurants.chunked(2)[chunk]
                    Row {
                        repeat(row.size) {
                            CltRestaurantCard(
                                modifier = Modifier.weight(1f),
                                restaurant = row[it],
                                toggleFavorite = { restaurantId ->
                                    userProfileViewModel.toggleFavorite(restaurantId = restaurantId)
                                },
                                onClick = { restaurantId ->
                                    navController.navigateToRestaurantDetails(
                                        restaurantId = restaurantId
                                    )
                                }
                            )
                        }
                        if (row.size == 1) Box(modifier = Modifier.weight(1f))
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                CltHeading(text = "Reviews")
                Spacer(modifier = Modifier.height(5.dp))
                if (!state.isCommentLoading && state.comments.isEmpty()) EmptyReviews()
                if (!state.isCommentLoading) repeat(state.comments.size) {
                    state.loggedInUserId?.let { it1 ->
                        Column {
                            CltCommentCard(
                                modifier = Modifier.padding(horizontal = 5.dp),
                                navController = navController,
                                comment = state.comments[it],
                                currentUserId = it1,
                                topBar = TopBar.Restaurant,
                                editComment = {},
                                deleteComment = {}
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UserStats(state: UserProfileState) {
    Surface(
        elevation = 4.dp,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp)
    ) {
        if (!state.isUserLoading && !state.isRestaurantLoading && !state.isCommentLoading) Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp, horizontal = 20.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CltHeading(text = state.comments.size.toString())
                    Text(text = "Reviews")
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CltHeading(
                        text = buildAnnotatedString {
                            if (state.comments.isEmpty()) return@buildAnnotatedString append("0.0")
                            val totalRating = state.comments.sumOf {
                                it.rating.toDouble()
                            }
                            val size = state.comments.size.toDouble()
                            val average = totalRating / size
                            append(average.toStringAsFixed(1))
                        }
                    )
                    Text(text = "AVG Rating")
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CltHeading(text = state.favRestaurants.size.toString())
                    Text(text = "Favorites")
                }
            }
        }
        if (state.isRestaurantLoading || state.isCommentLoading || state.isUserLoading) Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp, horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            repeat(3) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CltShimmer(
                        modifier = Modifier
                            .width(30.dp)
                            .height(30.dp)
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    CltShimmer(
                        modifier = Modifier
                            .width(60.dp)
                            .height(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyReviews() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        repeat(2) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .offset(
                        x = if (it == 0) (-25).dp else 25.dp,
                        y = if (it == 0) 10.dp else (-10).dp
                    ),
                shape = RoundedCornerShape(10.dp),
                elevation = if (isSystemInDarkTheme()) {
                    if (it == 0) 4.dp else 20.dp
                } else {
                    if (it == 0) 10.dp else 12.dp
                },
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(
                                color = MaterialTheme.colors.onBackground.copy(
                                    alpha = 0.3f
                                )
                            )
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        repeat(2) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(if (it == 0) 1f else 0.7f)
                                    .height(15.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        color = MaterialTheme.colors.onBackground.copy(
                                            alpha = 0.2f
                                        )
                                    )
                            )
                            if (it == 0) Spacer(modifier = Modifier.height(5.dp))
                        }
                    }
                }
            }
        }
        Text(text = "This user has no reviews yet...\n", fontSize = 24.sp)
    }
}

@Composable
private fun EmptyRestaurants() {
    val config = LocalConfiguration.current
    val width = remember {
        ((config.screenWidthDp - 30) / 2).dp
    }

    Column(
        modifier = Modifier.width(config.screenWidthDp.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(30.dp))
        Row {
            repeat(2) {
                CltLoadingRestaurantCard(
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
            text = "No favorite restaurants yet...",
            fontSize = 24.sp
        )
        Spacer(modifier = Modifier.height(10.dp))
    }
}