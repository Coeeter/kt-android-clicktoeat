package com.nasportfolio.restaurant.likedislike

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.nasportfolio.common.components.effects.CltLaunchFlowCollector
import com.nasportfolio.common.components.images.CltImageFromNetwork
import com.nasportfolio.common.components.loading.CltShimmer
import com.nasportfolio.common.navigation.navigateToUserProfile
import com.nasportfolio.common.theme.mediumOrange
import com.nasportfolio.domain.user.User
import com.nasportfolio.restaurant.likedislike.components.EmptyItems
import kotlinx.coroutines.launch

data class TabItem(
    val title: String,
    private val screen: @Composable TabItem.() -> Unit
) {
    @Composable
    fun Content() {
        screen(this)
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun LikeDislikeScreen(
    navController: NavHostController,
    likeDislikeViewModel: LikeDislikeViewModel = hiltViewModel()
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val state by likeDislikeViewModel.state.collectAsState()

    val scaffoldState = rememberScaffoldState()
    val pagerState = rememberPagerState(initialPage = state.initialIndex)
    val coroutineScope = rememberCoroutineScope()

    val tabItems = listOf(
        TabItem(
            title = "Likes",
            screen = {
                UserList(
                    users = state.comment?.likes ?: emptyList(),
                    isLoading = state.isLoading,
                    tabItem = this,
                    navController = navController
                )
            }
        ),
        TabItem(
            title = "Dislikes",
            screen = {
                UserList(
                    users = state.comment?.dislikes ?: emptyList(),
                    isLoading = state.isLoading,
                    tabItem = this,
                    navController = navController
                )
            }
        )
    )

    CltLaunchFlowCollector(
        lifecycleOwner = lifecycleOwner,
        flow = likeDislikeViewModel.errorChannel
    ) {
        scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
        scaffoldState.snackbarHostState.showSnackbar(it, "Okay")
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            Column {
                TopAppBar(
                    title = { Text(text = "Likes and Dislikes of comment") },
                    navigationIcon = {
                        IconButton(onClick = navController::popBackStack) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = null
                            )
                        }
                    }
                )
                TabRow(
                    selectedTabIndex = pagerState.currentPage,
                    indicator = {
                        TabRowDefaults.Indicator(
                            modifier = Modifier.pagerTabIndicatorOffset(pagerState, it),
                            color = Color.White
                        )
                    },
                ) {
                    tabItems.forEachIndexed { index, tabItem ->
                        Tab(
                            modifier = Modifier.background(
                                color = Color.White.copy(
                                    alpha = if (isSystemInDarkTheme()) 0.09f else 0f
                                )
                            ),
                            selectedContentColor = Color.White,
                            selected = pagerState.currentPage == index,
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            text = { Text(text = tabItem.title) }
                        )
                    }
                }
            }
        }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            HorizontalPager(
                count = 2,
                state = pagerState
            ) { page ->
                SwipeRefresh(
                    state = rememberSwipeRefreshState(isRefreshing = state.isRefreshing),
                    onRefresh = likeDislikeViewModel::refresh
                ) {
                    tabItems[page].Content()
                }
            }
        }
    }
}

@Composable
private fun UserList(
    users: List<User>,
    isLoading: Boolean,
    tabItem: TabItem,
    navController: NavHostController
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
    ) {
        if (isLoading) items(10) {
            LoadingUserCard()
        }
        if (!isLoading && users.isEmpty()) item {
            EmptyItems(tabItem)
        }
        if (!isLoading && users.isNotEmpty()) items(users) { user ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        navController.navigateToUserProfile(userId = user.id)
                    },
                elevation = 4.dp,
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .border(
                            width = 2.dp,
                            color = mediumOrange,
                            shape = CircleShape
                        )

                    user.image?.url?.let {
                        CltImageFromNetwork(
                            url = it,
                            placeholder = { CltShimmer() },
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = modifier
                        )
                    } ?: Box(
                        modifier = modifier,
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = user.username,
                        style = MaterialTheme.typography.h6
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingUserCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        elevation = 4.dp,
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CltShimmer(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .border(
                        width = 2.dp,
                        color = mediumOrange,
                        shape = CircleShape
                    ),
            )
            Spacer(modifier = Modifier.width(10.dp))
            CltShimmer(
                modifier = Modifier
                    .width(150.dp)
                    .height(25.dp),
            )
        }
    }
}