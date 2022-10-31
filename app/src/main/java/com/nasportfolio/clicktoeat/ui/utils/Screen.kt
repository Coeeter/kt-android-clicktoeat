package com.nasportfolio.clicktoeat.ui.utils

sealed class Screen(val route: String, val deepLink: String? = null) {
    object AuthScreen : Screen("/auth")
    object ResetPasswordFromLinkScreen : Screen("/reset-password", "https://clicktoeat.nasportfolio.com/reset-password")
    object HomeScreen : Screen("/home")
    object SearchScreen : Screen("/search")
    object RestaurantDetailsScreen : Screen("/restaurant")
    object UserProfileScreen : Screen("/users")
    object SettingsScreen : Screen("/settings")
}