package com.nasportfolio.clicktoeat.screens.home

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import com.nasportfolio.auth.navigation.navigateToAuthScreen
import com.nasportfolio.common.components.CltButton
import com.nasportfolio.common.navigation.homeScreenRoute
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val state by homeViewModel.state.collectAsState()
    val scaffoldState = rememberScaffoldState()

    LaunchedEffect(true) {
        lifecycleOwner.lifecycleScope.launch {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.errorChannel.collect {
                    scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
                    scaffoldState.snackbarHostState.showSnackbar(it, "Okay")
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        scaffoldState = scaffoldState
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            AnimatedContent(
                targetState = state.isLoading,
                transitionSpec = {
                    fadeIn() with fadeOut()
                }
            ) { isLoading ->
                if (isLoading)
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                if (!isLoading)
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(state.restaurantList) { restaurant ->
                            Column(modifier = Modifier.clickable { }) {
                                Text(
                                    text = restaurant.name,
                                    modifier = Modifier.padding(5.dp)
                                )
                                Divider(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(2.dp)
                                )
                            }
                        }
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                CltButton(
                                    modifier = Modifier.fillMaxWidth(0.5f),
                                    text = "Log Out",
                                    withLoading = true,
                                    enabled = true,
                                    onClick = {
                                        homeViewModel.logout()
                                        navController.navigateToAuthScreen(
                                            popUpTo = homeScreenRoute
                                        )
                                    }
                                )
                            }
                        }
                    }
            }
        }
    }
}