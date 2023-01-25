package com.nasportfolio.restaurant.details.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.nasportfolio.common.components.buttons.CltButton
import com.nasportfolio.common.components.form.CltInput
import com.nasportfolio.common.components.typography.CltHeading
import com.nasportfolio.common.modifier.gradientBackground
import com.nasportfolio.common.navigation.navigateToCommentsScreen
import com.nasportfolio.common.theme.lightOrange
import com.nasportfolio.common.theme.mediumOrange
import com.nasportfolio.common.utils.toStringAsFixed
import com.nasportfolio.domain.comment.Comment
import com.nasportfolio.domain.restaurant.TransformedRestaurant
import com.nasportfolio.restaurant.details.RestaurantDetailsEvent
import com.nasportfolio.restaurant.details.RestaurantDetailsViewModel
import com.nasportfolio.test.tags.TestTags
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReviewMetaDataSection(
    navController: NavHostController,
    restaurant: TransformedRestaurant,
    restaurantDetailsViewModel: RestaurantDetailsViewModel
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            CltHeading(
                text = "Reviews",
                textAlign = TextAlign.Start,
                fontSize = 30.sp
            )
            if (restaurant.comments.isNotEmpty()) TextButton(
                onClick = {
                    navController.navigateToCommentsScreen(
                        restaurantId = restaurant.id
                    )
                }
            ) {
                Text(text = "See all")
            }
        }
        CreateReviewForm(restaurantDetailsViewModel = restaurantDetailsViewModel)
        Spacer(modifier = Modifier.height(10.dp))
        if (restaurant.comments.isNotEmpty()) Reviews(restaurant)
        if (restaurant.comments.isEmpty()) EmptyReviews()
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CreateReviewForm(
    restaurantDetailsViewModel: RestaurantDetailsViewModel
) {
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val bringIntoViewRequester = remember {
        BringIntoViewRequester()
    }

    val state by restaurantDetailsViewModel.state.collectAsState()

    Column {
        CltInput(
            modifier = Modifier.onFocusEvent {
                if (!it.isFocused) return@onFocusEvent
                scope.launch {
                    bringIntoViewRequester.bringIntoView()
                }
            },
            testTag = TestTags.REVIEW_INPUT,
            value = state.review,
            label = "Review",
            error = state.reviewError,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            ),
            onValueChange = {
                restaurantDetailsViewModel.onEvent(
                    RestaurantDetailsEvent.OnReviewChangedEvent(
                        review = it
                    )
                )
            }
        )
        Spacer(modifier = Modifier.height(5.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .bringIntoViewRequester(bringIntoViewRequester),
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
                            .size(35.dp)
                            .testTag(TestTags.STAR),
                        contentPadding = PaddingValues(5.dp),
                        onClick = {
                            focusManager.clearFocus()
                            restaurantDetailsViewModel.onEvent(
                                RestaurantDetailsEvent.OnRatingChangedEvent(
                                    rating = it + 1
                                )
                            )
                        }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (it < state.rating) {
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
                modifier = Modifier.testTag(TestTags.CREATE_REVIEW_BTN),
                text = "Submit",
                withLoading = true,
                enabled = !state.isSubmitting,
                onClick = {
                    focusManager.clearFocus()
                    restaurantDetailsViewModel.onEvent(
                        RestaurantDetailsEvent.OnSubmit
                    )
                }
            )
        }
        AnimatedVisibility(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp),
            visible = state.ratingError != null,
            enter = fadeIn() + slideInHorizontally(animationSpec = spring()),
        ) {
            state.ratingError?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colors.error,
                    style = MaterialTheme.typography.body1,
                )
            }
        }
    }
}

@Composable
private fun Reviews(restaurant: TransformedRestaurant) {
    Column {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            elevation = 4.dp,
            shape = RoundedCornerShape(10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, bottom = 10.dp, top = 5.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = buildAnnotatedString(restaurant = restaurant))
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.End
                ) {
                    Spacer(modifier = Modifier.height(10.dp))
                    repeat(5) {
                        val animatedWidth by animateFloatAsState(
                            targetValue = calculatePercentOfUsers(
                                comments = restaurant.comments,
                                rating = 5 - it
                            ),
                            animationSpec = tween(
                                durationMillis = 500,
                                easing = FastOutSlowInEasing,
                            )
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row {
                                repeat(5 - it) {
                                    Icon(
                                        modifier = Modifier.size(10.dp),
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(5.dp))
                            LinearProgressIndicator(
                                progress = animatedWidth,
                                modifier = Modifier
                                    .width(150.dp)
                                    .clip(CircleShape),
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                    }
                    Text(
                        text = "${restaurant.ratingCount} reviews",
                        color = MaterialTheme.colors.onBackground.copy(
                            alpha = if (isSystemInDarkTheme()) 0.5f else 0.7f
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyReviews() {
    val onBackground = MaterialTheme.colors.onBackground
    val systemInDarkTheme = isSystemInDarkTheme()

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
                elevation = if (systemInDarkTheme) {
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
                                color = onBackground.copy(
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
                                        color = onBackground.copy(
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
        Text(text = buildAnnotatedString {
            withStyle(ParagraphStyle(textAlign = TextAlign.Center)) {
                withStyle(SpanStyle(fontSize = 24.sp)) {
                    append("No reviews yet...\n")
                }
                withStyle(
                    SpanStyle(
                        color = onBackground.copy(
                            alpha = if (systemInDarkTheme) {
                                0.5f
                            } else {
                                0.7f
                            }
                        )
                    )
                ) {
                    append("Try adding one now!")
                }
            }
        })
    }
}

private fun calculatePercentOfUsers(comments: List<Comment>, rating: Int): Float {
    val groupedComments = comments.groupBy { it.rating }
    val countOfUsersWithRating = groupedComments[rating]?.size?.toFloat() ?: 0f
    return countOfUsersWithRating / comments.size
}

@Composable
private fun buildAnnotatedString(restaurant: TransformedRestaurant): AnnotatedString {
    val onBackground = MaterialTheme.colors.onBackground
    val systemInDarkTheme = isSystemInDarkTheme()

    return buildAnnotatedString {
        withStyle(
            ParagraphStyle(
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
            )
        ) {
            withStyle(
                SpanStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 54.sp
                )
            ) {
                append(restaurant.averageRating.toStringAsFixed(1))
                append("\n")
            }
            withStyle(
                SpanStyle(
                    fontSize = 18.sp,
                    color = onBackground.copy(
                        alpha = if (systemInDarkTheme) {
                            0.5f
                        } else {
                            0.7f
                        }
                    )
                )
            ) {
                append("out of 5")
            }
        }
    }
}