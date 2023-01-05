package com.nasportfolio.restaurant.details

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import com.nasportfolio.common.components.CltImageFromNetwork
import com.nasportfolio.common.components.CltShimmer
import com.nasportfolio.common.navigation.navigateToHomeScreen
import com.nasportfolio.common.navigation.restaurantDetailScreenRoute
import com.nasportfolio.common.theme.mediumOrange
import kotlinx.coroutines.launch

private const val popUpToRoute = "$restaurantDetailScreenRoute/{restaurantId}"

@Composable
fun RestaurantDetailsScreen(
    navController: NavHostController,
    restaurantDetailsViewModel: RestaurantDetailsViewModel = hiltViewModel()
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val scaffoldState = rememberScaffoldState()
    val state by restaurantDetailsViewModel.state.collectAsState()

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
            popUpTo = popUpToRoute
        )
    }

    Scaffold(
        scaffoldState = scaffoldState
    ) {
        ParallaxToolbar(
            state = state,
            navController = navController,
            toggleFavorite = {
                restaurantDetailsViewModel.toggleFavorite()
            }
        ) {
            repeat(100) {
                state.restaurant?.let {
                    Text(text = it.description)
                }
            }
        }
    }
}

@Composable
private fun ParallaxToolbar(
    modifier: Modifier = Modifier,
    state: RestaurantsDetailState,
    navController: NavHostController,
    toggleFavorite: () -> Unit,
    content: @Composable () -> Unit
) {
    val scrollState = rememberScrollState()

    Box(modifier = modifier.fillMaxSize()) {
        AppBar(
            modifier = Modifier.zIndex(10f),
            state = state,
            scrollState = scrollState,
            navController = navController,
            toggleFavorite = toggleFavorite
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            )
            content()
        }
    }
}

@Composable
private fun AppBar(
    modifier: Modifier = Modifier,
    state: RestaurantsDetailState,
    scrollState: ScrollState,
    navController: NavHostController,
    toggleFavorite: () -> Unit
) {
    val density = LocalDensity.current

    var height by remember {
        mutableStateOf(0.dp)
    }

    val transformedHeight by remember {
        derivedStateOf {
            max(56.dp, height - with(density) {
                scrollState.value.toDp()
            })
        }
    }

    val offsetY by remember {
        derivedStateOf {
            max(0.dp, transformedHeight - 56.dp)
        }
    }

    val offsetX by remember {
        derivedStateOf {
            val percent = transformedHeight / height
            with(density) {
                (percent * (-50).dp.toPx()).toDp()
            }
        }
    }

    val imageAlpha by remember {
        derivedStateOf {
            if (scrollState.value >= with(density) { height.toPx() } - 56)
                return@derivedStateOf 0f
            transformedHeight / height
        }
    }

    TopAppBar(
        modifier = modifier
            .fillMaxWidth()
            .height(transformedHeight)
            .onGloballyPositioned {
                height = with(density) {
                    it.size.width.toDp()
                }
            },
        elevation = if (scrollState.value > 0) 4.dp else 0.dp,
        contentPadding = PaddingValues(),
        backgroundColor = MaterialTheme.colors.background,
    ) {
        Box {
            state.restaurant?.let {
                CltImageFromNetwork(
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(imageAlpha)
                        .height(transformedHeight),
                    url = it.imageUrl,
                    placeholder = { CltShimmer() },
                    contentDescription = null,
                )
            } ?: CltShimmer(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AppBarIconButton(
                        onClick = {
                            if (!state.isUpdated) return@AppBarIconButton run {
                                navController.popBackStack()
                            }
                            navController.navigateToHomeScreen(
                                popUpTo = popUpToRoute
                            )
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null,
                            tint = mediumOrange
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    state.restaurant?.let {
                        Text(
                            text = it.name,
                            modifier = Modifier.offset(
                                y = offsetY,
                                x = offsetX,
                            ),
                            style = MaterialTheme.typography.h6.copy(
                                color = mediumOrange
                            )
                        )
                    } ?: CltShimmer(
                        modifier = Modifier
                            .width(150.dp)
                            .height(24.dp)
                            .offset(
                                y = height - 55.dp,
                                x = (-50).dp
                            )
                    )
                }
                AppBarIconButton(
                    onClick = {
                        state.restaurant ?: return@AppBarIconButton
                        toggleFavorite()
                    }
                ) {
                    state.restaurant?.let {
                        Icon(
                            imageVector = if (it.isFavoriteByCurrentUser) {
                                Icons.Default.Favorite
                            } else {
                                Icons.Default.FavoriteBorder
                            },
                            tint = mediumOrange,
                            contentDescription = null
                        )
                    } ?: CltShimmer(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                    )
                }
            }
        }
    }
}

@Composable
private fun AppBarIconButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    TextButton(
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}