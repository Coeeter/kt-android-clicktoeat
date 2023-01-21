package com.nasportfolio.restaurant.details

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.nasportfolio.common.components.CltCommentCard
import com.nasportfolio.common.components.buttons.CltButton
import com.nasportfolio.common.components.buttons.CltSpeedDialFab
import com.nasportfolio.common.components.buttons.CltSpeedDialFabItem
import com.nasportfolio.common.components.buttons.rememberSpeedDialState
import com.nasportfolio.common.components.effects.CltLaunchFlowCollector
import com.nasportfolio.common.components.form.CltInput
import com.nasportfolio.common.components.loading.CltShimmer
import com.nasportfolio.common.components.typography.CltHeading
import com.nasportfolio.common.modifier.gradientBackground
import com.nasportfolio.common.navigation.homeScreenRoute
import com.nasportfolio.common.navigation.navigateToCreateBranch
import com.nasportfolio.common.navigation.navigateToHomeScreen
import com.nasportfolio.common.navigation.navigateToUpdateRestaurant
import com.nasportfolio.common.theme.lightOrange
import com.nasportfolio.common.theme.mediumOrange
import com.nasportfolio.domain.restaurant.TransformedRestaurant
import com.nasportfolio.restaurant.details.components.BranchesSection
import com.nasportfolio.restaurant.details.components.DataSection
import com.nasportfolio.restaurant.details.components.ParallaxToolbar
import com.nasportfolio.restaurant.details.components.ReviewMetaDataSection

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
    val (isScrollEnabled, setIsScrollEnabled) = remember {
        mutableStateOf(true)
    }
    var openRestaurantDeleteDialog by remember {
        mutableStateOf(false)
    }
    var openBranchDeleteDialog by remember {
        mutableStateOf(false)
    }
    var branchToBeDeletedId by remember {
        mutableStateOf<String?>(null)
    }

    CltLaunchFlowCollector(
        lifecycleOwner = lifecycleOwner,
        flow = restaurantDetailsViewModel.errorChannel
    ) {
        scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
        scaffoldState.snackbarHostState.showSnackbar(it, "Okay")
    }

    LaunchedEffect(state.isLoading) {
        setIsScrollEnabled(!state.isLoading)
    }

    LaunchedEffect(state.isDeleted) {
        if (!state.isDeleted) return@LaunchedEffect
        openRestaurantDeleteDialog = false
        navController.navigateToHomeScreen(
            popUpTo = homeScreenRoute
        )
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
        scaffoldState = scaffoldState,
        floatingActionButton = {
            SpeedDial(
                state = state,
                navController = navController,
                openDialog = {
                    openRestaurantDeleteDialog = true
                }
            )
        }
    ) {
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
                ScreenContent(
                    restaurant = restaurant,
                    state = state,
                    navController = navController,
                    restaurantDetailsViewModel = restaurantDetailsViewModel,
                    config = config,
                    setIsScrollEnabled = setIsScrollEnabled,
                    setOpenDeleteDialog = {
                        branchToBeDeletedId = it
                        openBranchDeleteDialog = true
                    }
                )
            } ?: LoadingComponent()
        }

        if (openRestaurantDeleteDialog) DeleteDialog(
            setIsOpen = { openRestaurantDeleteDialog = it },
            state = state,
            restaurantDetailsViewModel = restaurantDetailsViewModel
        )
        if (openBranchDeleteDialog) DeleteDialog(
            setIsOpen = { openBranchDeleteDialog = it },
            state = state,
            restaurantDetailsViewModel = restaurantDetailsViewModel,
            title = "this branch?",
            content = "branch",
            event = RestaurantDetailsEvent.DeleteBranch(
                branchId = branchToBeDeletedId!!
            )
        )
    }

    if (state.commentBeingEdited != null) EditCommentDialog(
        state = state,
        restaurantDetailsViewModel = restaurantDetailsViewModel,
        config = config
    )
}

@Composable
private fun DeleteDialog(
    setIsOpen: (Boolean) -> Unit,
    state: RestaurantsDetailState,
    restaurantDetailsViewModel: RestaurantDetailsViewModel,
    title: String = "restaurant ${state.restaurant?.name}?",
    content: String = "restaurant",
    event: RestaurantDetailsEvent = RestaurantDetailsEvent.DeleteRestaurant
) {
    AlertDialog(
        onDismissRequest = { setIsOpen(false) },
        title = {
            Text(text = "Are you sure you want to delete $title")
        },
        text = {
            Text(text = "This action is irreversible and will delete all data related to this $content")
        },
        buttons = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                CltButton(
                    modifier = Modifier.weight(1f),
                    text = "Cancel",
                    withLoading = false,
                    enabled = true,
                    onClick = {
                        setIsOpen(false)
                    }
                )
                Spacer(modifier = Modifier.width(10.dp))
                CltButton(
                    modifier = Modifier.weight(1f),
                    text = "Delete",
                    withLoading = true,
                    enabled = !state.isDeleting,
                    gradient = Brush.horizontalGradient(
                        colors = listOf(Color(0xFFE60000), Color(0xFFFF5E5E))
                    ),
                    onClick = {
                        setIsOpen(false)
                        restaurantDetailsViewModel.onEvent(
                            event = event
                        )
                    }
                )
            }
        }
    )
}

@Composable
private fun SpeedDial(
    state: RestaurantsDetailState,
    navController: NavHostController,
    openDialog: () -> Unit
) {
    val speedDialState = rememberSpeedDialState()

    CltSpeedDialFab(
        state = speedDialState,
        buttons = {
            CltSpeedDialFabItem(
                backgroundColor = mediumOrange,
                hint = "Add branch",
                onClick = {
                    state.restaurant?.let {
                        navController.navigateToCreateBranch(
                            restaurantId = it.id
                        )
                    }
                }
            ) {
                Icon(imageVector = Icons.Default.AddLocation, contentDescription = null)
            }
            CltSpeedDialFabItem(
                backgroundColor = mediumOrange,
                hint = "Edit restaurant",
                onClick = {
                    state.restaurant?.let {
                        navController.navigateToUpdateRestaurant(
                            restaurantId = it.id
                        )
                    }
                }
            ) {
                Icon(imageVector = Icons.Default.Restaurant, contentDescription = null)
            }
            CltSpeedDialFabItem(
                backgroundColor = Color.Red,
                hint = "Delete restaurant",
                onClick = {
                    openDialog()
                    speedDialState.isExpanded = false
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        },
        expandedChild = {
            Icon(imageVector = Icons.Default.Close, contentDescription = null)
        },
        collapsedChild = {
            Icon(imageVector = Icons.Default.Edit, contentDescription = null)
        }
    )
}

@Composable
private fun ScreenContent(
    restaurant: TransformedRestaurant,
    state: RestaurantsDetailState,
    navController: NavHostController,
    restaurantDetailsViewModel: RestaurantDetailsViewModel,
    config: Configuration,
    setIsScrollEnabled: (Boolean) -> Unit,
    setOpenDeleteDialog: (String) -> Unit
) {
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
                navController = navController,
                setIsScrollEnabled = setIsScrollEnabled,
                setIsAnimationDone = {
                    restaurantDetailsViewModel.onEvent(
                        event = RestaurantDetailsEvent.AnimationOverEvent(it)
                    )
                },
                deleteBranch = {
                    setOpenDeleteDialog(it)
                }
            )
            Spacer(modifier = Modifier.height(10.dp))
            ReviewMetaDataSection(
                restaurant = restaurant,
                restaurantDetailsViewModel = restaurantDetailsViewModel,
                navController = navController
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
                        count = if (restaurant.comments.size <= 3) {
                            restaurant.comments.size
                        } else {
                            3
                        },
                        key = {
                            restaurant.comments[it].id
                        }
                    ) {
                        Row {
                            var width = config.screenWidthDp.dp - 32.dp
                            if (restaurant.comments.size != 1) width -= 10.dp
                            CltCommentCard(
                                modifier = Modifier.width(width),
                                comment = restaurant.comments[it],
                                navController = navController,
                                currentUserId = userId,
                                editComment = {
                                    restaurantDetailsViewModel.onEvent(
                                        RestaurantDetailsEvent.OpenEditCommentDialog(
                                            index = it
                                        )
                                    )
                                },
                                deleteComment = {
                                    restaurantDetailsViewModel.onEvent(
                                        RestaurantDetailsEvent.DeleteComment(index = it)
                                    )
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

@Composable
private fun LoadingComponent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
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
        Spacer(modifier = Modifier.height(10.dp))
        CltShimmer(
            modifier = Modifier
                .width(150.dp)
                .height(40.dp)
        )
        Spacer(modifier = Modifier.height(5.dp))
        repeat(2) {
            Column {
                CltShimmer(
                    modifier = Modifier
                        .fillMaxWidth(if (it == 0) 1f else 0.6f)
                        .height(25.dp)
                )
                Spacer(modifier = Modifier.height(2.dp))
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        CltShimmer(
            modifier = Modifier
                .width(150.dp)
                .height(40.dp)
        )
        Spacer(modifier = Modifier.height(5.dp))
        CltShimmer(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .border(2.dp, mediumOrange)
        )
        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
private fun EditCommentDialog(
    state: RestaurantsDetailState,
    restaurantDetailsViewModel: RestaurantDetailsViewModel,
    config: Configuration
) {
    Dialog(
        properties = DialogProperties(
            dismissOnBackPress = !state.isEditSubmitting,
            dismissOnClickOutside = !state.isEditSubmitting
        ),
        onDismissRequest = {
            restaurantDetailsViewModel.onEvent(
                RestaurantDetailsEvent.OnCloseEditCommentDialog
            )
        }
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height((config.screenHeightDp * 0.2).dp),
            shape = RoundedCornerShape(10.dp),
            elevation = 10.dp
        ) {
            val focusManager = LocalFocusManager.current
            Column(
                modifier = Modifier.padding(10.dp),
                verticalArrangement = Arrangement.Center
            ) {
                CltInput(
                    value = state.editingReviewValue,
                    label = "Review",
                    error = state.editingReviewError,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    ),
                    onValueChange = {
                        restaurantDetailsViewModel.onEvent(
                            RestaurantDetailsEvent.OnEditReview(
                                review = it
                            )
                        )
                    }
                )
                Spacer(modifier = Modifier.height(5.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.gradientBackground(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    lightOrange,
                                    mediumOrange
                                )
                            )
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(5) {
                            TextButton(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .size(35.dp),
                                contentPadding = PaddingValues(5.dp),
                                onClick = {
                                    focusManager.clearFocus()
                                    restaurantDetailsViewModel.onEvent(
                                        RestaurantDetailsEvent.OnEditRating(
                                            rating = it + 1
                                        )
                                    )
                                }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = if (it < state.editingRatingValue) {
                                            Icons.Default.Star
                                        } else {
                                            Icons.Default.StarBorder
                                        },
                                        contentDescription = null,
                                        tint = mediumOrange
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    CltButton(
                        text = "Submit",
                        withLoading = true,
                        enabled = !state.isEditSubmitting,
                        onClick = {
                            focusManager.clearFocus()
                            restaurantDetailsViewModel.onEvent(
                                RestaurantDetailsEvent.OnCompleteEdit
                            )
                        }
                    )
                    AnimatedVisibility(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp),
                        visible = state.editingRatingError != null,
                        enter = fadeIn() + slideInHorizontally(animationSpec = spring()),
                    ) {
                        state.editingRatingError?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colors.error,
                                style = MaterialTheme.typography.body1,
                            )
                        }
                    }
                }
            }
        }
    }
}