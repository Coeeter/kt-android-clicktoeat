package com.nasportfolio.user.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.nasportfolio.common.components.CltCommentCard
import com.nasportfolio.common.components.CltRestaurantCard
import com.nasportfolio.common.components.TopBar
import com.nasportfolio.common.components.effects.CltLaunchFlowCollector
import com.nasportfolio.common.components.form.rememberImagePicker
import com.nasportfolio.common.components.loading.CltLoadingRestaurantCard
import com.nasportfolio.common.components.navigation.BottomAppBarRefreshListener
import com.nasportfolio.common.components.typography.CltHeading
import com.nasportfolio.common.navigation.navigateToRestaurantDetails
import com.nasportfolio.user.profile.components.EmptyRestaurants
import com.nasportfolio.user.profile.components.EmptyReviews
import com.nasportfolio.user.profile.components.ToolbarWithContent
import com.nasportfolio.user.profile.components.UserStats

@OptIn(ExperimentalMotionApi::class)
@Composable
fun UserProfileScreen(
    navController: NavHostController,
    userProfileViewModel: UserProfileViewModel = hiltViewModel()
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val activity = LocalContext.current as BottomAppBarRefreshListener
    val scaffoldState = rememberScaffoldState()
    val state by userProfileViewModel.state.collectAsState()

    val pickImage = rememberImagePicker {
        userProfileViewModel.editPhoto(bitmap = it)
    }

    CltLaunchFlowCollector(
        lifecycleOwner = lifecycleOwner,
        flow = userProfileViewModel.errorChannel
    ) {
        scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
        scaffoldState.snackbarHostState.showSnackbar(it, "Okay")
    }

    CltLaunchFlowCollector(
        lifecycleOwner = lifecycleOwner,
        flow = userProfileViewModel.photoUpdatedChannel
    ) {
        activity.refresh()
    }

    Scaffold(scaffoldState = scaffoldState) {
        ToolbarWithContent(
            state = state,
            navController = navController,
            isRefreshing = state.isRefreshing,
            refresh = userProfileViewModel::refresh,
            editPhoto = { pickImage.launch("image/*") },
            deletePhoto = userProfileViewModel::deletePhoto,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp),
            ) {
                Spacer(modifier = Modifier.height(10.dp))
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