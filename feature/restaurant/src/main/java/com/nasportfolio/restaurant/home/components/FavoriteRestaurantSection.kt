package com.nasportfolio.restaurant.home.components

import android.content.res.Configuration
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.nasportfolio.common.components.CltRestaurantCard
import com.nasportfolio.common.components.loading.CltLoadingRestaurantCard
import com.nasportfolio.common.navigation.navigateToRestaurantDetails
import com.nasportfolio.restaurant.home.HomeState
import com.nasportfolio.restaurant.home.HomeViewModel
import com.nasportfolio.test.tags.TestTags

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun FavoriteRestaurantSection(
    state: HomeState,
    width: Dp,
    homeViewModel: HomeViewModel,
    navController: NavHostController,
    config: Configuration
) {
    AnimatedContent(
        targetState = state.isLoading,
        transitionSpec = {
            fadeIn(
                animationSpec = tween(durationMillis = 350)
            ) with fadeOut(
                animationSpec = tween(durationMillis = 350)
            )
        }
    ) { isLoading ->
        if (isLoading) Row(
            modifier = Modifier.horizontalScroll(
                state = rememberScrollState(),
                enabled = false
            )
        ) {
            repeat(3) {
                CltLoadingRestaurantCard(
                    modifier = Modifier.width(width)
                )
            }
        }
        if (!isLoading) Box {
            LazyRow {
                items(state.favRestaurants.size) {
                    val index = state.favRestaurants[it]

                    CltRestaurantCard(
                        modifier = Modifier
                            .width(width)
                            .testTag(TestTags.FAVORITE_RESTAURANT_TAG),
                        restaurant = state.restaurantList[index],
                        toggleFavorite = { restaurantId ->
                            homeViewModel.toggleFavorite(restaurantId)
                        },
                        onClick = { restaurantId ->
                            navController.navigateToRestaurantDetails(
                                restaurantId = restaurantId
                            )
                        }
                    )
                }
            }
            if (state.favRestaurants.isEmpty()) {
                val onBackground = MaterialTheme.colors.onBackground
                val systemInDarkTheme = isSystemInDarkTheme()

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
                                elevation = if (systemInDarkTheme) {
                                    if (it == 0) 4.dp else 20.dp
                                } else {
                                    if (it == 0) 10.dp else 12.dp
                                }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(30.dp))
                    Text(
                        text = buildAnnotatedString {
                            withStyle(ParagraphStyle(textAlign = TextAlign.Center)) {
                                withStyle(SpanStyle(fontSize = 24.sp)) {
                                    append("No favorite restaurants yet...\n")
                                }
                                withStyle(
                                    SpanStyle(
                                        color = onBackground.copy(
                                            alpha = if (systemInDarkTheme) 0.5f else 0.7f
                                        )
                                    )
                                ) {
                                    append("Try adding one now!")
                                }
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}