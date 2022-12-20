package com.nasportfolio.auth

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nasportfolio.auth.login.LoginForm

@OptIn(ExperimentalAnimationApi::class)
@Composable
internal fun AuthScreen(
    navController: NavHostController
) {
    val scaffoldState = rememberScaffoldState()
    val authNavController = rememberNavController()

    Scaffold(scaffoldState = scaffoldState) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            NavHost(
                navController = authNavController,
                startDestination = "/login"
            ) {
                composable("/login") {
                    LoginForm(
                        scaffoldState = scaffoldState,
                        navController = navController,
                        changePage = {
                            authNavController.navigate("/sign-up")
                        }
                    )
                }
                composable("/sign-up") {
                    Text(text = "Sign Up")
                }
            }
        }
    }
}