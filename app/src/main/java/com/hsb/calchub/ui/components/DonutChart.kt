package com.hsb.calchub.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hsb.calchub.ui.theme.LocalCalcHubTokens
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt

/**
 * Data class representing a segment in [DonutChart].
 */
data class DonutChartData(
    val value: Double,
    val color: Color,
    val label: String
)

/**
 * Clean custom Compose [Canvas] growth/breakdown ring (Decision D5, zero blur glow).
 */
@Composable
fun DonutChart(
    data: List<DonutChartData>,
    modifier: Modifier = Modifier,
    chartSize: Dp = 152.dp,
    strokeWidth: Dp = 14.dp,
    centerValueText: String? = null,
    centerLabelText: String? = null,
    showLegend: Boolean = true
) {
    val tokens = LocalCalcHubTokens.current
    val safeData = data.map { item ->
        val safeVal = if (item.value.isFinite() && item.value > 0.0) item.value else 0.0
        item.copy(value = safeVal)
    }
    val total = safeData.sumOf { it.value }
    val sweepAngles = if (total > 0.0) {
        safeData.map { ((it.value / total) * 360.0).toFloat() }
    } else {
        safeData.map { 0f }
    }

    val lastPct = if (total > 0.0 && safeData.size >= 2) {
        ((safeData.last().value / total) * 100.0).roundToInt().coerceIn(0, 100)
    } else if (total > 0.0 && safeData.size == 1) {
        100
    } else {
        0
    }

    val primaryHeadline = centerValueText ?: "$lastPct%"
    val primarySub = centerLabelText ?: safeData.lastOrNull()?.label ?: "Total"

    val animation = remember { Animatable(0f) }
    LaunchedEffect(safeData) {
        animation.snapTo(0f)
        animation.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing)
        )
    }

    val currencyFormat = remember {
        NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN")).apply {
            maximumFractionDigits = 0
        }
    }

    val summaryDesc = buildString {
        append("$primarySub $primaryHeadline. ")
        safeData.forEach {
            append("${it.label}: ${currencyFormat.format(it.value)}. ")
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .semantics(mergeDescendants = true) { contentDescription = summaryDesc },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(chartSize),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(chartSize)) {
                val strokePx = strokeWidth.toPx()
                val arcSize = Size(size.width - strokePx, size.height - strokePx)
                val topLeft = Offset(strokePx / 2f, strokePx / 2f)
                val strokeStyle = Stroke(width = strokePx, cap = StrokeCap.Round)

                // Background ring track
                drawArc(
                    color = tokens.trackBg,
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = strokeStyle
                )

                var startAngle = -90f
                sweepAngles.forEachIndexed { index, rawSweep ->
                    val sweep = rawSweep * animation.value
                    if (sweep > 0.5f) {
                        val isSecondaryOrLast = index == sweepAngles.lastIndex
                        if (isSecondaryOrLast && tokens.useGradientFill) {
                            drawArc(
                                brush = Brush.linearGradient(listOf(tokens.chartStart, tokens.chartEnd)),
                                startAngle = startAngle,
                                sweepAngle = sweep,
                                useCenter = false,
                                topLeft = topLeft,
                                size = arcSize,
                                style = strokeStyle
                            )
                        } else {
                            val fallbackColor = if (index == 0) {
                                tokens.inkMuted.copy(alpha = 0.45f)
                            } else {
                                tokens.accentPrimary
                            }
                            drawArc(
                                color = fallbackColor,
                                startAngle = startAngle,
                                sweepAngle = sweep,
                                useCenter = false,
                                topLeft = topLeft,
                                size = arcSize,
                                style = strokeStyle
                            )
                        }
                    }
                    startAngle += rawSweep
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = primaryHeadline,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = tokens.inkPrimary
                )
                Text(
                    text = primarySub.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = tokens.inkSecondary
                )
            }
        }

        if (showLegend && safeData.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(tokens.cardCornerRadius))
                    .background(tokens.surfaceLow)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                safeData.forEachIndexed { idx, item ->
                    val dotColor = if (idx == safeData.lastIndex) tokens.chartEnd else tokens.inkMuted
                    val pct = if (total > 0.0) ((item.value / total) * 100.0).roundToInt() else 0
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(9.dp)
                                .clip(CircleShape)
                                .background(dotColor)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${item.label} ($pct%)",
                            style = MaterialTheme.typography.labelMedium,
                            color = tokens.inkSecondary
                        )
                    }
                }
            }
        }
    }
}
