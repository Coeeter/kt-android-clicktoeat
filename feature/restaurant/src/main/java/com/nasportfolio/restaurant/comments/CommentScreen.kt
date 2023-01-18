package com.nasportfolio.restaurant.comments

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.nasportfolio.common.components.buttons.CltButton
import com.nasportfolio.common.components.form.CltInput
import com.nasportfolio.common.modifier.gradientBackground
import com.nasportfolio.common.navigation.navigateToRestaurantDetails
import com.nasportfolio.common.navigation.restaurantDetailScreenRoute
import com.nasportfolio.common.theme.lightOrange
import com.nasportfolio.common.theme.mediumOrange
import com.nasportfolio.common.utils.toStringAsFixed
import com.nasportfolio.domain.comment.Comment
import com.nasportfolio.restaurant.details.components.CommentCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CommentScreen(
    navController: NavHostController,
    commentViewModel: CommentViewModel = hiltViewModel()
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val scaffoldState = rememberScaffoldState()
    val state by commentViewModel.state.collectAsState()

    LaunchedEffect(true) {
        lifecycleOwner.lifecycleScope.launch {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                commentViewModel.errorChannel.collect {
                    scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
                    scaffoldState.snackbarHostState.showSnackbar(it, "Okay")
                }
            }
        }
    }

    BackHandler(enabled = true) {
        if (state.isUpdated) return@BackHandler run {
            navController.navigateToRestaurantDetails(
                restaurantId = state.restaurantId!!,
                popUpTo = "$restaurantDetailScreenRoute/{restaurantId}"
            )
        }
        navController.popBackStack()
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text(text = "Comments of restaurant") },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (state.isUpdated) return@IconButton run {
                                navController.navigateToRestaurantDetails(
                                    restaurantId = state.restaurantId!!,
                                    popUpTo = "$restaurantDetailScreenRoute/{restaurantId}"
                                )
                            }
                            navController.popBackStack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) {
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = state.isRefreshing),
            onRefresh = {
                commentViewModel.onEvent(
                    event = CommentsScreenEvent.RefreshPage
                )
            }
        ) {
            if (!state.isLoading)
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        Box(
                            modifier = Modifier.padding(
                                end = 16.dp,
                                start = 16.dp,
                                top = 10.dp
                            )
                        ) {
                            CreateReviewForm(commentViewModel = commentViewModel)
                        }
                    }
                    item {
                        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                            if (state.comments.isNotEmpty()) Reviews(comments = state.comments)
                            if (state.comments.isEmpty()) EmptyReviews()
                        }
                    }
                    items(
                        count = state.comments.size,
                        key = { state.comments[it].id },
                    ) { index ->
                        state.currentUserId?.let { userId ->
                            if (index == 0) Spacer(modifier = Modifier.height(10.dp))
                            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                                CommentCard(
                                    modifier = Modifier.fillMaxWidth(),
                                    comment = state.comments[index],
                                    navController = navController,
                                    currentUserId = userId,
                                    editComment = {
                                        commentViewModel.onEvent(
                                            event = CommentsScreenEvent.OpenEditCommentDialog(
                                                index = index
                                            )
                                        )
                                    },
                                    deleteComment = {
                                        commentViewModel.onEvent(
                                            event = CommentsScreenEvent.OnDeleteComment(
                                                index = index
                                            )
                                        )
                                    }
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                        }
                    }
                }
            if (state.isLoading) Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }

    if (state.commentBeingEdited != null) EditCommentDialog(
        state = state,
        commentViewModel = commentViewModel
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CreateReviewForm(commentViewModel: CommentViewModel) {
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val bringIntoViewRequester = remember {
        BringIntoViewRequester()
    }

    val state by commentViewModel.state.collectAsState()

    Column {
        CltInput(
            modifier = Modifier.onFocusEvent {
                if (!it.isFocused) return@onFocusEvent
                scope.launch {
                    bringIntoViewRequester.bringIntoView()
                }
            },
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
                commentViewModel.onEvent(
                    CommentsScreenEvent.OnReviewChangedEvent(
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
                            .size(35.dp),
                        contentPadding = PaddingValues(5.dp),
                        onClick = {
                            focusManager.clearFocus()
                            commentViewModel.onEvent(
                                CommentsScreenEvent.OnRatingChangedEvent(
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
                text = "Submit",
                withLoading = true,
                enabled = !state.isCreating,
                onClick = {
                    focusManager.clearFocus()
                    commentViewModel.onEvent(
                        CommentsScreenEvent.OnCreate
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
        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
private fun Reviews(comments: List<Comment>) {
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
                Text(text = buildAnnotatedString(comments = comments))
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.End
                ) {
                    Spacer(modifier = Modifier.height(10.dp))
                    repeat(5) {
                        val animatedWidth by animateFloatAsState(
                            targetValue = calculatePercentOfUsers(
                                comments = comments,
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
                        text = "${comments.size} reviews",
                        color = MaterialTheme.colors.onBackground.copy(
                            alpha = if (isSystemInDarkTheme()) 0.5f else 0.7f
                        )
                    )
                }
            }
        }
    }
}

private fun calculatePercentOfUsers(comments: List<Comment>, rating: Int): Float {
    val groupedComments = comments.groupBy { it.rating }
    val countOfUsersWithRating = groupedComments[rating]?.size?.toFloat() ?: 0f
    return countOfUsersWithRating / comments.size
}

@Composable
private fun buildAnnotatedString(comments: List<Comment>) =
    buildAnnotatedString {
        val averageRating = comments.sumOf { it.rating } / comments.size.toDouble()

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
                append(averageRating.toStringAsFixed(1))
                append("\n")
            }
            withStyle(
                SpanStyle(
                    fontSize = 18.sp,
                    color = MaterialTheme.colors.onBackground.copy(
                        alpha = if (isSystemInDarkTheme()) {
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
        Text(text = buildAnnotatedString {
            withStyle(ParagraphStyle(textAlign = TextAlign.Center)) {
                withStyle(SpanStyle(fontSize = 24.sp)) {
                    append("No reviews yet...\n")
                }
                withStyle(
                    SpanStyle(
                        color = MaterialTheme.colors.onBackground.copy(
                            alpha = if (isSystemInDarkTheme()) {
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


@Composable
private fun EditCommentDialog(
    state: CommentScreenState,
    commentViewModel: CommentViewModel,
) {
    val config = LocalConfiguration.current

    Dialog(
        properties = DialogProperties(
            dismissOnBackPress = !state.isEditSubmitting,
            dismissOnClickOutside = !state.isEditSubmitting
        ),
        onDismissRequest = {
            commentViewModel.onEvent(
                CommentsScreenEvent.OnCloseEditCommentDialog
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
                    value = state.editingReview,
                    label = "Review",
                    error = state.editingReviewError,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    ),
                    onValueChange = {
                        commentViewModel.onEvent(
                            CommentsScreenEvent.OnEditReview(
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
                                    commentViewModel.onEvent(
                                        CommentsScreenEvent.OnEditRating(
                                            rating = it + 1
                                        )
                                    )
                                }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = if (it < state.editingRating) {
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
                            commentViewModel.onEvent(
                                CommentsScreenEvent.OnCompleteEdit
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