package com.nasportfolio.auth

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.nasportfolio.auth.animations.SplashToAuthAnimation
import com.nasportfolio.auth.login.LoginForm
import com.nasportfolio.auth.signup.SignUpForm

private const val TransitionDurationMillis = 500

@OptIn(ExperimentalAnimationApi::class)
@Composable
internal fun AuthScreen(
    navController: NavHostController
) {
    val scaffoldState = rememberScaffoldState()
    val authNavController = rememberAnimatedNavController()

    Scaffold(scaffoldState = scaffoldState) { padding ->
        SplashToAuthAnimation {
            AnimatedNavHost(
                modifier = Modifier.fillMaxSize(),
                navController = authNavController,
                startDestination = "/login"
            ) {
                composable(
                    route = "/login",
                    enterTransition = {
                        slideIntoContainer(
                            towards = AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(durationMillis = TransitionDurationMillis)
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            towards = AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(durationMillis = TransitionDurationMillis)
                        )
                    },
                    popEnterTransition = {
                        slideIntoContainer(
                            towards = AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(durationMillis = TransitionDurationMillis)
                        )
                    },
                ) {
                    LoginForm(
                        scaffoldState = scaffoldState,
                        navController = navController,
                        changePage = {
                            authNavController.navigate("/sign-up")
                        }
                    )
                }
                composable(
                    route = "/sign-up",
                    enterTransition = {
                        slideIntoContainer(
                            towards = AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(durationMillis = TransitionDurationMillis)
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            towards = AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(durationMillis = TransitionDurationMillis)
                        )
                    },
                    popExitTransition = {
                        slideOutOfContainer(
                            towards = AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(durationMillis = TransitionDurationMillis)
                        )
                    }
                ) {
                    SignUpForm(
                        scaffoldState = scaffoldState,
                        navController = navController,
                        changePage = {
                            authNavController.navigate("/login") {
                                popUpTo("/login") {
                                    inclusive = true
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}