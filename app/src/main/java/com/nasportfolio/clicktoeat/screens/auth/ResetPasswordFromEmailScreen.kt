package com.nasportfolio.clicktoeat.screens.auth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.nasportfolio.clicktoeat.utils.Screen

@Composable
fun ResetPasswordFromEmailScreen(
    email: String?,
    credential: String?,
    navController: NavHostController
) {
    if (email == null || credential == null) return run {
        navController.navigate(Screen.AuthScreen.route)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column {
            Text("email: $email")
            Text("credential: $credential")
        }
    }
}