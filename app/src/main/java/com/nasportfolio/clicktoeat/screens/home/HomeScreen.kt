package com.nasportfolio.clicktoeat.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.nasportfolio.auth.navigation.navigateToAuthScreen
import com.nasportfolio.common.components.CltButton
import com.nasportfolio.common.navigation.homeScreenRoute

@Composable
fun HomeScreen(
    navController: NavHostController,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
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