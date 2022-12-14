package com.nasportfolio.clicktoeat.screens.auth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import com.nasportfolio.clicktoeat.utils.Screen
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel = hiltViewModel(),
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(true) {
        lifecycleOwner.lifecycleScope.launch {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                authViewModel.isLoggedIn.collect {
                    if (!it) return@collect
                    navController.navigate(Screen.HomeScreen.route)
                }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Testing test")
    }
}