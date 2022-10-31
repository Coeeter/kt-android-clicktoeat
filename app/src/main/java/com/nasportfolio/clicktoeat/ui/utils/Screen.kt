package com.nasportfolio.clicktoeat.ui.utils

sealed class Screen(val route: String) {
    object AuthScreen: Screen("/auth")
    object HomeScreen: Screen("/home")
    object SearchScreen: Screen("/search")
    object RestaurantDetailsScreen: Screen("/restaurant")
    object UserProfileScreen: Screen("/users")
    object SettingsScreen: Screen("/settings")
}