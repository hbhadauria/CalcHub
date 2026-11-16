package com.hsb.calchub

import android.app.Application
import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import com.hsb.calchub.data.repository.ThemeRepository
import com.hsb.calchub.data.repository.UserDataRepository
import com.hsb.calchub.ui.theme.CalcHubSkin
import com.hsb.calchub.ui.theme.GradientMode
import com.hsb.calchub.ui.theme.ThemePolarity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * JVM-based Robolectric UI verification mirroring [CalcHubEspressoAndComposeTest] so Compose +
 * Espresso navigation and persistence assertions run on every CI build without an emulator.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], qualifiers = "w411dp-h891dp-xxhdpi")
class CalcHubRobolectricUiTest {

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun resetAppState() {
        val app = ApplicationProvider.getApplicationContext<Application>() as CalcHubApplication
        app.getSharedPreferences(ThemeRepository.PREFS_NAME, Context.MODE_PRIVATE)
            .edit().clear().commit()
        app.getSharedPreferences(UserDataRepository.PREFS_NAME, Context.MODE_PRIVATE)
            .edit().clear().commit()
        app.container.themeRepository.setSkin(CalcHubSkin.AURORA)
        app.container.themeRepository.setPolarity(ThemePolarity.SYSTEM)
        app.container.themeRepository.setGradientMode(GradientMode.AUTO_BY_SCREEN)
        app.container.userDataRepository.setSelectedToolsTab(0)
        composeRule.waitForIdle()
    }

    @Test
    fun homeHeader_hidesThemeName_and_opensFriendlyPrivacyDialog() {
        Espresso.onView(isRoot()).check(matches(isDisplayed()))

        composeRule.onNodeWithText("Home").performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText("CalcHub").assertIsDisplayed()
        composeRule.onNodeWithText("30+ Financial Calculators").assertIsDisplayed()
        composeRule.onNodeWithText("Aurora", substring = true).assertDoesNotExist()

        composeRule.onNodeWithContentDescription("Offline privacy status").performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText("Private & Works Offline").assertIsDisplayed()
        composeRule.onNodeWithText("Every calculator works 100% offline, anytime.").assertIsDisplayed()
        composeRule.onNodeWithText("Got it").performClick()
        composeRule.waitForIdle()
    }

    @Test
    fun appearanceSheet_showsFriendlyNonTechnicalCopy_and_switchesTheme() {
        Espresso.onView(isRoot()).check(matches(isDisplayed()))

        composeRule.onNodeWithContentDescription("Customize appearance and theme").performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText("Appearance & Theme").assertIsDisplayed()
        composeRule.onNodeWithText("Choose your preferred style and color mode").assertIsDisplayed()
        composeRule.onNodeWithText("Zero glassmorphism", substring = true).assertDoesNotExist()
        composeRule.onNodeWithText("Vibrant colors with smooth modern cards").assertIsDisplayed()
        composeRule.onNodeWithText("Ledger Paper").assertIsDisplayed()
        composeRule.onNodeWithText("Dot Matrix").assertIsDisplayed()

        composeRule.onNodeWithText("Ledger Paper").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Vibrant colors with smooth modern cards").performClick()
        composeRule.waitForIdle()

        Espresso.pressBack()
        composeRule.waitForIdle()
    }

    @Test
    fun toolsTab_remembersSelectedOption_afterReturningFromDetailScreen() {
        composeRule.onNodeWithText("Tools").performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText("Compare").performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText("SIP vs Home Loan Prepay").assertIsDisplayed()
        composeRule.onNodeWithText("2 · SIP vs Home Loan Prepay").assertDoesNotExist()

        composeRule.onNodeWithText("Open SIP").performScrollTo().performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText("SIP Calculator").assertIsDisplayed()

        Espresso.pressBack()
        composeRule.waitForIdle()

        composeRule.onNodeWithText("SIP vs Home Loan Prepay").assertIsDisplayed()

        // Also survives switching bottom hub tabs (Tools -> Home -> Tools)
        composeRule.onNodeWithText("Home").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Tools").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("SIP vs Home Loan Prepay").assertIsDisplayed()

        composeRule.onNodeWithText("Utilities").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("INR Units & Indian Words").assertIsDisplayed()
    }

    @Test
    fun home_showsOnlyHeartFavorites_and_opensSearchResultViaEspressoBack() {
        composeRule.onNodeWithText("Home").performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithContentDescription("Popular").assertDoesNotExist()

        composeRule.onNodeWithText("Search 30+ calculators (SIP, Tax, EMI...)").performTextInput("Car Loan")
        composeRule.waitForIdle()

        composeRule.onNodeWithText("Auto EMI & interest").performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText("EMI Calculator").assertIsDisplayed()

        Espresso.pressBack()
        composeRule.waitForIdle()

        composeRule.onNodeWithContentDescription("Clear search").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("30+ Financial Calculators").assertIsDisplayed()
    }
}
