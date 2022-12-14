package com.nasportfolio.clicktoeat.utils

sealed class Screen(val route: String, val deepLink: String? = null) {
    object SplashScreen : Screen("/splash")
    object AuthScreen : Screen("/auth")
    object HomeScreen : Screen("/home")
    object SearchScreen : Screen("/search")
    object RestaurantDetailsScreen : Screen("/restaurant")
    object UserProfileScreen : Screen("/users")
    object SettingsScreen : Screen("/settings")
    object ResetPasswordFromLinkScreen : Screen(
        "/reset-password",
        "https://clicktoeat.nasportfolio.com/reset-password"
    )
}