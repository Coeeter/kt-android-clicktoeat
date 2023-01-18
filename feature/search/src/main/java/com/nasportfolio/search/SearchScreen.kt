package com.nasportfolio.search

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.nasportfolio.common.components.typography.CltHeading
import com.nasportfolio.common.navigation.navigateToRestaurantDetails
import com.nasportfolio.search.components.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchScreen(
    navController: NavHostController,
    searchScreenViewModel: SearchScreenViewModel = hiltViewModel()
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val focusManager = LocalFocusManager.current
    val scaffoldState = rememberScaffoldState()
    val state by searchScreenViewModel.state.collectAsState()

    LaunchedEffect(true) {
        lifecycleOwner.lifecycleScope.launch {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                searchScreenViewModel.errorChannel.collect {
                    scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
                    scaffoldState.snackbarHostState.showSnackbar(it, "Okay")
                }
            }
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                contentPadding = PaddingValues()
            ) {
                SearchBar(
                    state = state,
                    searchScreenViewModel = searchScreenViewModel,
                    focusManager = focusManager
                )
            }
        }
    ) {
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = state.isRefreshing),
            onRefresh = searchScreenViewModel::refresh
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 5.dp, horizontal = 10.dp)
            ) {
                item {
                    CltHeading(text = "Restaurants")
                }
                if (state.isRestaurantLoading) items(5) {
                    RestaurantLoadingCard()
                }
                if (!state.isRestaurantLoading && state.filteredRestaurants.isEmpty()) item {
                    EmptyResult(field = "restaurants")
                }
                if (!state.isRestaurantLoading) items(
                    items = state.filteredRestaurants,
                    key = { it.id }
                ) {
                    SearchRestaurantCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                            .animateItemPlacement(),
                        restaurant = it,
                        state = state,
                        onFavBtnClicked = {
                            searchScreenViewModel.toggleFavorite(it.id)
                        },
                        onClick = {
                            navController.navigateToRestaurantDetails(
                                restaurantId = it.id
                            )
                        }
                    )
                }
                item {
                    CltHeading(
                        text = "Users",
                        modifier = Modifier.padding(top = 10.dp)
                    )
                }
                if (state.isUserLoading) items(5) {
                    UserLoadingCard()
                }
                if (!state.isUserLoading && state.filteredUsers.isEmpty()) item {
                    EmptyResult(field = "User")
                }
                if (!state.isUserLoading) items(state.filteredUsers) {
                    SearchUserCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                            .animateItemPlacement(),
                        user = it,
                        state = state,
                        onClick = {}
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyResult(field: String) {
    Surface(shape = RoundedCornerShape(10.dp), elevation = 4.dp) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Wow such empty...", style = MaterialTheme.typography.h6)
            Text(text = "No $field found by search")
        }
    }
}