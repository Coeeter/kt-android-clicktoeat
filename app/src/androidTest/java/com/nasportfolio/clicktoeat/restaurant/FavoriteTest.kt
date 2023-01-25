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
import com.nasportfolio.domain.favorites.FavoriteRepository
import com.nasportfolio.restaurant.navigation.restaurantComposable
import com.nasportfolio.test.favorites.FakeFavoriteRepository
import com.nasportfolio.test.tags.TestTags
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
@UninstallModules(RepoModule::class)
class FavoriteTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var favoriteRepository: FavoriteRepository

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
    fun whenAddFavAndRefreshPage_restaurantShouldBeInFavSection() {
        val repo = favoriteRepository as FakeFavoriteRepository
        repo.favorites = emptyList()
        composeRule.onNodeWithTag(TestTags.LAZY_COLUMN)
            .performTouchInput { swipeDown() }
        composeRule.onAllNodesWithTag(TestTags.FAVORITE_RESTAURANT_TAG)
            .assertCountEquals(0)
        composeRule.onAllNodesWithTag(TestTags.FEATURED_RESTAURANT_TAG)[0]
            .onChildren()
            .filterToOne(hasTestTag(TestTags.FAVORITE_BTN))
            .performClick()
        composeRule.onNodeWithTag(TestTags.LAZY_COLUMN)
            .performTouchInput { swipeDown() }
        composeRule.onAllNodesWithTag(TestTags.FAVORITE_RESTAURANT_TAG)
            .assertCountEquals(1)
    }

    @Test
    fun whenClickFavBtnAndRefresh_shouldRemoveFavFromFavSection() {
        composeRule.onAllNodesWithTag(TestTags.FAVORITE_RESTAURANT_TAG)
            .assertCountEquals(1)
        composeRule.onAllNodesWithTag(TestTags.FAVORITE_RESTAURANT_TAG)[0]
            .onChildren()
            .filterToOne(hasTestTag(TestTags.FAVORITE_BTN))
            .performClick()
        composeRule.onNodeWithTag(TestTags.LAZY_COLUMN)
            .performTouchInput { swipeDown() }
        composeRule.onAllNodesWithTag(TestTags.FAVORITE_RESTAURANT_TAG)
            .assertCountEquals(0)
    }
}