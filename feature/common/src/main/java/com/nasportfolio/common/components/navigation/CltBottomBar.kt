package com.nasportfolio.common.components.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.nasportfolio.common.navigation.homeScreenRoute
import com.nasportfolio.common.theme.mediumOrange

private enum class BottomNavigationBarItem(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    Home(route = homeScreenRoute, icon = Icons.Default.Home, label = "Home"),
    Search(route = "/search", icon = Icons.Default.Search, label = "Search"),
    Profile(route = "/profile", icon = Icons.Default.Person, label = "Profile")
}

@Composable
fun rememberBottomBarPadding() = rememberSaveable {
    mutableStateOf(56)
}

@Composable
fun CltBottomBar(
    bottomPadding: MutableState<Int>,
    navController: NavHostController
) {
    val items = BottomNavigationBarItem.values()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isVisible = rememberSaveable(currentRoute) {
        currentRoute in items.map { it.route }
    }

    LaunchedEffect(currentRoute) {
        bottomPadding.value = if (currentRoute in items.map { it.route }) {
            56
        } else {
            0
        }
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            animationSpec = tween(
                durationMillis = 750
            ),
            initialOffsetY = { it }
        ),
        exit = slideOutVertically(
            animationSpec = tween(
                durationMillis = 750
            ),
            targetOffsetY = { it }
        )
    ) {
        BottomAppBar(
            backgroundColor = MaterialTheme.colors.surface,
            elevation = 10.dp
        ) {
            items.forEach { item ->
                BottomNavigationItem(
                    selected = currentRoute == item.route,
                    selectedContentColor = mediumOrange,
                    icon = {
                        Icon(
                            item.icon,
                            contentDescription = item.label
                        )
                    },
                    label = { Text(item.label) },
                    onClick = {
                        navController.navigate(item.route) {
                            navController.graph.startDestinationRoute?.let { route ->
                                popUpTo(route) { saveState = true }
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}