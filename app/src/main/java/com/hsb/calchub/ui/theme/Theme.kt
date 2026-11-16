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

private fun pickColor(isDark: Boolean, dark: Color, light: Color): Color = if (isDark) dark else light

fun buildCalcHubTokens(
    skin: CalcHubSkin,
    isDark: Boolean,
    gradientMode: GradientMode,
    category: CalculatorCategory
): CalcHubDesignTokens = when (skin) {
    CalcHubSkin.AURORA -> buildAuroraTokens(isDark, gradientMode, category)
    CalcHubSkin.LEDGER -> buildLedgerTokens(isDark, gradientMode, category)
    CalcHubSkin.MATRIX -> buildMatrixTokens(isDark, gradientMode, category)
}

private fun buildAuroraTokens(
    isDark: Boolean,
    gradientMode: GradientMode,
    category: CalculatorCategory
): CalcHubDesignTokens {
    val palette = CategoryPalettes.resolveForScreen(category, gradientMode)
    val useGrad = gradientMode != GradientMode.SOLID_OFF
    val accent = pickColor(isDark, Color(0xFF818CF8), Color(0xFF4F46E5))
    val accentCont = pickColor(isDark, Color(0xFF272B5E), Color(0xFFE0E7FF))
    val heroS = if (useGrad) pickColor(isDark, palette.darkWashStart, palette.lightWashStart) else accentCont
    val heroE = if (useGrad) pickColor(isDark, palette.darkWashEnd, palette.lightWashEnd) else accentCont
    return CalcHubDesignTokens(
        skin = CalcHubSkin.AURORA,
        isDark = isDark,
        gradientMode = gradientMode,
        activeCategory = category,
        appBackground = pickColor(isDark, Color(0xFF0B0E17), Color(0xFFF8FAFE)),
        surfaceLowest = pickColor(isDark, Color(0xFF131724), Color(0xFFFFFFFF)),
        surfaceLow = pickColor(isDark, Color(0xFF181D2E), Color(0xFFF3F6FC)),
        surfaceContainer = pickColor(isDark, Color(0xFF1E2438), Color(0xFFEAEFF8)),
        surfaceHigh = pickColor(isDark, Color(0xFF262E47), Color(0xFFE0E6F3)),
        surfaceToolbar = pickColor(isDark, Color(0xFF171C2B), Color(0xFFFFFFFF)),
        inkPrimary = pickColor(isDark, Color(0xFFF4F7FF), Color(0xFF0B1020)),
        inkSecondary = pickColor(isDark, Color(0xFFB4BED6), Color(0xFF434B61)),
        inkMuted = pickColor(isDark, Color(0xFF8E99B6), Color(0xFF5A637B)),
        accentPrimary = accent,
        onAccent = pickColor(isDark, Color(0xFF0F172A), Color(0xFFFFFFFF)),
        accentContainer = accentCont,
        onAccentContainer = pickColor(isDark, Color(0xFFE0E7FF), Color(0xFF1E1B4B)),
        gainInk = pickColor(isDark, AuroraGainDark, AuroraGainLight),
        gainBg = pickColor(isDark, AuroraGainBgDark, AuroraGainBgLight),
        lossInk = pickColor(isDark, AuroraLossDark, AuroraLossLight),
        lossBg = pickColor(isDark, AuroraLossBgDark, AuroraLossBgLight),
        cardBorder = pickColor(isDark, Color(0x1AF4F7FF), Color(0x140F172A)),
        trackBg = pickColor(isDark, Color(0xFF2A324D), Color(0xFFDCE3F0)),
        highlightBg = Color.Transparent,
        heroCornerRadius = 28.dp,
        cardCornerRadius = 22.dp,
        pillCornerRadius = 999.dp,
        heroWashStart = heroS,
        heroWashEnd = heroE,
        chartStart = if (useGrad) palette.gradStart else accent,
        chartEnd = if (useGrad) palette.gradEnd else accent,
        useGradientFill = useGrad
    )
}

private fun buildLedgerTokens(
    isDark: Boolean,
    gradientMode: GradientMode,
    category: CalculatorCategory
): CalcHubDesignTokens {
    val heroSolid = pickColor(isDark, Color(0xFF1E1C18), Color(0xFFFFFDF8))
    return CalcHubDesignTokens(
        skin = CalcHubSkin.LEDGER,
        isDark = isDark,
        gradientMode = gradientMode,
        activeCategory = category,
        appBackground = pickColor(isDark, Color(0xFF141311), Color(0xFFFAF7F0)),
        surfaceLowest = pickColor(isDark, Color(0xFF1C1A17), Color(0xFFFFFDF9)),
        surfaceLow = pickColor(isDark, Color(0xFF211F1B), Color(0xFFF5F1E7)),
        surfaceContainer = pickColor(isDark, Color(0xFF292622), Color(0xFFECE6D9)),
        surfaceHigh = pickColor(isDark, Color(0xFF332F29), Color(0xFFE2DACB)),
        surfaceToolbar = pickColor(isDark, Color(0xFF1E1C19), Color(0xFFFFFDF8)),
        inkPrimary = pickColor(isDark, Color(0xFFF5F2EB), Color(0xFF1C1917)),
        inkSecondary = pickColor(isDark, Color(0xFFB8B2A6), Color(0xFF57534E)),
        inkMuted = pickColor(isDark, Color(0xFF928C80), Color(0xFF6E6861)),
        accentPrimary = pickColor(isDark, Color(0xFF93C5FD), Color(0xFF1D4ED8)),
        onAccent = pickColor(isDark, Color(0xFF0F172A), Color(0xFFFFFFFF)),
        accentContainer = pickColor(isDark, Color(0xFF1E3A8A), Color(0xFFDBEAFE)),
        onAccentContainer = pickColor(isDark, Color(0xFFDBEAFE), Color(0xFF1E3A8A)),
        gainInk = pickColor(isDark, Color(0xFF4ADE80), Color(0xFF15803D)),
        gainBg = pickColor(isDark, Color(0xFF143321), Color(0xFFDCFCE7)),
        lossInk = pickColor(isDark, Color(0xFFFCA5A5), Color(0xFFB91C1C)),
        lossBg = pickColor(isDark, Color(0xFF3E1818), Color(0xFFFEE2E2)),
        cardBorder = pickColor(isDark, Color(0x21F5F2EB), Color(0x211C1917)),
        trackBg = pickColor(isDark, Color(0xFF38342E), Color(0xFFDDD6C7)),
        highlightBg = pickColor(isDark, LedgerHighlightDark, LedgerHighlightLight),
        heroCornerRadius = 14.dp,
        cardCornerRadius = 12.dp,
        pillCornerRadius = 10.dp,
        heroWashStart = heroSolid,
        heroWashEnd = heroSolid,
        chartStart = pickColor(isDark, Color(0xFF60A5FA), Color(0xFF1D4ED8)),
        chartEnd = pickColor(isDark, Color(0xFF93C5FD), Color(0xFF3B82F6)),
        useGradientFill = false
    )
}

private fun buildMatrixTokens(
    isDark: Boolean,
    gradientMode: GradientMode,
    category: CalculatorCategory
): CalcHubDesignTokens {
    val accent = pickColor(isDark, Color(0xFFFF2E2E), Color(0xFFD92020))
    val heroSolid = pickColor(isDark, Color(0xFF0F1014), Color(0xFFFAF9F5))
    val chartG = pickColor(isDark, Color(0xFF22C55E), Color(0xFF15803D))
    return CalcHubDesignTokens(
        skin = CalcHubSkin.MATRIX,
        isDark = isDark,
        gradientMode = gradientMode,
        activeCategory = category,
        appBackground = pickColor(isDark, Color(0xFF050506), Color(0xFFEFECE6)),
        surfaceLowest = pickColor(isDark, Color(0xFF0E0F12), Color(0xFFFAF9F5)),
        surfaceLow = pickColor(isDark, Color(0xFF131519), Color(0xFFF4F2EC)),
        surfaceContainer = pickColor(isDark, Color(0xFF191C22), Color(0xFFE6E3DB)),
        surfaceHigh = pickColor(isDark, Color(0xFF232730), Color(0xFFDAD6CC)),
        surfaceToolbar = pickColor(isDark, Color(0xFF0E0F12), Color(0xFFFAF9F5)),
        inkPrimary = pickColor(isDark, Color(0xFFF5F6F8), Color(0xFF121212)),
        inkSecondary = pickColor(isDark, Color(0xFFA6ACB8), Color(0xFF4B4B4B)),
        inkMuted = pickColor(isDark, Color(0xFF848B99), Color(0xFF636363)),
        accentPrimary = accent,
        onAccent = Color(0xFFFFFFFF),
        accentContainer = pickColor(isDark, Color(0xFF3F1114), Color(0xFFFEE2E2)),
        onAccentContainer = pickColor(isDark, Color(0xFFFECACA), Color(0xFF7F1D1D)),
        gainInk = chartG,
        gainBg = pickColor(isDark, Color(0xFF0C2E1B), Color(0xFFDCFCE7)),
        lossInk = pickColor(isDark, Color(0xFFF87171), Color(0xFFB91C1C)),
        lossBg = pickColor(isDark, Color(0xFF3B1214), Color(0xFFFEE2E2)),
        cardBorder = pickColor(isDark, Color(0x21FFFFFF), Color(0x29121212)),
        trackBg = pickColor(isDark, Color(0xFF262A33), Color(0xFFD5D1C7)),
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
