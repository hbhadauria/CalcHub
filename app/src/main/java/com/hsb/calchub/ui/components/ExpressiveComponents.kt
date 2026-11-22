package com.hsb.calchub.ui.components

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Matrix
import android.graphics.Path as AndroidPath
import android.net.Uri
import android.os.Build
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Contrast
import androidx.compose.material.icons.rounded.Gradient
import androidx.compose.material.icons.rounded.Grid4x4
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.StarRate
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.Morph
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.circle
import androidx.graphics.shapes.pillStar
import androidx.graphics.shapes.rectangle
import androidx.graphics.shapes.star
import androidx.graphics.shapes.toPath
import com.hsb.calchub.data.repository.ThemePreferences
import com.hsb.calchub.domain.model.CalculatorCategory
import com.hsb.calchub.ui.theme.CalcHubDesignTokens
import com.hsb.calchub.ui.theme.CalcHubSkin
import com.hsb.calchub.ui.theme.CategoryAccentPalette
import com.hsb.calchub.ui.theme.CategoryPalettes
import com.hsb.calchub.ui.theme.GradientMode
import com.hsb.calchub.ui.theme.LocalCalcHubTokens
import com.hsb.calchub.ui.theme.ThemePolarity

/**
 * Distinct expressive polygon silhouettes assigned to each financial category orb.
 */
enum class PolygonShapeType {
    CLOVER,      // All Calculators (12-lobe organic badge)
    FLOWER_4,    // Investment (4-petal growth star)
    SQUIRCLE,    // Loan (structural rounded quad)
    HEXAGON,     // Retirement (6-sided longevity prism)
    DIAMOND,     // Tax (tilted fiscal shield)
    STARBURST,   // Trading (6-point momentum burst)
    CIRCLE       // Other (balanced utility orb)
}

fun shapeTypeForCategory(category: CalculatorCategory?): PolygonShapeType = when (category) {
    null, CalculatorCategory.POPULAR -> PolygonShapeType.CLOVER
    CalculatorCategory.INVESTMENT -> PolygonShapeType.FLOWER_4
    CalculatorCategory.LOAN -> PolygonShapeType.SQUIRCLE
    CalculatorCategory.RETIREMENT -> PolygonShapeType.HEXAGON
    CalculatorCategory.TAX -> PolygonShapeType.DIAMOND
    CalculatorCategory.TRADING -> PolygonShapeType.STARBURST
    CalculatorCategory.OTHER -> PolygonShapeType.CIRCLE
}

private fun buildNormalizedPolygon(type: PolygonShapeType, selected: Boolean): RoundedPolygon {
    if (selected) {
        return RoundedPolygon.pillStar(
            width = 2f,
            height = 2f,
            numVerticesPerRadius = 6,
            innerRadiusRatio = 0.88f,
            rounding = CornerRounding(radius = 0.28f, smoothing = 0.8f)
        ).normalized()
    }
    return when (type) {
        PolygonShapeType.CLOVER -> RoundedPolygon.pillStar(
            width = 2f,
            height = 2f,
            numVerticesPerRadius = 8,
            innerRadiusRatio = 0.84f,
            rounding = CornerRounding(radius = 0.22f, smoothing = 0.85f)
        )
        PolygonShapeType.FLOWER_4 -> RoundedPolygon.star(
            numVerticesPerRadius = 4,
            radius = 1f,
            innerRadius = 0.72f,
            rounding = CornerRounding(radius = 0.35f, smoothing = 0.9f),
            innerRounding = CornerRounding(radius = 0.28f, smoothing = 0.9f)
        )
        PolygonShapeType.SQUIRCLE -> RoundedPolygon.rectangle(
            width = 1.9f,
            height = 1.9f,
            rounding = CornerRounding(radius = 0.45f, smoothing = 0.8f)
        )
        PolygonShapeType.HEXAGON -> RoundedPolygon(
            numVertices = 6,
            radius = 1f,
            rounding = CornerRounding(radius = 0.32f, smoothing = 0.7f)
        )
        PolygonShapeType.DIAMOND -> RoundedPolygon(
            numVertices = 4,
            radius = 1f,
            rounding = CornerRounding(radius = 0.28f, smoothing = 0.75f)
        )
        PolygonShapeType.STARBURST -> RoundedPolygon.star(
            numVerticesPerRadius = 6,
            radius = 1f,
            innerRadius = 0.74f,
            rounding = CornerRounding(radius = 0.24f, smoothing = 0.8f),
            innerRounding = CornerRounding(radius = 0.20f, smoothing = 0.8f)
        )
        PolygonShapeType.CIRCLE -> RoundedPolygon.circle(
            numVertices = 10,
            radius = 1f
        )
    }.normalized()
}

private class MorphPolygonShape(
    private val morph: Morph,
    private val progress: Float
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val androidPath = AndroidPath()
        morph.toPath(progress.coerceIn(0f, 1f), androidPath)
        val matrix = Matrix().apply {
            setScale(size.width, size.height)
        }
        androidPath.transform(matrix)
        return Outline.Generic(androidPath.asComposePath())
    }
}

/**
 * Category selector orb with M3 Expressive polygon morphing, high-contrast vector iconography,
 * and an unobtrusive bottom-right count pill.
 */
@Composable
fun MorphingPolygonBadge(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    shapeType: PolygonShapeType,
    accentPalette: CategoryAccentPalette,
    count: Int? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = LocalCalcHubTokens.current
    val haptic = LocalHapticFeedback.current

    val idlePoly = remember(shapeType) { buildNormalizedPolygon(shapeType, selected = false) }
    val activePoly = remember(shapeType) { buildNormalizedPolygon(shapeType, selected = true) }
    val morph = remember(idlePoly, activePoly) { Morph(idlePoly, activePoly) }

    val morphProgress by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = spring(
            dampingRatio = 0.65f,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "polygonMorph"
    )

    val isMinimalBlack = tokens.skin == CalcHubSkin.MINIMAL_BLACK
    val idleContainer = if (tokens.skin == CalcHubSkin.MATRIX || isMinimalBlack) {
        tokens.surfaceLow
    } else if (tokens.isDark) {
        accentPalette.darkWashStart
    } else {
        accentPalette.lightWashStart
    }

    val containerBrush = when {
        isMinimalBlack && selected -> SolidColor(tokens.accentPrimary)
        selected -> Brush.linearGradient(listOf(accentPalette.badgeGradStart, accentPalette.badgeGradEnd))
        else -> SolidColor(idleContainer)
    }

    val iconTint by animateColorAsState(
        targetValue = when {
            isMinimalBlack && selected -> tokens.onAccent
            isMinimalBlack -> tokens.inkSecondary
            selected -> Color.White
            tokens.isDark -> lerp(accentPalette.gradStart, Color.White, 0.42f)
            else -> accentPalette.badgeGradStart
        },
        animationSpec = tween(220),
        label = "orbIcon"
    )

    val borderColor by animateColorAsState(
        targetValue = when {
            isMinimalBlack && selected -> tokens.accentPrimary
            isMinimalBlack -> tokens.cardBorder
            selected -> accentPalette.badgeGradEnd
            else -> accentPalette.gradStart.copy(alpha = if (tokens.isDark) 0.45f else 0.35f)
        },
        animationSpec = tween(220),
        label = "orbBorder"
    )

    val shape: Shape = remember(tokens.skin, morph, morphProgress) {
        when (tokens.skin) {
            CalcHubSkin.AURORA -> MorphPolygonShape(morph, morphProgress)
            CalcHubSkin.LEDGER -> RoundedCornerShape(14.dp)
            CalcHubSkin.MATRIX -> RoundedCornerShape(8.dp)
            CalcHubSkin.MINIMAL_BLACK -> RoundedCornerShape(12.dp)
        }
    }

    val labelAccent = when {
        isMinimalBlack -> tokens.inkPrimary
        tokens.isDark -> lerp(accentPalette.gradStart, Color.White, 0.35f)
        else -> accentPalette.badgeGradStart
    }
    val countLabel = count?.let { if (it > 40) "40+" else it.toString() }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(
                role = Role.Tab,
                onClickLabel = "$label category"
            ) {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick()
            }
            .semantics {
                this.selected = selected
                this.contentDescription = if (countLabel != null) "$label, $countLabel calculators" else label
            }
            .padding(horizontal = 4.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(62.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(shape)
                    .background(brush = containerBrush)
                    .border(
                        width = if (selected) 1.5.dp else 1.dp,
                        color = borderColor,
                        shape = shape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(25.dp)
                )
            }

            if (countLabel != null) {
                Surface(
                    modifier = Modifier.align(Alignment.BottomEnd),
                    shape = RoundedCornerShape(999.dp),
                    color = if (selected) tokens.inkPrimary else tokens.surfaceLowest,
                    border = BorderStroke(1.dp, borderColor)
                ) {
                    Text(
                        text = countLabel,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFeatureSettings = "tnum"
                        ),
                        color = if (selected) tokens.appBackground else tokens.inkSecondary,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
            ),
            color = if (selected) labelAccent else tokens.inkSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * Multi-option segmented pill selector with consistent uniform rounded corners on both ends
 * for every option (both selected and unselected).
 */
@Composable
fun <T> SegmentedPillGroup(
    options: List<T>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit,
    labelProvider: (T) -> String,
    modifier: Modifier = Modifier,
    accentColor: Color = LocalCalcHubTokens.current.accentPrimary,
    onAccentColor: Color = LocalCalcHubTokens.current.onAccent,
    compact: Boolean = false
) {
    val tokens = LocalCalcHubTokens.current
    val haptic = LocalHapticFeedback.current
    val outerCorner = if (tokens.skin == CalcHubSkin.MATRIX) 10.dp else 20.dp
    val segmentCorner = if (tokens.skin == CalcHubSkin.MATRIX) 8.dp else 16.dp

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(outerCorner),
        color = tokens.surfaceContainer,
        border = BorderStroke(1.dp, tokens.cardBorder)
    ) {
        Row(
            modifier = Modifier.padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            options.forEach { option ->
                val isSelected = option == selectedOption
                val pillBg by animateColorAsState(
                    targetValue = if (isSelected) accentColor else Color.Transparent,
                    animationSpec = tween(180),
                    label = "segmentBg"
                )
                val labelColor by animateColorAsState(
                    targetValue = if (isSelected) onAccentColor else tokens.inkSecondary,
                    animationSpec = tween(180),
                    label = "segmentFg"
                )

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(segmentCorner))
                        .semantics { this.selected = isSelected }
                        .clickable(role = Role.Tab) {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onOptionSelected(option)
                        },
                    shape = RoundedCornerShape(segmentCorner),
                    color = pillBg
                ) {
                    Box(
                        modifier = Modifier.padding(
                            horizontal = if (compact) 6.dp else 10.dp,
                            vertical = if (compact) 8.dp else 10.dp
                        ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = labelProvider(option),
                            style = (if (compact) {
                                MaterialTheme.typography.labelMedium
                            } else {
                                MaterialTheme.typography.labelLarge
                            }).copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontFeatureSettings = "tnum"
                            ),
                            color = labelColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

/**
 * Skin-adaptive numeric hero readout:
 * - [CalcHubSkin.AURORA]: tabular animated currency/metric readout.
 * - [CalcHubSkin.LEDGER]: editorial ink numerals with optional marker highlight sweep.
 * - [CalcHubSkin.MATRIX]: optional crisp 5x7 dot-matrix LED grid readout for hero numbers.
 */
@Composable
fun OdometerText(
    text: String,
    style: TextStyle,
    color: Color,
    modifier: Modifier = Modifier,
    highlightLedger: Boolean = false,
    useDotMatrixInMatrixSkin: Boolean = false
) {
    val tokens = LocalCalcHubTokens.current

    if (useDotMatrixInMatrixSkin && tokens.skin == CalcHubSkin.MATRIX) {
        DotMatrixHeroNumber(
            text = text,
            activeDotColor = tokens.accentPrimary,
            dimDotColor = tokens.trackBg,
            modifier = modifier
        )
        return
    }

    AnimatedContent(
        targetState = text,
        transitionSpec = {
            fadeIn(animationSpec = tween(140)) togetherWith fadeOut(animationSpec = tween(100))
        },
        modifier = modifier,
        label = "odometerFade"
    ) { value ->
        if (highlightLedger && tokens.skin == CalcHubSkin.LEDGER && tokens.highlightBg != Color.Transparent) {
            Box(
                modifier = Modifier
                    .background(color = tokens.highlightBg, shape = RoundedCornerShape(6.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = value,
                    style = style.copy(fontFeatureSettings = "tnum"),
                    color = color,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        } else {
            Text(
                text = value,
                style = style.copy(fontFeatureSettings = "tnum"),
                color = color,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * 5x7 dot-matrix LED character renderer for the Matrix terminal skin.
 */
@Composable
fun DotMatrixHeroNumber(
    text: String,
    activeDotColor: Color,
    dimDotColor: Color,
    modifier: Modifier = Modifier,
    height: Dp = 36.dp
) {
    val display = remember(text) { text.take(14) }
    Canvas(
        modifier = modifier
            .height(height)
            .semantics { contentDescription = text }
    ) {
        if (display.isEmpty()) return@Canvas
        val rows = 7
        val colsPerChar = 5
        val gapCols = 1
        val totalCols = display.length * colsPerChar + (display.length - 1).coerceAtLeast(0) * gapCols
        val cellByHeight = size.height / rows
        val cellByWidth = size.width / totalCols.coerceAtLeast(1)
        val step = minOf(cellByHeight, cellByWidth).coerceAtLeast(1.8f)
        val dotRadius = step * 0.36f

        var cursorX = 0f
        display.forEach { ch ->
            val glyph = DOT_MATRIX_GLYPHS[ch] ?: DOT_MATRIX_GLYPHS[' ']!!
            for (r in 0 until rows) {
                val mask = glyph[r]
                for (c in 0 until colsPerChar) {
                    val lit = ((mask shr (colsPerChar - 1 - c)) and 1) == 1
                    val cx = cursorX + c * step + step * 0.5f
                    val cy = r * step + step * 0.5f
                    drawCircle(
                        color = if (lit) activeDotColor else dimDotColor.copy(alpha = 0.45f),
                        radius = dotRadius,
                        center = Offset(cx, cy)
                    )
                }
            }
            cursorX += (colsPerChar + gapCols) * step
        }
    }
}

private val DOT_MATRIX_GLYPHS: Map<Char, IntArray> = mapOf(
    '0' to intArrayOf(0b01110, 0b10001, 0b10011, 0b10101, 0b11001, 0b10001, 0b01110),
    '1' to intArrayOf(0b00100, 0b01100, 0b00100, 0b00100, 0b00100, 0b00100, 0b01110),
    '2' to intArrayOf(0b01110, 0b10001, 0b00001, 0b00110, 0b01000, 0b10000, 0b11111),
    '3' to intArrayOf(0b11110, 0b00001, 0b00001, 0b01110, 0b00001, 0b00001, 0b11110),
    '4' to intArrayOf(0b00010, 0b00110, 0b01010, 0b10010, 0b11111, 0b00010, 0b00010),
    '5' to intArrayOf(0b11111, 0b10000, 0b11110, 0b00001, 0b00001, 0b10001, 0b01110),
    '6' to intArrayOf(0b00110, 0b01000, 0b10000, 0b11110, 0b10001, 0b10001, 0b01110),
    '7' to intArrayOf(0b11111, 0b00001, 0b00010, 0b00100, 0b01000, 0b01000, 0b01000),
    '8' to intArrayOf(0b01110, 0b10001, 0b10001, 0b01110, 0b10001, 0b10001, 0b01110),
    '9' to intArrayOf(0b01110, 0b10001, 0b10001, 0b01111, 0b00001, 0b00010, 0b01100),
    '₹' to intArrayOf(0b11111, 0b00100, 0b11111, 0b01100, 0b01000, 0b00100, 0b00011),
    ',' to intArrayOf(0b00000, 0b00000, 0b00000, 0b00000, 0b00100, 0b00100, 0b01000),
    '.' to intArrayOf(0b00000, 0b00000, 0b00000, 0b00000, 0b00000, 0b01100, 0b01100),
    '%' to intArrayOf(0b11001, 0b11010, 0b00010, 0b00100, 0b01000, 0b01011, 0b10011),
    '+' to intArrayOf(0b00000, 0b00100, 0b00100, 0b11111, 0b00100, 0b00100, 0b00000),
    '-' to intArrayOf(0b00000, 0b00000, 0b00000, 0b11111, 0b00000, 0b00000, 0b00000),
    'L' to intArrayOf(0b10000, 0b10000, 0b10000, 0b10000, 0b10000, 0b10000, 0b11111),
    'a' to intArrayOf(0b00000, 0b00000, 0b01110, 0b00001, 0b01111, 0b10001, 0b01111),
    'C' to intArrayOf(0b01110, 0b10001, 0b10000, 0b10000, 0b10000, 0b10001, 0b01110),
    'r' to intArrayOf(0b00000, 0b00000, 0b10110, 0b11001, 0b10000, 0b10000, 0b10000),
    'K' to intArrayOf(0b10001, 0b10010, 0b10100, 0b11000, 0b10100, 0b10010, 0b10001),
    'M' to intArrayOf(0b10001, 0b11011, 0b10101, 0b10101, 0b10001, 0b10001, 0b10001),
    'x' to intArrayOf(0b00000, 0b00000, 0b10001, 0b01010, 0b00100, 0b01010, 0b10001),
    ' ' to intArrayOf(0b00000, 0b00000, 0b00000, 0b00000, 0b00000, 0b00000, 0b00000)
)

/**
 * Applies solid tonal background plus optional subtle screen-scoped or user-selected gradient wash.
 * Never uses blur/glassmorphism filters.
 */
fun Modifier.calcHubScreenBackdrop(
    tokens: CalcHubDesignTokens
): Modifier {
    if (!tokens.useGradientFill || tokens.gradientMode == GradientMode.SOLID_OFF || tokens.skin != CalcHubSkin.AURORA) {
        return background(tokens.appBackground)
    }
    return background(
        brush = Brush.verticalGradient(
            0f to tokens.heroWashStart.copy(alpha = if (tokens.isDark) 0.52f else 0.72f),
            0.22f to tokens.heroWashEnd.copy(alpha = if (tokens.isDark) 0.22f else 0.30f),
            0.46f to tokens.appBackground,
            1f to tokens.appBackground
        )
    )
}

/**
 * Modal bottom sheet allowing instant customization of Design Skin, Polarity (Light/Dark/System),
 * and Screen Gradient mode (`By Screen` category-reactive or fixed preset / off).
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AppearanceThemeSheet(
    preferences: ThemePreferences,
    onSelectSkin: (CalcHubSkin) -> Unit,
    onSelectPolarity: (ThemePolarity) -> Unit,
    onSelectGradientMode: (GradientMode) -> Unit,
    onOpenPrivacyInfo: () -> Unit,
    onOpenAboutInfo: () -> Unit = {},
    onDismiss: () -> Unit
) {
    val tokens = LocalCalcHubTokens.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = tokens.surfaceLowest,
        contentColor = tokens.inkPrimary
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Palette,
                        contentDescription = null,
                        tint = tokens.accentPrimary
                    )
                    Column {
                        Text(
                            text = "Appearance & Theme",
                            style = MaterialTheme.typography.titleLarge,
                            color = tokens.inkPrimary
                        )
                        Text(
                            text = "Choose your preferred style and color mode",
                            style = MaterialTheme.typography.bodySmall,
                            color = tokens.inkSecondary
                        )
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Close theme sheet",
                        tint = tokens.inkSecondary
                    )
                }
            }

            Text(
                text = "STYLE",
                style = MaterialTheme.typography.labelSmall,
                color = tokens.inkSecondary
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SkinOptionCard(
                    title = CalcHubSkin.AURORA.title,
                    subtitle = CalcHubSkin.AURORA.subtitle,
                    icon = Icons.Rounded.AutoAwesome,
                    swatchColors = listOf(Color(0xFF0B0E17), Color(0xFF0D9488), Color(0xFF7C3AED)),
                    selected = preferences.skin == CalcHubSkin.AURORA,
                    onClick = { onSelectSkin(CalcHubSkin.AURORA) }
                )
                SkinOptionCard(
                    title = CalcHubSkin.LEDGER.title,
                    subtitle = CalcHubSkin.LEDGER.subtitle,
                    icon = Icons.AutoMirrored.Rounded.MenuBook,
                    swatchColors = listOf(Color(0xFFFAF7F0), Color(0xFFFACC15), Color(0xFF1D4ED8)),
                    selected = preferences.skin == CalcHubSkin.LEDGER,
                    onClick = { onSelectSkin(CalcHubSkin.LEDGER) }
                )
                SkinOptionCard(
                    title = CalcHubSkin.MATRIX.title,
                    subtitle = CalcHubSkin.MATRIX.subtitle,
                    icon = Icons.Rounded.Grid4x4,
                    swatchColors = listOf(Color(0xFF0E0F12), Color(0xFFFF2E2E), Color(0xFF22C55E)),
                    selected = preferences.skin == CalcHubSkin.MATRIX,
                    onClick = { onSelectSkin(CalcHubSkin.MATRIX) }
                )
                SkinOptionCard(
                    title = CalcHubSkin.MINIMAL_BLACK.title,
                    subtitle = CalcHubSkin.MINIMAL_BLACK.subtitle,
                    icon = Icons.Rounded.Contrast,
                    swatchColors = listOf(Color(0xFF000000), Color(0xFF27272A), Color(0xFFFFFFFF)),
                    selected = preferences.skin == CalcHubSkin.MINIMAL_BLACK,
                    onClick = { onSelectSkin(CalcHubSkin.MINIMAL_BLACK) }
                )
            }

            Text(
                text = "COLOR MODE",
                style = MaterialTheme.typography.labelSmall,
                color = tokens.inkSecondary
            )

            SegmentedPillGroup(
                options = ThemePolarity.entries,
                selectedOption = preferences.polarity,
                onOptionSelected = onSelectPolarity,
                labelProvider = { polarity ->
                    when (polarity) {
                        ThemePolarity.SYSTEM -> "System"
                        ThemePolarity.LIGHT -> "Light"
                        ThemePolarity.DARK -> "Dark"
                    }
                },
                accentColor = tokens.accentPrimary,
                onAccentColor = tokens.onAccent,
                modifier = Modifier.fillMaxWidth()
            )

            if (preferences.skin == CalcHubSkin.AURORA) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Gradient,
                            contentDescription = null,
                            tint = tokens.accentPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "BACKGROUND GRADIENT",
                            style = MaterialTheme.typography.labelSmall,
                            color = tokens.inkSecondary
                        )
                    }
                    Text(
                        text = "Matches each calculator",
                        style = MaterialTheme.typography.labelSmall,
                        color = tokens.accentPrimary
                    )
                }

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    GradientMode.entries.forEach { mode ->
                        val isSelected = preferences.gradientMode == mode
                        val previewColors = when (mode) {
                            GradientMode.AUTO_BY_SCREEN -> listOf(tokens.chartStart, tokens.chartEnd)
                            GradientMode.AURORA -> listOf(CategoryPalettes.Investment.gradStart, CategoryPalettes.Investment.gradEnd)
                            GradientMode.SUNSET -> listOf(Color(0xFFEA580C), Color(0xFFE11D48))
                            GradientMode.EMERALD -> listOf(Color(0xFF059669), Color(0xFF10B981))
                            GradientMode.OCEAN -> listOf(CategoryPalettes.Tax.gradStart, CategoryPalettes.Tax.gradEnd)
                            GradientMode.SOLID_OFF -> listOf(tokens.surfaceContainer, tokens.cardBorder)
                        }
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { onSelectGradientMode(mode) },
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) tokens.accentContainer else tokens.surfaceLow,
                            border = BorderStroke(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) tokens.accentPrimary else tokens.cardBorder
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(Brush.linearGradient(previewColors))
                                        .border(1.dp, tokens.cardBorder, CircleShape)
                                )
                                Text(
                                    text = mode.label,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    ),
                                    color = if (isSelected) tokens.onAccentContainer else tokens.inkPrimary
                                )
                            }
                        }
                    }
                }
            } else {
                Text(
                    text = "Background gradients apply to Aurora style.",
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.inkMuted
                )
            }

            HorizontalDivider(color = tokens.cardBorder)

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(tokens.cardCornerRadius))
                    .clickable(onClick = onOpenPrivacyInfo),
                shape = RoundedCornerShape(tokens.cardCornerRadius),
                color = tokens.surfaceLow,
                border = BorderStroke(1.dp, tokens.cardBorder)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.VerifiedUser,
                        contentDescription = null,
                        tint = tokens.gainInk
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Private & Works Offline",
                            style = MaterialTheme.typography.titleSmall,
                            color = tokens.inkPrimary
                        )
                        Text(
                            text = "All calculations and saved plans stay on your phone",
                            style = MaterialTheme.typography.bodySmall,
                            color = tokens.inkSecondary
                        )
                    }
                }
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(tokens.cardCornerRadius))
                    .clickable(onClick = onOpenAboutInfo),
                shape = RoundedCornerShape(tokens.cardCornerRadius),
                color = tokens.surfaceLow,
                border = BorderStroke(1.dp, tokens.cardBorder)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Info,
                        contentDescription = null,
                        tint = tokens.accentPrimary
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "About CalcHub",
                            style = MaterialTheme.typography.titleSmall,
                            color = tokens.inkPrimary
                        )
                        Text(
                            text = "Version, disclaimer & rate app",
                            style = MaterialTheme.typography.bodySmall,
                            color = tokens.inkSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SkinOptionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    swatchColors: List<Color>,
    selected: Boolean,
    onClick: () -> Unit
) {
    val tokens = LocalCalcHubTokens.current
    val borderColor by animateColorAsState(
        targetValue = if (selected) tokens.accentPrimary else tokens.cardBorder,
        animationSpec = tween(180),
        label = "skinBorder"
    )
    val bgColor by animateColorAsState(
        targetValue = if (selected) tokens.accentContainer.copy(alpha = 0.45f) else tokens.surfaceLow,
        animationSpec = tween(180),
        label = "skinBg"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(tokens.cardCornerRadius))
            .clickable(role = Role.RadioButton, onClick = onClick),
        shape = RoundedCornerShape(tokens.cardCornerRadius),
        color = bgColor,
        border = BorderStroke(if (selected) 1.5.dp else 1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                modifier = Modifier.size(42.dp),
                shape = RoundedCornerShape(12.dp),
                color = if (selected) tokens.accentPrimary else tokens.surfaceLowest,
                border = BorderStroke(1.dp, tokens.cardBorder)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (selected) tokens.onAccent else tokens.accentPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = tokens.inkPrimary
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.inkSecondary,
                    maxLines = 2
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                swatchColors.forEach { swatch ->
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(swatch)
                            .border(1.dp, tokens.cardBorder, CircleShape)
                    )
                }
            }

            if (selected) {
                Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = "Selected skin",
                    tint = tokens.accentPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/**
 * Explicit local-first privacy transparency dialog triggered from the badge or theme sheet.
 */
@Composable
fun OfflinePrivacyDialog(
    onDismiss: () -> Unit
) {
    val tokens = LocalCalcHubTokens.current

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = tokens.surfaceLowest,
        titleContentColor = tokens.inkPrimary,
        textContentColor = tokens.inkSecondary,
        icon = {
            Icon(
                imageVector = Icons.Rounded.Security,
                contentDescription = null,
                tint = tokens.accentPrimary
            )
        },
        title = {
            Text(
                text = "Private & Works Offline",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                PrivacyBulletRow(
                    title = "No Internet Required",
                    detail = "Every calculator works 100% offline, anytime."
                )
                PrivacyBulletRow(
                    title = "No Tracking or Ads",
                    detail = "CalcHub never collects personal data, usage history, or analytics."
                )
                PrivacyBulletRow(
                    title = "Saved on Your Phone",
                    detail = "Your pinned calculators, theme, and saved plans stay privately on your device."
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Got it",
                    color = tokens.accentPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    )
}

@Composable
private fun PrivacyBulletRow(title: String, detail: String) {
    val tokens = LocalCalcHubTokens.current
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Rounded.CheckCircle,
            contentDescription = null,
            tint = tokens.gainInk,
            modifier = Modifier
                .padding(top = 2.dp)
                .size(16.dp)
        )
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = tokens.inkPrimary
            )
            Text(
                text = detail,
                style = MaterialTheme.typography.bodySmall,
                color = tokens.inkSecondary
            )
        }
    }
}

/**
 * Compact About dialog showing dynamic app version, offline MIT summary,
 * financial planning disclaimer, and Play Store rating action.
 */
@Composable
fun AboutCalcHubDialog(
    onDismiss: () -> Unit
) {
    val tokens = LocalCalcHubTokens.current
    val context = LocalContext.current
    val versionName = remember(context) {
        runCatching {
            val pkgInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.PackageInfoFlags.of(0)
                )
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(context.packageName, 0)
            }
            pkgInfo.versionName
        }.getOrNull() ?: "3.0.0"
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = tokens.surfaceLowest,
        titleContentColor = tokens.inkPrimary,
        textContentColor = tokens.inkSecondary,
        icon = {
            Icon(
                imageVector = Icons.Rounded.Info,
                contentDescription = null,
                tint = tokens.accentPrimary
            )
        },
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "About CalcHub",
                    style = MaterialTheme.typography.titleLarge
                )
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = tokens.surfaceContainer
                ) {
                    Text(
                        text = "v$versionName",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = tokens.inkPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "100% offline financial toolkit built for privacy with zero ads or tracking. Open-source under the MIT license.",
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.inkSecondary
                )
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = tokens.surfaceLow,
                    border = BorderStroke(1.dp, tokens.cardBorder)
                ) {
                    Text(
                        text = "Disclaimer: Estimates are for planning and educational purposes. Verify tax, bank, and scheme rules before investing.",
                        style = MaterialTheme.typography.bodySmall,
                        color = tokens.inkMuted,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val pkg = context.packageName
                    val marketIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$pkg")).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    val webIntent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$pkg")
                    ).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    runCatching { context.startActivity(marketIntent) }
                        .onFailure { runCatching { context.startActivity(webIntent) } }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = tokens.accentPrimary,
                    contentColor = tokens.onAccent
                ),
                shape = RoundedCornerShape(tokens.pillCornerRadius)
            ) {
                Icon(
                    imageVector = Icons.Rounded.StarRate,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Rate App")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = tokens.inkSecondary, fontWeight = FontWeight.SemiBold)
            }
        }
    )
}
