package com.nasportfolio.clicktoeat.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.nasportfolio.auth.navigation.navigateToAuthScreen
import com.nasportfolio.common.utils.Screen

@Composable
fun HomeScreen(
    navController: NavHostController,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(onClick = {
            homeViewModel.logout()
            navController.navigateToAuthScreen(
                shouldPopBackStack = true,
                popUpTo = Screen.HomeScreen.route
            )
        }) {
            Text(text = "Log Out")
        }
    }
}