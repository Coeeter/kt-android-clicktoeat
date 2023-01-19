package com.nasportfolio.user

import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import com.nasportfolio.user.components.ToolbarWithContent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMotionApi::class)
@Composable
fun UserProfileScreen(
    navController: NavHostController,
    userProfileViewModel: UserProfileViewModel = hiltViewModel()
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val scaffoldState = rememberScaffoldState()
    val state by userProfileViewModel.state.collectAsState()

    LaunchedEffect(true) {
        lifecycleOwner.lifecycleScope.launch {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                userProfileViewModel.errorChannel.collect {
                    scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
                    scaffoldState.snackbarHostState.showSnackbar(it, "Okay")
                }
            }
        }
    }

    Scaffold(scaffoldState = scaffoldState) {
        ToolbarWithContent(
            state = state,
            navController = navController
        ) {
            repeat(100) {
                Text(text = "Chicken")
            }
        }
    }
}