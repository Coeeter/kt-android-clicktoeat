package com.nasportfolio.common.utils

sealed class Screen(val route: String, val deepLink: String? = null) {
    object HomeScreen : Screen("/home")
    object SearchScreen : Screen("/search")
    object RestaurantDetailsScreen : Screen("/restaurant")
    object UserProfileScreen : Screen("/users")
    object SettingsScreen : Screen("/settings")
}