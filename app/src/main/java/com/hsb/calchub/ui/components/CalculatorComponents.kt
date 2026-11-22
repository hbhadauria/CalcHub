package com.hsb.calchub.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BookmarkAdd
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
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
    val haptic = LocalHapticFeedback.current

    var textValue by remember { mutableStateOf(formatInputNumber(value)) }
    var isFocused by remember { mutableStateOf(false) }
    var lastHapticBucket by remember { mutableStateOf(formatInputNumber(value)) }

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
                Text(
                    text = "${formatBoundLabel(range.start, symbol)} – ${formatBoundLabel(range.endInclusive, symbol)}",
                    style = MaterialTheme.typography.labelSmall.copy(fontFeatureSettings = "tnum"),
                    color = tokens.inkMuted
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

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
                                textValue = sanitized
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
                            onDone = { focusManager.clearFocus() }
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
                                    textValue = formatInputNumber(finalVal)
                                }
                                isFocused = nowFocused
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

        if (isOutOfRange) {
            Text(
                text = "Will adjust to range ${formatInputNumber(range.start)}–${formatInputNumber(range.endInclusive)}",
                style = MaterialTheme.typography.labelSmall,
                color = tokens.lossInk,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

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
                textValue = formatted
                onValueChange(rawDouble)
            },
            valueRange = range.start.toFloat()..range.endInclusive.toFloat(),
            colors = SliderDefaults.colors(
                thumbColor = tokens.accentPrimary,
                activeTrackColor = tokens.accentPrimary,
                inactiveTrackColor = tokens.trackBg
            ),
            modifier = Modifier
                .fillMaxWidth()
                .semantics {
                    contentDescription = "$label slider"
                }
        )
    }
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

        Spacer(modifier = Modifier.height(8.dp))

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

        Spacer(modifier = Modifier.height(14.dp))
        HorizontalDivider(color = tokens.cardBorder)
        Spacer(modifier = Modifier.height(12.dp))

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
                    uncheckedTrackColor = tokens.surfaceContainer,
                    uncheckedBorderColor = tokens.cardBorder
                ),
                modifier = Modifier.semantics {
                    contentDescription = "Adjust for Inflation"
                }
            )
        }
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
