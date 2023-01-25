package com.nasportfolio.clicktoeat.auth

import android.annotation.SuppressLint
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import com.nasportfolio.auth.navigation.authScreenComposable
import com.nasportfolio.clicktoeat.*
import com.nasportfolio.common.components.effects.CltLaunchFlowCollector
import com.nasportfolio.common.components.navigation.BottomNavigationBarItem
import com.nasportfolio.common.components.navigation.CltBottomBar
import com.nasportfolio.common.components.navigation.rememberBottomBarPadding
import com.nasportfolio.common.navigation.homeScreenRoute
import com.nasportfolio.common.theme.ClickToEatTheme
import com.nasportfolio.data.di.RepoModule
import com.nasportfolio.restaurant.navigation.restaurantComposable
import com.nasportfolio.search.searchComposable
import com.nasportfolio.test.tags.TestTags
import com.nasportfolio.user.userComposable
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
@UninstallModules(RepoModule::class)
class NavGraphBackStackClearedTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    lateinit var device: UiDevice

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    @Before
    fun setUp() {
        hiltRule.inject()
        composeRule.activity.setContent {
            ClickToEatTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val bottomPadding = rememberBottomBarPadding()
                    val lifecycleOwner = LocalLifecycleOwner.current
                    val navController = rememberNavController()
                    val scaffoldState = rememberScaffoldState()

                    CltLaunchFlowCollector(
                        lifecycleOwner = lifecycleOwner,
                        flow = NotificationService.snackBarChannel
                    ) {
                        scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
                        scaffoldState.snackbarHostState.showSnackbar(it, "Okay")
                    }

                    Scaffold(
                        scaffoldState = scaffoldState,
                        bottomBar = {
                            CltBottomBar(
                                navController = navController,
                                bottomPadding = bottomPadding,
                                profileImage = null
                            )
                        }
                    ) {
                        val bottomPadding by animateDpAsState(
                            targetValue = bottomPadding.value.dp,
                            animationSpec = tween(
                                durationMillis = 750
                            )
                        )

                        NavHost(
                            modifier = Modifier.padding(bottom = bottomPadding),
                            navController = navController,
                            startDestination = homeScreenRoute
                        ) {
                            authScreenComposable(navController = navController)
                            restaurantComposable(navController = navController)
                            searchComposable(navController = navController)
                            userComposable(navController = navController)
                        }
                    }
                }
            }
        }
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    }

    @Test
    fun whenLogOut_ShouldNotBeAbleToGoBackHome() {
        runBlocking {
            composeRule.onNodeWithText("Welcome, ", substring = true)
                .assertIsDisplayed()
            composeRule.onNodeWithTag(TestTags.LOG_OUT_BTN)
                .performClick()
            composeRule.awaitIdle()
            composeRule.onNodeWithText("ClickToEat")
                .assertIsDisplayed()
            device.pressBack()
            device.waitForWindowUpdate(null, 3000)
            device.findObject(UiSelector().text("ClickToRun")).exists()
        }
    }

    @Test
    fun whenInOtherBottomNavScreensAndPressBack_ShouldAlwaysBeBackAtHome() {
        runBlocking {
            composeRule.onNodeWithText("Welcome, ", substring = true)
                .assertIsDisplayed()
            composeRule.onNodeWithTag(TestTags.BOTTOM_NAV_SEARCH)
                .performClick()
            composeRule.awaitIdle()
            device.pressBack()
            composeRule.onNodeWithText("Welcome, ", substring = true)
                .assertIsDisplayed()
            composeRule.onNodeWithTag(TestTags.BOTTOM_NAV_PROFILE)
                .performClick()
            composeRule.awaitIdle()
            device.pressBack()
            composeRule.onNodeWithText("Welcome, ", substring = true)
                .assertIsDisplayed()
            composeRule.onNodeWithTag(TestTags.BOTTOM_NAV_PROFILE)
                .performClick()
            composeRule.onNodeWithTag(TestTags.BOTTOM_NAV_SEARCH)
                .performClick()
            composeRule.awaitIdle()
            device.pressBack()
            composeRule.onNodeWithText("Welcome, ", substring = true)
                .assertIsDisplayed()
        }
    }
}