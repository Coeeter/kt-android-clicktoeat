package com.nasportfolio.clicktoeat.search

import androidx.activity.compose.setContent
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.nasportfolio.clicktoeat.MainActivity
import com.nasportfolio.common.navigation.searchScreenRoute
import com.nasportfolio.common.theme.ClickToEatTheme
import com.nasportfolio.data.di.RepoModule
import com.nasportfolio.search.searchComposable
import com.nasportfolio.test.tags.TestTags
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
@UninstallModules(RepoModule::class)
class SearchQueryTest {
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
                    startDestination = searchScreenRoute
                ) {
                    searchComposable(navController)
                }
            }
        }
    }

    @Test
    fun whenSearch_shouldDisplayOnlyCardsContainingText() {
        runBlocking {
            composeRule.onNodeWithTag(TestTags.SEARCH_INPUT)
                .performTextInput("9")
            composeRule.awaitIdle()
            composeRule.onAllNodesWithTag(TestTags.SEARCH_SCREEN_ITEM)
                .assertAny(hasText("9", substring = true))
        }
    }
}