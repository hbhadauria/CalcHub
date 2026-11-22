package com.hsb.calchub.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BookmarkAdd
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.hsb.calchub.ui.theme.CalcHubSkin
import com.hsb.calchub.ui.theme.LocalCalcHubTokens
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToLong

private fun formatInputNumber(value: Double): String {
    if (!value.isFinite()) return "0"
    val rounded = value.roundToLong().toDouble()
    return if (abs(value - rounded) < 0.005) {
        rounded.toLong().toString()
    } else {
        String.format(Locale.US, "%.2f", value).trimEnd('0').trimEnd('.')
    }
}

private fun formatBoundLabel(value: Double, symbol: String): String {
    val compact = formatCompactNumber(value)
    return when {
        symbol == "₹" -> "₹$compact"
        symbol.isEmpty() -> compact
        else -> "$compact$symbol"
    }
}

private fun formatCompactNumber(value: Double): String {
    val absVal = abs(value)
    return when {
        absVal >= 1_00_00_000 -> String.format(Locale.US, "%.1fCr", value / 1_00_00_000).replace(".0", "")
        absVal >= 1_00_000 -> String.format(Locale.US, "%.1fL", value / 1_00_000).replace(".0", "")
        absVal >= 1_000 -> String.format(Locale.US, "%.1fK", value / 1_000).replace(".0", "")
        else -> formatInputNumber(value)
    }
}

/**
 * Tactile calculator parameter control with:
 * - Decimal-safe manual text entry (preserves intermediate states like "12." while typing)
 * - Focus-loss range clamping and explicit validation helper
 * - Subtle haptic feedback on slider steps
 * - Tabular numerals and semantic labels across all themes
 */
@Composable
fun CalculatorInput(
    label: String,
    value: Double,
    onValueChange: (Double) -> Unit,
    range: ClosedFloatingPointRange<Double>,
    symbol: String,
    modifier: Modifier = Modifier
) {
    val tokens = LocalCalcHubTokens.current
    val focusManager = LocalFocusManager.current

    var textValue by remember { mutableStateOf(formatInputNumber(value)) }
    var isFocused by remember { mutableStateOf(false) }

    LaunchedEffect(value) {
        if (!isFocused) {
            textValue = formatInputNumber(value)
        }
    }

    val parsedWhileEditing = textValue.toDoubleOrNull()
    val isOutOfRange = isFocused && parsedWhileEditing != null && parsedWhileEditing !in range
    val isInvalidSyntax = isFocused && textValue.isNotEmpty() && parsedWhileEditing == null

    val inputBoxShape = RoundedCornerShape(if (tokens.skin == CalcHubSkin.MATRIX) 8.dp else 12.dp)
    val boxBorderColor = when {
        isOutOfRange || isInvalidSyntax -> tokens.lossInk
        isFocused -> tokens.accentPrimary
        else -> tokens.cardBorder
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = tokens.inkPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (isFocused || isOutOfRange) {
                    Text(
                        text = "${formatBoundLabel(range.start, symbol)} – ${formatBoundLabel(range.endInclusive, symbol)}",
                        style = MaterialTheme.typography.labelSmall.copy(fontFeatureSettings = "tnum"),
                        color = tokens.inkMuted
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            CalculatorInputTextBox(
                label = label,
                textValue = textValue,
                value = value,
                range = range,
                symbol = symbol,
                isFocused = isFocused,
                inputBoxShape = inputBoxShape,
                boxBorderColor = boxBorderColor,
                onTextChange = { textValue = it },
                onValueChange = onValueChange,
                onFocusChange = { isFocused = it },
                onDone = { focusManager.clearFocus() }
            )
        }

        if (isOutOfRange) {
            Text(
                text = "Will adjust to range ${formatInputNumber(range.start)}–${formatInputNumber(range.endInclusive)}",
                style = MaterialTheme.typography.labelSmall,
                color = tokens.lossInk,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        CalculatorInputSlider(
            label = label,
            value = value,
            range = range,
            onSliderChange = { rawDouble, formatted ->
                textValue = formatted
                onValueChange(rawDouble)
            }
        )
    }
}

@Composable
private fun CalculatorInputTextBox(
    label: String,
    textValue: String,
    value: Double,
    range: ClosedFloatingPointRange<Double>,
    symbol: String,
    isFocused: Boolean,
    inputBoxShape: RoundedCornerShape,
    boxBorderColor: Color,
    onTextChange: (String) -> Unit,
    onValueChange: (Double) -> Unit,
    onFocusChange: (Boolean) -> Unit,
    onDone: () -> Unit
) {
    val tokens = LocalCalcHubTokens.current
    Surface(
        shape = inputBoxShape,
        color = tokens.surfaceLow,
        border = BorderStroke(1.dp, boxBorderColor)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            if (symbol == "₹") {
                Text(
                    text = "₹",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontFeatureSettings = "tnum"
                    ),
                    color = tokens.accentPrimary,
                    modifier = Modifier.padding(end = 4.dp)
                )
            }
            BasicTextField(
                value = textValue,
                onValueChange = { next ->
                    val sanitized = next.filter { it.isDigit() || it == '.' }
                    if (sanitized.count { it == '.' } <= 1) {
                        onTextChange(sanitized)
                        val parsed = sanitized.toDoubleOrNull()
                        if (parsed != null && parsed in range) {
                            onValueChange(parsed)
                        }
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { onDone() }
                ),
                textStyle = MaterialTheme.typography.titleSmall.copy(
                    color = tokens.inkPrimary,
                    fontWeight = FontWeight.Bold,
                    fontFeatureSettings = "tnum",
                    textAlign = TextAlign.End
                ),
                cursorBrush = SolidColor(tokens.accentPrimary),
                modifier = Modifier
                    .widthIn(min = 52.dp, max = 104.dp)
                    .semantics {
                        contentDescription = "$label value input"
                    }
                    .onFocusChanged { focusState ->
                        val nowFocused = focusState.isFocused
                        if (isFocused && !nowFocused) {
                            val parsed = textValue.toDoubleOrNull()
                            val finalVal = (parsed ?: value).coerceIn(range.start, range.endInclusive)
                            onValueChange(finalVal)
                            onTextChange(formatInputNumber(finalVal))
                        }
                        onFocusChange(nowFocused)
                    }
            )
            if (symbol != "₹" && symbol.isNotEmpty()) {
                Text(
                    text = symbol,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontFeatureSettings = "tnum"
                    ),
                    color = tokens.accentPrimary,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun CalculatorInputSlider(
    label: String,
    value: Double,
    range: ClosedFloatingPointRange<Double>,
    onSliderChange: (Double, String) -> Unit
) {
    val tokens = LocalCalcHubTokens.current
    val haptic = LocalHapticFeedback.current
    var lastHapticBucket by remember { mutableStateOf(formatInputNumber(value)) }
    val sliderVal = value.toFloat().coerceIn(range.start.toFloat(), range.endInclusive.toFloat())
    Slider(
        value = sliderVal,
        onValueChange = { raw ->
            val rawDouble = raw.toDouble().coerceIn(range.start, range.endInclusive)
            val formatted = formatInputNumber(rawDouble)
            if (formatted != lastHapticBucket) {
                lastHapticBucket = formatted
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            }
            onSliderChange(rawDouble, formatted)
        },
        valueRange = range.start.toFloat()..range.endInclusive.toFloat(),
        colors = SliderDefaults.colors(
            thumbColor = tokens.accentPrimary,
            activeTrackColor = tokens.accentPrimary,
            inactiveTrackColor = tokens.trackBg
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(36.dp)
            .semantics {
                contentDescription = "$label slider"
            }
    )
}

/**
 * Top-ready hero summary verdict card used by calculators across the app.
 * Features TalkBack live-region announcements, compact wealth multiple badge, and theme-adaptive hero display.
 */
@Composable
fun ResultCard(
    investedAmount: Double,
    estimatedReturns: Double,
    totalValue: Double,
    primaryLabel: String = "Total Value",
    investedLabel: String = "Invested",
    returnsLabel: String = "Est. Returns",
    onSaveScenario: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val tokens = LocalCalcHubTokens.current
    val currencyFormat = remember {
        NumberFormat.getCurrencyInstance(Locale.Builder().setLanguage("en").setRegion("IN").build()).apply {
            maximumFractionDigits = 0
        }
    }

    val safeInvested = if (investedAmount.isFinite()) investedAmount else 0.0
    val safeReturns = if (estimatedReturns.isFinite()) estimatedReturns else 0.0
    val safeTotal = if (totalValue.isFinite()) totalValue else 0.0

    val totalText = currencyFormat.format(safeTotal)
    val compactBadge = formatCompactINR(safeTotal, currencyFormat)
    val multiplier = if (safeInvested > 0.0 && safeTotal > 0.0) safeTotal / safeInvested else null

    NeonCard(
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                liveRegion = LiveRegionMode.Polite
                contentDescription = "$primaryLabel $totalText. $investedLabel ${currencyFormat.format(safeInvested)}. $returnsLabel ${currencyFormat.format(safeReturns)}"
            },
        useGradientBorder = true
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(tokens.accentPrimary)
                )
                Text(
                    text = primaryLabel.uppercase(),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = tokens.accentPrimary
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (multiplier != null && multiplier >= 1.05) {
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = tokens.gainBg,
                        border = BorderStroke(1.dp, tokens.gainInk.copy(alpha = 0.35f))
                    ) {
                        Text(
                            text = String.format(Locale.US, "%.2fx", multiplier),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontFeatureSettings = "tnum"
                            ),
                            color = tokens.gainInk,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
                if (onSaveScenario != null) {
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = tokens.surfaceLow,
                        border = BorderStroke(1.dp, tokens.cardBorder),
                        onClick = onSaveScenario
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.BookmarkAdd,
                                contentDescription = "Save scenario to Vault",
                                tint = tokens.accentPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Save",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = tokens.inkPrimary
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OdometerText(
                text = if (tokens.skin == CalcHubSkin.MATRIX) compactBadge else totalText,
                style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
                color = tokens.inkPrimary,
                highlightLedger = true,
                useDotMatrixInMatrixSkin = true,
                modifier = Modifier.weight(1f)
            )
            if (tokens.skin == CalcHubSkin.MATRIX) {
                Text(
                    text = totalText,
                    style = MaterialTheme.typography.labelMedium.copy(fontFeatureSettings = "tnum"),
                    color = tokens.inkSecondary
                )
            } else {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = tokens.surfaceContainer
                ) {
                    Text(
                        text = compactBadge,
                        style = MaterialTheme.typography.labelSmall.copy(fontFeatureSettings = "tnum"),
                        color = tokens.inkSecondary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        HorizontalDivider(color = tokens.cardBorder)
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(tokens.chartStart)
                    )
                    Text(
                        text = investedLabel,
                        style = MaterialTheme.typography.labelMedium,
                        color = tokens.inkSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = currencyFormat.format(safeInvested),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontFeatureSettings = "tnum"
                    ),
                    color = tokens.inkPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.End
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(tokens.chartEnd)
                    )
                    Text(
                        text = returnsLabel,
                        style = MaterialTheme.typography.labelMedium,
                        color = tokens.inkSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = currencyFormat.format(safeReturns),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontFeatureSettings = "tnum"
                    ),
                    color = tokens.gainInk,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * Formatted key-value breakdown row used inside result summary cards.
 */
@Composable
fun ResultRow(
    label: String,
    value: String,
    isTotal: Boolean = false
) {
    val tokens = LocalCalcHubTokens.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = if (isTotal) MaterialTheme.typography.titleSmall else MaterialTheme.typography.bodyMedium,
            color = if (isTotal) tokens.inkPrimary else tokens.inkSecondary,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = value,
            style = (if (isTotal) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleSmall).copy(
                fontWeight = if (isTotal) FontWeight.Bold else FontWeight.SemiBold,
                fontFeatureSettings = "tnum"
            ),
            color = if (isTotal) tokens.accentPrimary else tokens.inkPrimary
        )
    }
}

/**
 * Reusable inflation-adjustment toggle and rate slider for investment calculators.
 */
@Composable
fun InflationAdjustmentControl(
    enabled: Boolean,
    onEnabledChange: (Boolean) -> Unit,
    inflationRate: Double,
    onInflationRateChange: (Double) -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = LocalCalcHubTokens.current
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Adjust for Inflation",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = tokens.inkPrimary
                )
                Text(
                    text = "Discount maturity to today's purchasing power",
                    style = MaterialTheme.typography.labelSmall,
                    color = tokens.inkMuted
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Switch(
                checked = enabled,
                onCheckedChange = onEnabledChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = tokens.onAccent,
                    checkedTrackColor = tokens.accentPrimary,
                    checkedBorderColor = tokens.accentPrimary,
                    uncheckedThumbColor = tokens.inkPrimary,
                    uncheckedTrackColor = tokens.trackBg,
                    uncheckedBorderColor = tokens.inkSecondary.copy(alpha = 0.65f)
                ),
                modifier = Modifier.semantics {
                    contentDescription = "Adjust for Inflation"
                }
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        FinanceTermTooltip(
            term = "What is Inflation Adjustment?",
            explanation = "Converts future maturity into today's purchasing power so you see what your money will really buy."
        )
        if (enabled) {
            Spacer(modifier = Modifier.height(12.dp))
            CalculatorInput(
                label = "Expected Inflation (p.a)",
                value = inflationRate,
                onValueChange = onInflationRateChange,
                range = 0.0..15.0,
                symbol = "%"
            )
        }
    }
}

/**
 * Displays Today's Value (inflation-adjusted purchasing power) alongside nominal maturity and real gain.
 */
@Composable
fun InflationAdjustedSummaryCard(
    nominalValue: Double,
    realValue: Double,
    investedAmount: Double,
    inflationRate: Double,
    years: Double,
    modifier: Modifier = Modifier
) {
    val tokens = LocalCalcHubTokens.current
    val currencyFormat = remember {
        NumberFormat.getCurrencyInstance(Locale.Builder().setLanguage("en").setRegion("IN").build()).apply {
            maximumFractionDigits = 0
        }
    }
    val safeReal = if (realValue.isFinite()) realValue.coerceAtLeast(0.0) else 0.0
    val safeNominal = if (nominalValue.isFinite()) nominalValue.coerceAtLeast(0.0) else 0.0
    val safeInvested = if (investedAmount.isFinite()) investedAmount.coerceAtLeast(0.0) else 0.0
    val realGain = safeReal - safeInvested

    NeonCard(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "TODAY'S VALUE (INFLATION-ADJUSTED)",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = tokens.accentPrimary,
                modifier = Modifier.weight(1f)
            )
            Surface(
                shape = RoundedCornerShape(999.dp),
                color = tokens.surfaceLow,
                border = BorderStroke(1.dp, tokens.cardBorder)
            ) {
                Text(
                    text = String.format(Locale.US, "%.1f%% / yr · %.0fy", inflationRate, years),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontFeatureSettings = "tnum"
                    ),
                    color = tokens.inkSecondary,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = currencyFormat.format(safeReal),
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontFeatureSettings = "tnum"
                ),
                color = tokens.inkPrimary
            )
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = tokens.surfaceContainer
            ) {
                Text(
                    text = formatCompactINR(safeReal, currencyFormat),
                    style = MaterialTheme.typography.labelSmall.copy(fontFeatureSettings = "tnum"),
                    color = tokens.inkSecondary,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        HorizontalDivider(color = tokens.cardBorder)
        Spacer(modifier = Modifier.height(8.dp))
        ResultRow("Nominal Maturity", formatCompactINR(safeNominal, currencyFormat))
        ResultRow("Real Gain vs. Invested", currencyFormat.format(realGain))
    }
}

/**
 * Shared compact donut card used below calculator inputs across screens.
 */
@Composable
fun StandardDonutCard(
    data: List<DonutChartData>,
    centerLabelText: String? = null,
    modifier: Modifier = Modifier
) {
    NeonCard(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DonutChart(
                data = data,
                centerLabelText = centerLabelText
            )
        }
    }
}

/**
 * Quick-fill preset chip model used across calculator screens for single-tap exploration.
 */
data class StarterPreset(
    val label: String,
    val onApply: () -> Unit
)

/**
 * Horizontal row of compact starter preset chips so beginners can explore realistic inputs in one tap.
 */
@Composable
fun StarterPresetChips(
    presets: List<StarterPreset>,
    modifier: Modifier = Modifier
) {
    if (presets.isEmpty()) return
    val tokens = LocalCalcHubTokens.current
    val haptic = LocalHapticFeedback.current
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(presets, key = { it.label }) { preset ->
            Surface(
                shape = RoundedCornerShape(999.dp),
                color = tokens.surfaceLow,
                border = BorderStroke(1.dp, tokens.cardBorder),
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    preset.onApply()
                }
            ) {
                Text(
                    text = preset.label,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = tokens.accentPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

/**
 * Compact expandable inline hint explaining financial terminology in plain English.
 */
@Composable
fun FinanceTermTooltip(
    term: String,
    explanation: String,
    modifier: Modifier = Modifier
) {
    val tokens = LocalCalcHubTokens.current
    var expanded by rememberSaveable(term) { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(999.dp),
            color = tokens.surfaceLow,
            border = BorderStroke(1.dp, if (expanded) tokens.accentPrimary.copy(alpha = 0.4f) else tokens.cardBorder),
            onClick = { expanded = !expanded }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Info,
                    contentDescription = if (expanded) "Hide explanation for $term" else "Explain $term",
                    tint = tokens.accentPrimary,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = term,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = tokens.inkSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        if (expanded) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = tokens.surfaceContainer,
                border = BorderStroke(1.dp, tokens.cardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = explanation,
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.inkSecondary,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }
    }
}

/**
 * High-contrast theme-aware checkbox row matching active CalcHub skin tokens.
 */
@Composable
fun ThemedCheckboxRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = LocalCalcHubTokens.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = tokens.accentPrimary,
                uncheckedColor = tokens.inkSecondary,
                checkmarkColor = tokens.onAccent
            )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = tokens.inkPrimary
        )
    }
}

/**
 * Remembers an Indian-locale currency [NumberFormat] configured with [maxFractionDigits].
 */
@Composable
fun rememberInrFormat(maxFractionDigits: Int = 0): NumberFormat = remember(maxFractionDigits) {
    NumberFormat.getCurrencyInstance(Locale.Builder().setLanguage("en").setRegion("IN").build()).apply {
        maximumFractionDigits = maxFractionDigits
    }
}

/**
 * Standard two-slice donut card comparing invested principal/outflow vs. returns/gains.
 */
@Composable
fun InvestedReturnsDonutCard(
    invested: Double,
    returns: Double,
    investedLabel: String = "Invested",
    returnsLabel: String = "Returns",
    centerLabelText: String? = null,
    useGainLossColors: Boolean = false,
    modifier: Modifier = Modifier
) {
    val tokens = LocalCalcHubTokens.current
    StandardDonutCard(
        data = listOf(
            DonutChartData(invested, if (useGainLossColors) tokens.gainInk else tokens.chartStart, investedLabel),
            DonutChartData(returns, if (useGainLossColors) tokens.lossInk else tokens.chartEnd, returnsLabel)
        ),
        centerLabelText = centerLabelText,
        modifier = modifier
    )
}

/**
 * Shared result-first layout for standard investment calculators (ResultCard -> Inputs NeonCard -> Donut).
 */
@Composable
fun StandardInvestmentCalculatorLayout(
    title: String,
    calculatorId: String,
    onBackClick: () -> Unit,
    investedAmount: Double,
    estimatedReturns: Double,
    totalValue: Double,
    primaryLabel: String = "Total Value",
    investedLabel: String = "Invested",
    returnsLabel: String = "Est. Returns",
    donutInvestedLabel: String = investedLabel,
    donutReturnsLabel: String = if (returnsLabel == "Est. Returns") "Returns" else returnsLabel,
    centerLabelText: String? = null,
    shareSummary: String? = null,
    inputsContent: @Composable ColumnScope.() -> Unit
) {
    CalculatorScrollScaffold(
        title = title,
        onBackClick = onBackClick,
        calculatorId = calculatorId,
        shareSummary = shareSummary
    ) {
        ResultCard(
            investedAmount = investedAmount,
            estimatedReturns = estimatedReturns,
            totalValue = totalValue,
            primaryLabel = primaryLabel,
            investedLabel = investedLabel,
            returnsLabel = returnsLabel
        )

        NeonCard {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                content = inputsContent
            )
        }

        InvestedReturnsDonutCard(
            invested = investedAmount,
            returns = estimatedReturns,
            investedLabel = donutInvestedLabel,
            returnsLabel = donutReturnsLabel,
            centerLabelText = centerLabelText
        )
    }
}

/**
 * Shared result-first layout for custom-result calculators (Results NeonCard -> Inputs NeonCard -> optional footer).
 */
@Composable
fun StandardResultsCalculatorLayout(
    title: String,
    calculatorId: String,
    onBackClick: () -> Unit,
    shareSummary: String? = null,
    resultsContent: @Composable ColumnScope.() -> Unit,
    inputsContent: @Composable ColumnScope.() -> Unit,
    footerContent: (@Composable ColumnScope.() -> Unit)? = null
) {
    CalculatorScrollScaffold(
        title = title,
        onBackClick = onBackClick,
        calculatorId = calculatorId,
        shareSummary = shareSummary
    ) {
        NeonCard(modifier = Modifier.fillMaxWidth(), useGradientBorder = true, content = resultsContent)
        NeonCard {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                content = inputsContent
            )
        }
        footerContent?.invoke(this)
    }
}
