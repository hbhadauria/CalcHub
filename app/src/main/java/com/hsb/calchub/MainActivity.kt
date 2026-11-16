package com.hsb.calchub

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hsb.calchub.di.LocalAppContainer
import com.hsb.calchub.ui.navigation.AppNavigation
import com.hsb.calchub.ui.theme.CalcHubTheme
import com.hsb.calchub.ui.theme.LocalCalcHubTokens

/**
 * Single Activity host configuring edge-to-edge system bars, manual DI container scope,
 * live skin/polarity/gradient observation, and Navigation 3 display host.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val appContainer = (application as CalcHubApplication).container

        setContent {
            CompositionLocalProvider(LocalAppContainer provides appContainer) {
                val themePrefs by appContainer.themeRepository.preferences.collectAsStateWithLifecycle()

                CalcHubTheme(
                    skin = themePrefs.skin,
                    polarity = themePrefs.polarity,
                    gradientMode = themePrefs.gradientMode
                ) {
                    val tokens = LocalCalcHubTokens.current
                    val view = LocalView.current
                    if (!view.isInEditMode) {
                        SideEffect {
                            val window = (view.context as? Activity)?.window
                            if (window != null) {
                                val controller = WindowCompat.getInsetsController(window, view)
                                controller.isAppearanceLightStatusBars = !tokens.isDark
                                controller.isAppearanceLightNavigationBars = !tokens.isDark
                            }
                        }
                    }

                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = tokens.appBackground
                    ) {
                        AppNavigation()
                    }
                }
            }
        }
    }
}
