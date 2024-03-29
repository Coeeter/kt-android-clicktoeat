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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.nasportfolio.common.navigation.homeScreenRoute
import com.nasportfolio.common.navigation.searchScreenRoute
import com.nasportfolio.common.navigation.userProfileScreen
import com.nasportfolio.common.theme.mediumOrange
import com.nasportfolio.test.tags.TestTags

interface BottomAppBarRefreshListener {
    fun refresh()
    fun setIsVisible(isVisible: Boolean)
}

enum class BottomNavigationBarItem(
    val route: String,
    val selectedIcon: ImageVector,
    val label: String,
    val testTag: String
) {
    Home(
        route = homeScreenRoute,
        selectedIcon = Icons.Default.Home,
        label = "Home",
        testTag = TestTags.BOTTOM_NAV_HOME
    ),
    Search(
        route = searchScreenRoute,
        selectedIcon = Icons.Default.Search,
        label = "Search",
        testTag = TestTags.BOTTOM_NAV_SEARCH
    ),
    Profile(
        route = "$userProfileScreen/{userId}",
        selectedIcon = Icons.Default.Person,
        label = "Profile",
        testTag = TestTags.BOTTOM_NAV_PROFILE
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
) {
    val items = BottomNavigationBarItem.values()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentArgs = navBackStackEntry?.arguments?.let {
        currentDestination?.arguments?.get("userId")?.type?.get(
            it,
            key = "userId"
        )
    }
    val currentRoute = currentDestination?.route
    val isVisible = rememberSaveable(currentRoute) {
        currentArgs?.let {
            return@rememberSaveable it == "null"
        }
        currentRoute in items.map { it.route }
    }

    LaunchedEffect(isVisible) {
        bottomPadding.value = if (isVisible) {
            56
        } else {
            0
        }
    }

    AnimatedVisibility(
        visible = isVisible && bottomPadding.value == 56,
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
                var isSelected = currentRoute == item.route
                if (isCurrentRouteProfile) isSelected = currentArgs == "null"

                BottomNavigationItem(
                    modifier = Modifier.testTag(item.testTag),
                    selected = isSelected,
                    selectedContentColor = mediumOrange,
                    icon = {
                        var modifier = Modifier
                            .size(35.dp)
                            .clip(CircleShape)
                        if (isSelected) modifier = modifier.border(
                            width = 2.dp,
                            color = mediumOrange,
                            shape = CircleShape
                        )
                        if (!isSelected) modifier = modifier.alpha(alpha = 0.7f)
                        if (hasProfileImage && isCurrentRouteProfile) Image(
                            bitmap = profileImage!!.asImageBitmap(),
                            contentDescription = item.label,
                            modifier = modifier,
                            contentScale = ContentScale.Crop
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
                        navController.navigate(
                            route = if (isCurrentRouteProfile) {
                                item.route.replace(
                                    oldValue = "{userId}",
                                    newValue = "null"
                                )
                            } else {
                                item.route
                            }
                        ) {
                            popUpTo(homeScreenRoute) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = item.route != currentRoute && !isCurrentRouteProfile
                        }
                    }
                )
            }
        }
    }
}