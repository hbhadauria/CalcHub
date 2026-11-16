package com.hsb.calchub.ui.theme

import androidx.compose.ui.graphics.Color
import com.hsb.calchub.domain.model.CalculatorCategory

/**
 * User-selectable visual themes for CalcHub (Decision D2).
 */
enum class CalcHubSkin(val title: String, val subtitle: String) {
    AURORA("Aurora", "Vibrant colors with smooth modern cards"),
    LEDGER("Ledger Paper", "Warm paper tones with classic print styling"),
    MATRIX("Dot Matrix", "Retro digital display with high contrast")
}

/**
 * Light / Dark / System polarity preference.
 */
enum class ThemePolarity(val label: String) {
    SYSTEM("System"),
    LIGHT("Light Mode"),
    DARK("Dark Mode")
}

/**
 * Optional gradient color setting (User Request #1 & #5).
 * [AUTO_BY_SCREEN] derives each calculator screen's gradient from its category.
 */
enum class GradientMode(val label: String) {
    AUTO_BY_SCREEN("By Screen"),
    AURORA("Aurora"),
    SUNSET("Sunset"),
    EMERALD("Emerald"),
    OCEAN("Ocean"),
    SOLID_OFF("Solid Off")
}

/**
 * Category accent pair + tonal hero wash + deep AA-contrasting icon orb gradient.
 */
data class CategoryAccentPalette(
    val category: CalculatorCategory,
    val label: String,
    val gradStart: Color,
    val gradEnd: Color,
    val badgeGradStart: Color,
    val badgeGradEnd: Color,
    val lightWashStart: Color,
    val lightWashEnd: Color,
    val darkWashStart: Color,
    val darkWashEnd: Color
)

object CategoryPalettes {
    val All = CategoryAccentPalette(
        category = CalculatorCategory.POPULAR,
        label = "All 30+",
        gradStart = Color(0xFF475569),
        gradEnd = Color(0xFF1E293B),
        badgeGradStart = Color(0xFF334155),
        badgeGradEnd = Color(0xFF0F172A),
        lightWashStart = Color(0xFFDCF8F3),
        lightWashEnd = Color(0xFFECE6FE),
        darkWashStart = Color(0xFF0E2F2E),
        darkWashEnd = Color(0xFF231844)
    )

    val Investment = CategoryAccentPalette(
        category = CalculatorCategory.INVESTMENT,
        label = "Investment",
        gradStart = Color(0xFF0D9488),
        gradEnd = Color(0xFF7C3AED),
        badgeGradStart = Color(0xFF0F766E),
        badgeGradEnd = Color(0xFF6D28D9),
        lightWashStart = Color(0xFFDCF8F3),
        lightWashEnd = Color(0xFFECE6FE),
        darkWashStart = Color(0xFF0E2F2E),
        darkWashEnd = Color(0xFF231844)
    )

    val Loan = CategoryAccentPalette(
        category = CalculatorCategory.LOAN,
        label = "Loan",
        gradStart = Color(0xFFD97706),
        gradEnd = Color(0xFFE11D48),
        badgeGradStart = Color(0xFFC2410C),
        badgeGradEnd = Color(0xFFBE123C),
        lightWashStart = Color(0xFFFEF1D6),
        lightWashEnd = Color(0xFFFDE4EA),
        darkWashStart = Color(0xFF32210B),
        darkWashEnd = Color(0xFF3B1221)
    )

    val Tax = CategoryAccentPalette(
        category = CalculatorCategory.TAX,
        label = "Tax",
        gradStart = Color(0xFF0284C7),
        gradEnd = Color(0xFF4F46E5),
        badgeGradStart = Color(0xFF0369A1),
        badgeGradEnd = Color(0xFF4338CA),
        lightWashStart = Color(0xFFDFF2FE),
        lightWashEnd = Color(0xFFE7E7FE),
        darkWashStart = Color(0xFF0C283C),
        darkWashEnd = Color(0xFF1C1C48)
    )

    val Retirement = CategoryAccentPalette(
        category = CalculatorCategory.RETIREMENT,
        label = "Retirement",
        gradStart = Color(0xFFEA580C),
        gradEnd = Color(0xFFDB2777),
        badgeGradStart = Color(0xFFC2410C),
        badgeGradEnd = Color(0xFFBE185D),
        lightWashStart = Color(0xFFFFECDC),
        lightWashEnd = Color(0xFFFCE3F0),
        darkWashStart = Color(0xFF361A0C),
        darkWashEnd = Color(0xFF3C112B)
    )

    val Trading = CategoryAccentPalette(
        category = CalculatorCategory.TRADING,
        label = "Trading",
        gradStart = Color(0xFF65A30D),
        gradEnd = Color(0xFF059669),
        badgeGradStart = Color(0xFF3F6212),
        badgeGradEnd = Color(0xFF047857),
        lightWashStart = Color(0xFFECF8CF),
        lightWashEnd = Color(0xFFD9F7EA),
        darkWashStart = Color(0xFF1B2C0D),
        darkWashEnd = Color(0xFF0B2E22)
    )

    val Other = CategoryAccentPalette(
        category = CalculatorCategory.OTHER,
        label = "Other",
        gradStart = Color(0xFF0891B2),
        gradEnd = Color(0xFF2563EB),
        badgeGradStart = Color(0xFF0E7490),
        badgeGradEnd = Color(0xFF1D4ED8),
        lightWashStart = Color(0xFFD7F4FA),
        lightWashEnd = Color(0xFFE0EAFE),
        darkWashStart = Color(0xFF0B2A36),
        darkWashEnd = Color(0xFF142248)
    )

    fun forCategory(category: CalculatorCategory): CategoryAccentPalette = when (category) {
        CalculatorCategory.POPULAR -> All
        CalculatorCategory.INVESTMENT -> Investment
        CalculatorCategory.LOAN -> Loan
        CalculatorCategory.TAX -> Tax
        CalculatorCategory.RETIREMENT -> Retirement
        CalculatorCategory.TRADING -> Trading
        CalculatorCategory.OTHER -> Other
    }

    fun resolveForScreen(
        screenCategory: CalculatorCategory,
        gradientMode: GradientMode
    ): CategoryAccentPalette = when (gradientMode) {
        GradientMode.AUTO_BY_SCREEN -> forCategory(screenCategory)
        GradientMode.AURORA -> Investment
        GradientMode.SUNSET -> CategoryAccentPalette(
            category = screenCategory,
            label = "Sunset",
            gradStart = Color(0xFFEA580C),
            gradEnd = Color(0xFFE11D48),
            badgeGradStart = Color(0xFFC2410C),
            badgeGradEnd = Color(0xFFBE123C),
            lightWashStart = Color(0xFFFFECD6),
            lightWashEnd = Color(0xFFFDE2E8),
            darkWashStart = Color(0xFF381B0B),
            darkWashEnd = Color(0xFF3C1121)
        )
        GradientMode.EMERALD -> CategoryAccentPalette(
            category = screenCategory,
            label = "Emerald",
            gradStart = Color(0xFF059669),
            gradEnd = Color(0xFF10B981),
            badgeGradStart = Color(0xFF047857),
            badgeGradEnd = Color(0xFF059669),
            lightWashStart = Color(0xFFD9F7EA),
            lightWashEnd = Color(0xFFE6FAEE),
            darkWashStart = Color(0xFF0A2E22),
            darkWashEnd = Color(0xFF0E3424)
        )
        GradientMode.OCEAN -> Tax
        GradientMode.SOLID_OFF -> forCategory(screenCategory)
    }
}

// Semantic Gain / Loss Tokens
val AuroraGainLight = Color(0xFF047857)
val AuroraGainBgLight = Color(0xFFD1FAE5)
val AuroraGainDark = Color(0xFF34D399)
val AuroraGainBgDark = Color(0xFF0D3528)
val AuroraLossLight = Color(0xFFBE123C)
val AuroraLossBgLight = Color(0xFFFFE4E6)
val AuroraLossDark = Color(0xFFFDA4AF)
val AuroraLossBgDark = Color(0xFF3D1420)

val LedgerHighlightLight = Color(0x5CFACC15)
val LedgerHighlightDark = Color(0x3360A5FA)

// Legacy aliases retained so untouched calculator screens continue compiling cleanly
val NeonGreen = Color(0xFF047857)
val NeonPink = Color(0xFF7C3AED)
val NeonCyan = Color(0xFF0284C7)
val NeonBackground = Color(0xFFF8FAFE)
val NeonSurface = Color(0xFFFFFFFF)
val NeonText = Color(0xFF0B1020)
val NeonTextSecondary = Color(0xFF434B61)
val NeonBorder = Color(0x140F172A)
val NeonInactiveTrack = Color(0xFFDCE3F0)
val NeoWhite = Color(0xFFFFFFFF)
val NeoBlack = Color(0xFF0B1020)
val NeoGray = Color(0xFFEAEFF8)
