package com.nasportfolio.restaurant.create.branch

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import com.nasportfolio.common.navigation.navigateToHomeScreen
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@Composable
fun CreateBranchScreen(
    navController: NavHostController,
    createBranchViewModel: CreateBranchViewModel = hiltViewModel()
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val state by createBranchViewModel.state.collectAsState()
    val scaffoldState = rememberScaffoldState()

    LaunchedEffect(true) {
        lifecycleOwner.lifecycleScope.launch {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                createBranchViewModel.errorChannel.collect {
                    scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
                    scaffoldState.snackbarHostState.showSnackbar(it, "Okay")
                }
            }
        }
    }

    LaunchedEffect(state.isError, state.isCreated) {
        if (!state.isError && !state.isCreated) return@LaunchedEffect
        navController.navigateToHomeScreen(
            popUpTo = "$createBranchViewModel/{restaurantId}"
        )
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text(text = "Add branch to restaurant") },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.navigateToHomeScreen(
                                popUpTo = "$createBranchViewModel/{restaurantId}"
                            )
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

    }
}