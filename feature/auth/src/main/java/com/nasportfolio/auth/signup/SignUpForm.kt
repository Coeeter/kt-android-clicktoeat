package com.nasportfolio.auth.signup

import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import com.nasportfolio.common.utils.Screen
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@Composable
fun SignUpForm(
    modifier: Modifier = Modifier,
    scaffoldState: ScaffoldState,
    navController: NavHostController,
    changePage: () -> Unit,
    signUpViewModel: SignUpViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val state by signUpViewModel.signUpState.collectAsState()

    LaunchedEffect(true) {
        lifecycleOwner.lifecycleScope.launch {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                signUpViewModel.errorChannel.collect {
                    scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
                    scaffoldState.snackbarHostState.showSnackbar(it, "Okay")
                }
            }
        }
    }

    LaunchedEffect(state.isCreated) {
        if (!state.isCreated) return@LaunchedEffect
        navController.navigate(Screen.HomeScreen.route)
    }
}