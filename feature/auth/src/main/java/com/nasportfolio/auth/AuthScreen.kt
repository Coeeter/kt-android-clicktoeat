package com.nasportfolio.auth

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nasportfolio.auth.animations.SplashToAuthAnimation
import com.nasportfolio.auth.login.LoginForm

@OptIn(ExperimentalAnimationApi::class)
@Composable
internal fun AuthScreen(
    navController: NavHostController
) {
    val scaffoldState = rememberScaffoldState()
    val authNavController = rememberNavController()

    Scaffold(scaffoldState = scaffoldState) {
        SplashToAuthAnimation { alignment ->
            NavHost(
                modifier = Modifier.fillMaxSize(),
                navController = authNavController,
                startDestination = "/login"
            ) {
                composable("/login") {
                    LoginForm(
                        scaffoldState = scaffoldState,
                        navController = navController,
                        alignment = alignment,
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