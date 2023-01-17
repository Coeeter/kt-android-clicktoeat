package com.nasportfolio.common.components.navigation

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.BottomAppBar
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.nasportfolio.common.components.loading.CltShimmer
import com.nasportfolio.common.navigation.homeScreenRoute
import com.nasportfolio.common.navigation.searchScreenRoute
import com.nasportfolio.common.navigation.userProfileScreen
import com.nasportfolio.common.theme.mediumOrange

enum class BottomNavigationBarItem(
    val route: String,
    val selectedIcon: ImageVector,
    val label: String
) {
    Home(
        route = homeScreenRoute,
        selectedIcon = Icons.Default.Home,
        label = "Home"
    ),
    Search(
        route = searchScreenRoute,
        selectedIcon = Icons.Default.Search,
        label = "Search"
    ),
    Profile(
        route = userProfileScreen,
        selectedIcon = Icons.Default.Person,
        label = "Profile"
    )
}

@Composable
fun rememberBottomBarPadding() = rememberSaveable {
    mutableStateOf(56)
}

@Composable
fun CltBottomBar(
    bottomPadding: MutableState<Int>,
    navController: NavHostController,
    profileImage: Bitmap? = null,
    isLoading: Boolean = false
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
                val hasProfileImage = profileImage != null
                val isCurrentRouteProfile = item.route == BottomNavigationBarItem.Profile.route
                val isSelected = currentRoute == item.route

                BottomNavigationItem(
                    selected = isSelected,
                    selectedContentColor = mediumOrange,
                    icon = {
                        if (isCurrentRouteProfile && isLoading) CltShimmer(
                            modifier = Modifier
                                .size(35.dp)
                                .clip(CircleShape)
                                .border(if (isSelected) 2.dp else 0.dp, mediumOrange, CircleShape)
                        )
                        if (hasProfileImage && isCurrentRouteProfile) Image(
                            bitmap = profileImage!!.asImageBitmap(),
                            contentDescription = item.label,
                            modifier = Modifier
                                .size(35.dp)
                                .clip(CircleShape)
                                .border(if (isSelected) 2.dp else 0.dp, mediumOrange, CircleShape)
                        )
                        if (!isCurrentRouteProfile || !hasProfileImage) Icon(
                            imageVector = item.selectedIcon,
                            contentDescription = item.label
                        )
                    },
                    onClick = {
                        if (item.route == currentRoute) {
                            navController.popBackStack(
                                route = item.route,
                                inclusive = false
                            )
                            return@BottomNavigationItem
                        }
                        navController.navigate(item.route) {
                            popUpTo(homeScreenRoute) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = item.route != currentRoute
                        }
                    }
                )
            }
        }
    }
}