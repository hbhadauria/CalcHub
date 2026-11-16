package com.hsb.calchub.data.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.Immutable
import com.hsb.calchub.ui.theme.CalcHubSkin
import com.hsb.calchub.ui.theme.GradientMode
import com.hsb.calchub.ui.theme.ThemePolarity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Immutable
data class ThemePreferences(
    val skin: CalcHubSkin = CalcHubSkin.AURORA,
    val polarity: ThemePolarity = ThemePolarity.SYSTEM,
    val gradientMode: GradientMode = GradientMode.AUTO_BY_SCREEN
)

/**
 * Persists selected visual skin, light/dark polarity, and gradient color option offline.
 */
class ThemeRepository(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _preferences = MutableStateFlow(readPrefs())
    val preferences: StateFlow<ThemePreferences> = _preferences.asStateFlow()

    private fun readPrefs(): ThemePreferences {
        val skinName = prefs.getString(KEY_SKIN, CalcHubSkin.AURORA.name)
        val polarityName = prefs.getString(KEY_POLARITY, ThemePolarity.SYSTEM.name)
        val gradName = prefs.getString(KEY_GRADIENT, GradientMode.AUTO_BY_SCREEN.name)
        return ThemePreferences(
            skin = CalcHubSkin.entries.firstOrNull { it.name == skinName } ?: CalcHubSkin.AURORA,
            polarity = ThemePolarity.entries.firstOrNull { it.name == polarityName } ?: ThemePolarity.SYSTEM,
            gradientMode = GradientMode.entries.firstOrNull { it.name == gradName } ?: GradientMode.AUTO_BY_SCREEN
        )
    }

    fun setSkin(skin: CalcHubSkin) {
        update(preferences.value.copy(skin = skin))
    }

    fun setPolarity(polarity: ThemePolarity) {
        update(preferences.value.copy(polarity = polarity))
    }

    fun setGradientMode(gradientMode: GradientMode) {
        update(preferences.value.copy(gradientMode = gradientMode))
    }

    private fun update(next: ThemePreferences) {
        _preferences.value = next
        prefs.edit()
            .putString(KEY_SKIN, next.skin.name)
            .putString(KEY_POLARITY, next.polarity.name)
            .putString(KEY_GRADIENT, next.gradientMode.name)
            .apply()
    }

    companion object {
        const val PREFS_NAME = "calc_theme_prefs"
        private const val KEY_SKIN = "skin"
        private const val KEY_POLARITY = "polarity"
        private const val KEY_GRADIENT = "gradient_mode"
    }
}
