package com.nasportfolio.clicktoeat

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.test.filters.LargeTest
import com.nasportfolio.auth.navigation.authScreenComposable
import com.nasportfolio.common.navigation.authScreenRoute
import com.nasportfolio.common.theme.ClickToEatTheme
import com.nasportfolio.data.di.RepoModule
import com.nasportfolio.domain.user.UserRepository
import com.nasportfolio.restaurant.navigation.restaurantComposable
import com.nasportfolio.test.tags.TestTags
import com.nasportfolio.test.user.FakeUserRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
@UninstallModules(RepoModule::class)
class LoginTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var fakeUserRepository: UserRepository

    @Before
    fun setUp() {
        hiltRule.inject()
        composeRule.setContent {
            ClickToEatTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = authScreenRoute
                ) {
                    authScreenComposable(navController)
                    restaurantComposable(navController)
                }
            }
        }
    }

    @Test
    fun loginWithEmptyField_showError() {
        composeRule.onNodeWithTag(TestTags.LOGIN_BUTTON).assertIsDisplayed().performClick()
        composeRule.onNodeWithText("Email required!").assertIsDisplayed()
        composeRule.onNodeWithText("Password required!").assertIsDisplayed()
    }

    @Test
    fun loginWithInvalidField_showError() {
        composeRule.onNodeWithTag(TestTags.EMAIL_INPUT).performTextInput("Totally invalid email here")
        composeRule.onNodeWithTag(TestTags.PASSWORD_INPUT).performTextInput("StrongPassword!")
        composeRule.onNodeWithTag(TestTags.LOGIN_BUTTON).assertIsDisplayed().performClick()
        composeRule.onNodeWithText("Invalid email provided").assertIsDisplayed()
    }

    @Test
    fun loginWithValidField_navigateToHomeScreen() {
        val user = (fakeUserRepository as FakeUserRepository).users.last()
        composeRule.onNodeWithTag(TestTags.EMAIL_INPUT).performTextInput(user.email)
        composeRule.onNodeWithTag(TestTags.PASSWORD_INPUT).performTextInput("StrongPassword!")
        composeRule.onNodeWithTag(TestTags.LOGIN_BUTTON).assertIsDisplayed().performClick()
        composeRule.onNodeWithText("Welcome, ${user.username}").assertIsDisplayed()
    }
}