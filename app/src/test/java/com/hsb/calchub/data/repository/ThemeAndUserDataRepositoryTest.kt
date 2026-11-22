package com.hsb.calchub.data.repository

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.hsb.calchub.di.AppContainer
import com.hsb.calchub.ui.theme.CalcHubSkin
import com.hsb.calchub.ui.theme.GradientMode
import com.hsb.calchub.ui.theme.ThemePolarity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class ThemeAndUserDataRepositoryTest {
    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        context.getSharedPreferences(ThemeRepository.PREFS_NAME, Context.MODE_PRIVATE)
            .edit().clear().commit()
        context.getSharedPreferences(UserDataRepository.PREFS_NAME, Context.MODE_PRIVATE)
            .edit().clear().commit()
    }

    @Test
    fun `theme repository persists skin polarity and gradient option`() {
        val repo = ThemeRepository(context)
        assertEquals(CalcHubSkin.AURORA, repo.preferences.value.skin)
        assertEquals(ThemePolarity.SYSTEM, repo.preferences.value.polarity)
        assertEquals(GradientMode.AUTO_BY_SCREEN, repo.preferences.value.gradientMode)

        repo.setSkin(CalcHubSkin.LEDGER)
        repo.setPolarity(ThemePolarity.DARK)
        repo.setGradientMode(GradientMode.SUNSET)

        val reloaded = ThemeRepository(context)
        assertEquals(CalcHubSkin.LEDGER, reloaded.preferences.value.skin)
        assertEquals(ThemePolarity.DARK, reloaded.preferences.value.polarity)
        assertEquals(GradientMode.SUNSET, reloaded.preferences.value.gradientMode)

        repo.setSkin(CalcHubSkin.MINIMAL_BLACK)
        assertEquals(CalcHubSkin.MINIMAL_BLACK, ThemeRepository(context).preferences.value.skin)
    }

    @Test
    fun `user data repository persists recent calculation and vault scenarios`() {
        val repo = UserDataRepository(context)
        assertEquals("sip", repo.lastUsed.value.route)
        assertEquals(4, repo.savedScenarios.value.size)

        val updatedRecent = RecentCalculation(
            route = "home_loan_emi",
            calculatorName = "Home Loan",
            paramsSummary = "₹50 L · 8.5% · 20 yrs",
            formattedValue = "₹43,391/mo",
            secondarySummary = "Total Int: ₹54.14 L"
        )
        repo.updateLastUsed(updatedRecent)

        val newScenario = SavedScenario(
            id = "custom_1",
            route = "ppf",
            title = "PPF 15Y Lock",
            summary = "₹1.5 L/yr @ 7.1%",
            resultFormatted = "₹40.68 L"
        )
        repo.saveScenario(newScenario)

        val reloaded = UserDataRepository(context)
        assertEquals("home_loan_emi", reloaded.lastUsed.value.route)
        assertEquals("₹43,391/mo", reloaded.lastUsed.value.formattedValue)
        assertTrue(reloaded.savedScenarios.value.any { it.id == "custom_1" })

        reloaded.removeScenario("custom_1")
        assertTrue(reloaded.savedScenarios.value.none { it.id == "custom_1" })
    }

    @Test
    fun `app container provides single favorites and user data repositories`() {
        val container = AppContainer(context)
        assertTrue(container.userDataRepository.savedScenarios.value.isNotEmpty())
        container.favoritesRepository.toggleFavorite("sip")
        assertTrue(container.favoritesRepository.isFavorite("sip"))
    }

    @Test
    fun `user data repository remembers selected tools sub-tab across recreation`() {
        val repo = UserDataRepository(context)
        assertEquals(0, repo.selectedToolsTab.value)
        repo.setSelectedToolsTab(2)
        assertEquals(2, repo.selectedToolsTab.value)

        val reloaded = UserDataRepository(context)
        assertEquals(2, reloaded.selectedToolsTab.value)

        reloaded.setSelectedToolsTab(99)
        assertEquals(3, reloaded.selectedToolsTab.value)
    }
}
