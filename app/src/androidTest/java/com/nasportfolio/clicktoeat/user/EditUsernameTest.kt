package com.nasportfolio.clicktoeat.user

import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.nasportfolio.clicktoeat.MainActivity
import com.nasportfolio.common.navigation.homeScreenRoute
import com.nasportfolio.common.navigation.navigateToUserProfile
import com.nasportfolio.common.theme.ClickToEatTheme
import com.nasportfolio.data.di.RepoModule
import com.nasportfolio.restaurant.navigation.restaurantComposable
import com.nasportfolio.test.tags.TestTags
import com.nasportfolio.user.userComposable
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
@UninstallModules(RepoModule::class)
class EditUsernameTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private lateinit var device: UiDevice

    @Before
    fun setUp() {
        hiltRule.inject()
        composeRule.activity.setContent {
            ClickToEatTheme {
                val navController = rememberNavController()
                LaunchedEffect(true) {
                    navController.navigateToUserProfile(userId = null)
                }
                NavHost(
                    navController = navController,
                    startDestination = homeScreenRoute
                ) {
                    restaurantComposable(navController)
                    userComposable(navController)
                }
            }
        }
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    }

    @Test
    fun whenEditProfile_shouldUpdateUsername() {
        runBlocking {
            composeRule.onNodeWithTag(TestTags.EDIT_PROFILE_BTN)
                .assertIsDisplayed()
                .performClick()
            composeRule.awaitIdle()
            composeRule.onNodeWithTag(TestTags.USERNAME_INPUT)
                .performTextClearance()
            composeRule.onNodeWithTag(TestTags.USERNAME_INPUT)
                .performTextInput("TestUsername")
            composeRule.onNodeWithTag(TestTags.UPDATE_ACCOUNT_BTN)
                .performClick()
            composeRule.awaitIdle()
            device.pressBack()
            composeRule.awaitIdle()
            composeRule.onNodeWithText("TestUsername")
                .assertIsDisplayed()
        }
    }
}