package com.nasportfolio.clicktoeat.common

import androidx.activity.compose.setContent
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.nasportfolio.clicktoeat.MainActivity
import com.nasportfolio.common.navigation.homeScreenRoute
import com.nasportfolio.common.theme.ClickToEatTheme
import com.nasportfolio.data.di.RepoModule
import com.nasportfolio.restaurant.navigation.restaurantComposable
import com.nasportfolio.test.tags.TestTags
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
@UninstallModules(RepoModule::class)
class LeaveAppTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    lateinit var device: UiDevice

    @Before
    fun setUp() {
        hiltRule.inject()
        composeRule.activity.setContent {
            ClickToEatTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = homeScreenRoute
                ) {
                    restaurantComposable(navController)
                }
            }
        }
        device = UiDevice.getInstance(getInstrumentation())
    }

    @Test
    fun whenLeaveAppAndGoBack_shouldHaveSameRestaurantCardsPresent() {
        composeRule.onAllNodesWithTag(TestTags.FAVORITE_RESTAURANT_TAG)
            .assertCountEquals(1)
        device.pressHome()
        device.pressRecentApps()
        device.waitForWindowUpdate(null, 3000)
        device.click(device.displayWidth / 2, device.displayHeight / 2)
        device.wait(Until.hasObject(By.pkg("com.nasportfolio.clicktoeat")), 5000)
        composeRule.onAllNodesWithTag(TestTags.FAVORITE_RESTAURANT_TAG)
            .assertCountEquals(1)
    }
}