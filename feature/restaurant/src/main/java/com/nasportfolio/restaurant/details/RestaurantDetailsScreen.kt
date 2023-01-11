package com.nasportfolio.restaurant.details

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import com.nasportfolio.common.components.CltButton
import com.nasportfolio.common.components.CltHeading
import com.nasportfolio.common.components.CltInput
import com.nasportfolio.common.navigation.homeScreenRoute
import com.nasportfolio.common.navigation.navigateToHomeScreen
import com.nasportfolio.common.theme.mediumOrange
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
                                    count = if (restaurant.comments.size <= 3) {
                                        restaurant.comments.size
                                    } else {
                                        3
                                    },
                                ) {
                                    Row {
                                        CommentCard(
                                            modifier = Modifier.width(config.screenWidthDp.dp - 42.dp),
                                            comment = restaurant.comments[it],
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
        }
    }

    if (state.commentBeingEdited != null) Dialog(
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