package com.nasportfolio.clicktoeat

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.nasportfolio.auth.navigation.authScreenComposable
import com.nasportfolio.common.navigation.splashScreenRoute
import com.nasportfolio.restaurant.navigation.restaurantComposable
import com.nasportfolio.search.searchComposable
import com.nasportfolio.user.userComposable

@Composable
fun NavGraph(navController: NavHostController, paddingValues: PaddingValues) {
    val bottomPadding by animateDpAsState(
        targetValue = paddingValues.calculateBottomPadding(),
        animationSpec = tween(
            durationMillis = 750
        )
    )

    NavHost(
        modifier = Modifier.padding(bottom = bottomPadding),
        navController = navController,
        startDestination = splashScreenRoute
    ) {
        authScreenComposable(navController = navController)
        restaurantComposable(navController = navController)
        searchComposable(navController = navController)
        userComposable(navController = navController)
    }
}