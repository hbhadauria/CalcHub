package com.hsb.calchub.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.hsb.calchub.domain.model.CalculatorCategory

/**
 * Semantic design tokens shared across Expressive Aurora, Ledger Paper, and MATRIX // DOT.
 */
@Immutable
data class CalcHubDesignTokens(
    val skin: CalcHubSkin,
    val isDark: Boolean,
    val gradientMode: GradientMode,
    val activeCategory: CalculatorCategory,
    val appBackground: Color,
    val surfaceLowest: Color,
    val surfaceLow: Color,
    val surfaceContainer: Color,
    val surfaceHigh: Color,
    val surfaceToolbar: Color,
    val inkPrimary: Color,
    val inkSecondary: Color,
    val inkMuted: Color,
    val accentPrimary: Color,
    val onAccent: Color,
    val accentContainer: Color,
    val onAccentContainer: Color,
    val gainInk: Color,
    val gainBg: Color,
    val lossInk: Color,
    val lossBg: Color,
    val cardBorder: Color,
    val trackBg: Color,
    val highlightBg: Color,
    val heroCornerRadius: Dp,
    val cardCornerRadius: Dp,
    val pillCornerRadius: Dp,
    val heroWashStart: Color,
    val heroWashEnd: Color,
    val chartStart: Color,
    val chartEnd: Color,
    val useGradientFill: Boolean
)

val LocalCalcHubTokens = staticCompositionLocalOf {
    buildCalcHubTokens(
        skin = CalcHubSkin.AURORA,
        isDark = false,
        gradientMode = GradientMode.AUTO_BY_SCREEN,
        category = CalculatorCategory.INVESTMENT
    )
}

fun buildCalcHubTokens(
    skin: CalcHubSkin,
    isDark: Boolean,
    gradientMode: GradientMode,
    category: CalculatorCategory
): CalcHubDesignTokens {
    val palette = CategoryPalettes.resolveForScreen(category, gradientMode)
    val useGrad = gradientMode != GradientMode.SOLID_OFF

    return when (skin) {
        CalcHubSkin.AURORA -> {
            val accent = if (isDark) Color(0xFF818CF8) else Color(0xFF4F46E5)
            val accentCont = if (isDark) Color(0xFF272B5E) else Color(0xFFE0E7FF)
            val heroS = if (!useGrad) accentCont else if (isDark) palette.darkWashStart else palette.lightWashStart
            val heroE = if (!useGrad) accentCont else if (isDark) palette.darkWashEnd else palette.lightWashEnd
            val c1 = if (useGrad) palette.gradStart else accent
            val c2 = if (useGrad) palette.gradEnd else accent
            CalcHubDesignTokens(
                skin = skin,
                isDark = isDark,
                gradientMode = gradientMode,
                activeCategory = category,
                appBackground = if (isDark) Color(0xFF0B0E17) else Color(0xFFF8FAFE),
                surfaceLowest = if (isDark) Color(0xFF131724) else Color(0xFFFFFFFF),
                surfaceLow = if (isDark) Color(0xFF181D2E) else Color(0xFFF3F6FC),
                surfaceContainer = if (isDark) Color(0xFF1E2438) else Color(0xFFEAEFF8),
                surfaceHigh = if (isDark) Color(0xFF262E47) else Color(0xFFE0E6F3),
                surfaceToolbar = if (isDark) Color(0xFF171C2B) else Color(0xFFFFFFFF),
                inkPrimary = if (isDark) Color(0xFFF4F7FF) else Color(0xFF0B1020),
                inkSecondary = if (isDark) Color(0xFFB4BED6) else Color(0xFF434B61),
                inkMuted = if (isDark) Color(0xFF8E99B6) else Color(0xFF5A637B),
                accentPrimary = accent,
                onAccent = if (isDark) Color(0xFF0F172A) else Color(0xFFFFFFFF),
                accentContainer = accentCont,
                onAccentContainer = if (isDark) Color(0xFFE0E7FF) else Color(0xFF1E1B4B),
                gainInk = if (isDark) AuroraGainDark else AuroraGainLight,
                gainBg = if (isDark) AuroraGainBgDark else AuroraGainBgLight,
                lossInk = if (isDark) AuroraLossDark else AuroraLossLight,
                lossBg = if (isDark) AuroraLossBgDark else AuroraLossBgLight,
                cardBorder = if (isDark) Color(0x1AF4F7FF) else Color(0x140F172A),
                trackBg = if (isDark) Color(0xFF2A324D) else Color(0xFFDCE3F0),
                highlightBg = Color.Transparent,
                heroCornerRadius = 28.dp,
                cardCornerRadius = 22.dp,
                pillCornerRadius = 999.dp,
                heroWashStart = heroS,
                heroWashEnd = heroE,
                chartStart = c1,
                chartEnd = c2,
                useGradientFill = useGrad
            )
        }

        CalcHubSkin.LEDGER -> {
            val accent = if (isDark) Color(0xFF93C5FD) else Color(0xFF1D4ED8)
            val heroSolid = if (isDark) Color(0xFF1E1C18) else Color(0xFFFFFDF8)
            CalcHubDesignTokens(
                skin = skin,
                isDark = isDark,
                gradientMode = gradientMode,
                activeCategory = category,
                appBackground = if (isDark) Color(0xFF141311) else Color(0xFFFAF7F0),
                surfaceLowest = if (isDark) Color(0xFF1C1A17) else Color(0xFFFFFDF9),
                surfaceLow = if (isDark) Color(0xFF211F1B) else Color(0xFFF5F1E7),
                surfaceContainer = if (isDark) Color(0xFF292622) else Color(0xFFECE6D9),
                surfaceHigh = if (isDark) Color(0xFF332F29) else Color(0xFFE2DACB),
                surfaceToolbar = if (isDark) Color(0xFF1E1C19) else Color(0xFFFFFDF8),
                inkPrimary = if (isDark) Color(0xFFF5F2EB) else Color(0xFF1C1917),
                inkSecondary = if (isDark) Color(0xFFB8B2A6) else Color(0xFF57534E),
                inkMuted = if (isDark) Color(0xFF928C80) else Color(0xFF6E6861),
                accentPrimary = accent,
                onAccent = if (isDark) Color(0xFF0F172A) else Color(0xFFFFFFFF),
                accentContainer = if (isDark) Color(0xFF1E3A8A) else Color(0xFFDBEAFE),
                onAccentContainer = if (isDark) Color(0xFFDBEAFE) else Color(0xFF1E3A8A),
                gainInk = if (isDark) Color(0xFF4ADE80) else Color(0xFF15803D),
                gainBg = if (isDark) Color(0xFF143321) else Color(0xFFDCFCE7),
                lossInk = if (isDark) Color(0xFFFCA5A5) else Color(0xFFB91C1C),
                lossBg = if (isDark) Color(0xFF3E1818) else Color(0xFFFEE2E2),
                cardBorder = if (isDark) Color(0x21F5F2EB) else Color(0x211C1917),
                trackBg = if (isDark) Color(0xFF38342E) else Color(0xFFDDD6C7),
                highlightBg = if (isDark) LedgerHighlightDark else LedgerHighlightLight,
                heroCornerRadius = 14.dp,
                cardCornerRadius = 12.dp,
                pillCornerRadius = 10.dp,
                heroWashStart = heroSolid,
                heroWashEnd = heroSolid,
                chartStart = if (isDark) Color(0xFF60A5FA) else Color(0xFF1D4ED8),
                chartEnd = if (isDark) Color(0xFF93C5FD) else Color(0xFF3B82F6),
                useGradientFill = false
            )
        }

        CalcHubSkin.MATRIX -> {
            val accent = if (isDark) Color(0xFFFF2E2E) else Color(0xFFD92020)
            val heroSolid = if (isDark) Color(0xFF0F1014) else Color(0xFFFAF9F5)
            val chartG = if (isDark) Color(0xFF22C55E) else Color(0xFF15803D)
            CalcHubDesignTokens(
                skin = skin,
                isDark = isDark,
                gradientMode = gradientMode,
                activeCategory = category,
                appBackground = if (isDark) Color(0xFF050506) else Color(0xFFEFECE6),
                surfaceLowest = if (isDark) Color(0xFF0E0F12) else Color(0xFFFAF9F5),
                surfaceLow = if (isDark) Color(0xFF131519) else Color(0xFFF4F2EC),
                surfaceContainer = if (isDark) Color(0xFF191C22) else Color(0xFFE6E3DB),
                surfaceHigh = if (isDark) Color(0xFF232730) else Color(0xFFDAD6CC),
                surfaceToolbar = if (isDark) Color(0xFF0E0F12) else Color(0xFFFAF9F5),
                inkPrimary = if (isDark) Color(0xFFF5F6F8) else Color(0xFF121212),
                inkSecondary = if (isDark) Color(0xFFA6ACB8) else Color(0xFF4B4B4B),
                inkMuted = if (isDark) Color(0xFF848B99) else Color(0xFF636363),
                accentPrimary = accent,
                onAccent = Color(0xFFFFFFFF),
                accentContainer = if (isDark) Color(0xFF3F1114) else Color(0xFFFEE2E2),
                onAccentContainer = if (isDark) Color(0xFFFECACA) else Color(0xFF7F1D1D),
                gainInk = chartG,
                gainBg = if (isDark) Color(0xFF0C2E1B) else Color(0xFFDCFCE7),
                lossInk = if (isDark) Color(0xFFF87171) else Color(0xFFB91C1C),
                lossBg = if (isDark) Color(0xFF3B1214) else Color(0xFFFEE2E2),
                cardBorder = if (isDark) Color(0x21FFFFFF) else Color(0x29121212),
                trackBg = if (isDark) Color(0xFF262A33) else Color(0xFFD5D1C7),
                highlightBg = Color.Transparent,
                heroCornerRadius = 12.dp,
                cardCornerRadius = 10.dp,
                pillCornerRadius = 6.dp,
                heroWashStart = heroSolid,
                heroWashEnd = heroSolid,
                chartStart = chartG,
                chartEnd = accent,
                useGradientFill = true
            )
        }
    }
}

private fun CalcHubDesignTokens.toMaterialColorScheme(): ColorScheme = if (isDark) {
    darkColorScheme(
        primary = accentPrimary,
        onPrimary = onAccent,
        primaryContainer = accentContainer,
        onPrimaryContainer = onAccentContainer,
        secondary = chartStart,
        onSecondary = onAccent,
        tertiary = chartEnd,
        onTertiary = onAccent,
        background = appBackground,
        onBackground = inkPrimary,
        surface = surfaceLowest,
        onSurface = inkPrimary,
        surfaceVariant = surfaceContainer,
        onSurfaceVariant = inkSecondary,
        outline = cardBorder,
        error = lossInk
    )
} else {
    lightColorScheme(
        primary = accentPrimary,
        onPrimary = onAccent,
        primaryContainer = accentContainer,
        onPrimaryContainer = onAccentContainer,
        secondary = chartStart,
        onSecondary = Color.White,
        tertiary = chartEnd,
        onTertiary = Color.White,
        background = appBackground,
        onBackground = inkPrimary,
        surface = surfaceLowest,
        onSurface = inkPrimary,
        surfaceVariant = surfaceContainer,
        onSurfaceVariant = inkSecondary,
        outline = cardBorder,
        error = lossInk
    )
}

/**
 * Re-scopes [LocalCalcHubTokens] and Material [ColorScheme] to a calculator screen's [category],
 * while honoring the user's [GradientMode] option (Refinements #1 & #5).
 */
@Composable
fun ProvideCategoryAccent(
    category: CalculatorCategory,
    content: @Composable () -> Unit
) {
    val current = LocalCalcHubTokens.current
    val scoped = buildCalcHubTokens(
        skin = current.skin,
        isDark = current.isDark,
        gradientMode = current.gradientMode,
        category = category
    )
    CompositionLocalProvider(LocalCalcHubTokens provides scoped) {
        MaterialTheme(
            colorScheme = scoped.toMaterialColorScheme(),
            typography = MaterialTheme.typography,
            shapes = MaterialTheme.shapes,
            content = content
        )
    }
}

@Composable
fun CalcHubTheme(
    skin: CalcHubSkin = CalcHubSkin.AURORA,
    polarity: ThemePolarity = ThemePolarity.SYSTEM,
    gradientMode: GradientMode = GradientMode.AUTO_BY_SCREEN,
    activeCategory: CalculatorCategory = CalculatorCategory.INVESTMENT,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val resolvedDark = when (polarity) {
        ThemePolarity.SYSTEM -> darkTheme
        ThemePolarity.LIGHT -> false
        ThemePolarity.DARK -> true
    }

    val tokens = buildCalcHubTokens(
        skin = skin,
        isDark = resolvedDark,
        gradientMode = gradientMode,
        category = activeCategory
    )

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val activity = view.context as? Activity ?: return@SideEffect
            val controller = WindowCompat.getInsetsController(activity.window, view)
            controller.isAppearanceLightStatusBars = !resolvedDark
            controller.isAppearanceLightNavigationBars = !resolvedDark
        }
    }

    val shapes = Shapes(
        small = RoundedCornerShape(tokens.pillCornerRadius),
        medium = RoundedCornerShape(tokens.cardCornerRadius),
        large = RoundedCornerShape(tokens.heroCornerRadius)
    )

    CompositionLocalProvider(LocalCalcHubTokens provides tokens) {
        MaterialTheme(
            colorScheme = tokens.toMaterialColorScheme(),
            typography = calcHubTypography(skin),
            shapes = shapes,
            content = content
        )
    }
}
