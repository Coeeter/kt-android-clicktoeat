package com.nasportfolio.clicktoeat.restaurant

import androidx.activity.compose.setContent
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
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
class EditRestaurantTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

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
    }

    @Test
    fun whenEditRestaurantName_ShouldUpdateNameInDetails() {
        val name = "Test Restaurant Name"
        composeRule.onAllNodesWithTag(TestTags.FAVORITE_RESTAURANT_TAG)[0]
            .performClick()
        composeRule.onNodeWithTag(TestTags.EDIT_RESTAURANT_FAB)
            .performClick()
        composeRule.onNodeWithTag(TestTags.EDIT_RESTAURANT_SPEED_DIAL)
            .performClick()
        composeRule.onNodeWithTag(TestTags.RESTAURANT_NAME_INPUT)
            .performTextClearance()
        composeRule.onNodeWithTag(TestTags.RESTAURANT_NAME_INPUT)
            .performTextInput(name)
        composeRule.onNodeWithTag(TestTags.UPDATE_RESTAURANT_BTN)
            .performClick()
        composeRule.onNodeWithText(name)
            .assertIsDisplayed()
    }
}