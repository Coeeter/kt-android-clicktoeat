package com.nasportfolio.restaurant.details.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.nasportfolio.common.components.CltImageFromNetwork
import com.nasportfolio.common.components.CltShimmer
import com.nasportfolio.common.navigation.homeScreenRoute
import com.nasportfolio.common.navigation.navigateToHomeScreen
import com.nasportfolio.common.theme.mediumOrange
import com.nasportfolio.restaurant.details.RestaurantsDetailState

@Composable
fun ParallaxToolbar(
    modifier: Modifier = Modifier,
    isScrollEnabled: Boolean = true,
    state: RestaurantsDetailState,
    navController: NavHostController,
    toggleFavorite: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
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
                .verticalScroll(scrollState, enabled = isScrollEnabled)
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
                (percent * (-64).dp.toPx()).toDp()
            }
        }
    }

    val imageAlpha by remember {
        derivedStateOf {
            var percent = transformedHeight / height
            if (percent < 0.2) percent = 0f
            percent
        }
    }

    val color by remember {
        derivedStateOf {
            var percent = 1 - transformedHeight / height
            if (percent == Float.NEGATIVE_INFINITY) return@derivedStateOf Color.Black
            if (percent > 0.85) percent = 1f
            Color.Black.copy(green = percent, red = percent, blue = percent)
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
        backgroundColor = if (isSystemInDarkTheme()) {
            MaterialTheme.colors.background
        } else {
            state.restaurant?.let { mediumOrange } ?: Color.White
        }
    ) {
        Box {
            state.restaurant?.let {
                CltImageFromNetwork(
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(imageAlpha)
                        .height(transformedHeight),
                    url = it.imageUrl,
                    placeholder = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    if (isSystemInDarkTheme()) {
                                        Color.Transparent
                                    } else {
                                        Color.White
                                    }
                                )
                        ) {
                            CltShimmer()
                        }
                    },
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
                    IconButton(
                        onClick = {
                            if (!state.isUpdated) return@IconButton run {
                                navController.popBackStack()
                            }
                            navController.navigateToHomeScreen(
                                popUpTo = homeScreenRoute
                            )
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null,
                            tint = color
                        )
                    }
                    Spacer(modifier = Modifier.width(30.dp))
                    state.restaurant?.let {
                        Text(
                            text = it.name,
                            modifier = Modifier.offset(
                                y = offsetY,
                                x = offsetX,
                            ),
                            style = MaterialTheme.typography.h6.copy(
                                color = color
                            )
                        )
                    } ?: CltShimmer(
                        modifier = Modifier
                            .width(150.dp)
                            .height(24.dp)
                            .offset(
                                y = height - 56.dp,
                                x = (-64).dp
                            )
                    )
                }
                IconButton(
                    onClick = {
                        state.restaurant ?: return@IconButton
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
                            tint = color,
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