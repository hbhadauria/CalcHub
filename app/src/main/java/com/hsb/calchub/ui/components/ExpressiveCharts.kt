package com.hsb.calchub.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hsb.calchub.ui.theme.LocalCalcHubTokens
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt

/**
 * Custom Compose Canvas + PathMeasure spline chart (Decision D5) with touch-scrubbing & haptics.
 *
 * @param points List of pairs (Invested/Principal, Total Wealth/Maturity) ordered by year.
 */
@Composable
fun WealthGrowthSplineChart(
    title: String,
    points: List<Pair<Double, Double>>,
    modifier: Modifier = Modifier
) {
    val tokens = LocalCalcHubTokens.current
    val haptic = LocalHapticFeedback.current
    var scrubbedIndex by remember(points) { mutableIntStateOf(-1) }

    val drawProgress = remember(points) { Animatable(0f) }
    LaunchedEffect(points) {
        drawProgress.snapTo(0f)
        drawProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing)
        )
    }

    val safePoints = remember(points) {
        if (points.size >= 2) points else listOf(0.0 to 0.0, 1.0 to 1.0)
    }
    val formatter = rememberInrFormatter()
    val pillLabel = if (scrubbedIndex in safePoints.indices) {
        val pt = safePoints[scrubbedIndex]
        "Yr $scrubbedIndex · ${formatCompactINR(pt.second, formatter)}"
    } else {
        "Drag curve to scrub"
    }

    ChartHeaderSurfaceCard(
        title = title,
        badgeLabel = pillLabel,
        badgeHighlighted = scrubbedIndex >= 0,
        modifier = modifier
    ) {
            Spacer(modifier = Modifier.height(10.dp))

            val pathMeasure = remember { PathMeasure() }
            val lineColorBrush = if (tokens.useGradientFill) {
                Brush.horizontalGradient(listOf(tokens.chartStart, tokens.chartEnd))
            } else {
                Brush.horizontalGradient(listOf(tokens.accentPrimary, tokens.accentPrimary))
            }
            val areaBrush = Brush.verticalGradient(
                listOf(
                    tokens.chartEnd.copy(alpha = 0.28f),
                    tokens.chartStart.copy(alpha = 0.02f)
                )
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(126.dp)
                    .pointerInput(safePoints) {
                        detectTapGestures { offset ->
                            val idx = ((offset.x / size.width.coerceAtLeast(1)) * (safePoints.size - 1))
                                .roundToInt()
                                .coerceIn(0, safePoints.lastIndex)
                            if (idx != scrubbedIndex) {
                                scrubbedIndex = idx
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            }
                        }
                    }
                    .pointerInput(safePoints) {
                        detectHorizontalDragGestures(
                            onDragEnd = {},
                            onDragCancel = {},
                            onHorizontalDrag = { change, _ ->
                                val idx = ((change.position.x / size.width.coerceAtLeast(1)) * (safePoints.size - 1))
                                    .roundToInt()
                                    .coerceIn(0, safePoints.lastIndex)
                                if (idx != scrubbedIndex) {
                                    scrubbedIndex = idx
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                }
                            }
                        )
                    }
            ) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    val padH = 8.dp.toPx()
                    val padTop = 10.dp.toPx()
                    val padBot = 8.dp.toPx()
                    val chartW = (size.width - padH * 2f).coerceAtLeast(1f)
                    val chartH = (size.height - padTop - padBot).coerceAtLeast(1f)
                    val yBottom = padTop + chartH

                    val maxVal = safePoints.maxOf { maxOf(it.first, it.second) }.coerceAtLeast(1.0) * 1.06

                    // Baseline & mid dashed grid line
                    drawLine(
                        color = tokens.trackBg,
                        start = Offset(padH, yBottom),
                        end = Offset(padH + chartW, yBottom),
                        strokeWidth = 1.dp.toPx()
                    )
                    drawLine(
                        color = tokens.trackBg,
                        start = Offset(padH, padTop + chartH * 0.5f),
                        end = Offset(padH + chartW, padTop + chartH * 0.5f),
                        strokeWidth = 1.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 10f))
                    )

                    val valOffsets = safePoints.mapIndexed { i, pt ->
                        val fracX = i.toFloat() / (safePoints.size - 1).coerceAtLeast(1)
                        val fracY = (pt.second / maxVal).toFloat().coerceIn(0f, 1f)
                        Offset(padH + fracX * chartW, yBottom - fracY * chartH)
                    }
                    val invOffsets = safePoints.mapIndexed { i, pt ->
                        val fracX = i.toFloat() / (safePoints.size - 1).coerceAtLeast(1)
                        val fracY = (pt.first / maxVal).toFloat().coerceIn(0f, 1f)
                        Offset(padH + fracX * chartW, yBottom - fracY * chartH)
                    }

                    val smoothValPath = buildCatmullRomPath(valOffsets)
                    val areaPath = Path().apply {
                        addPath(smoothValPath)
                        lineTo(valOffsets.last().x, yBottom)
                        lineTo(valOffsets.first().x, yBottom)
                        close()
                    }
                    val invPath = buildCatmullRomPath(invOffsets)

                    drawPath(path = areaPath, brush = areaBrush, alpha = drawProgress.value)
                    drawPath(
                        path = invPath,
                        color = tokens.inkMuted,
                        style = Stroke(
                            width = 1.6.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f))
                        )
                    )

                    pathMeasure.setPath(smoothValPath, false)
                    val drawnSegment = Path()
                    pathMeasure.getSegment(0f, pathMeasure.length * drawProgress.value, drawnSegment, true)
                    drawPath(
                        path = drawnSegment,
                        brush = lineColorBrush,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )

                    if (scrubbedIndex in valOffsets.indices) {
                        val pt = valOffsets[scrubbedIndex]
                        drawLine(
                            color = tokens.inkPrimary.copy(alpha = 0.55f),
                            start = Offset(pt.x, padTop),
                            end = Offset(pt.x, yBottom),
                            strokeWidth = 1.2.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f))
                        )
                        drawCircle(
                            color = Color.White,
                            radius = 6.dp.toPx(),
                            center = pt
                        )
                        drawCircle(
                            color = tokens.chartEnd,
                            radius = 4.dp.toPx(),
                            center = pt
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val maxYr = (safePoints.size - 1).coerceAtLeast(1)
                Text("Yr 0", style = MaterialTheme.typography.labelMedium, color = tokens.inkMuted)
                Text("Yr ${maxYr / 2}", style = MaterialTheme.typography.labelMedium, color = tokens.inkMuted)
                Text("Yr $maxYr", style = MaterialTheme.typography.labelMedium, color = tokens.inkMuted)
            }
        }
}

/**
 * Yearly Principal vs Interest stacked bar chart with Crossover Year callout (Decision D5).
 */
@Composable
fun LoanAmortizationChart(
    yearlyBreakdown: List<Pair<Double, Double>>,
    crossoverYear: Int,
    modifier: Modifier = Modifier
) {
    val tokens = LocalCalcHubTokens.current
    var selectedYearIdx by remember(yearlyBreakdown) { mutableIntStateOf(-1) }
    val formatter = rememberInrFormatter()
    val label = if (selectedYearIdx in yearlyBreakdown.indices) {
        val (p, i) = yearlyBreakdown[selectedYearIdx]
        "Yr ${selectedYearIdx + 1}: P ${formatCompactINR(p, formatter)} · I ${formatCompactINR(i, formatter)}"
    } else {
        "Crossover Yr $crossoverYear"
    }

    ChartHeaderSurfaceCard(
        title = "Yearly Principal vs Interest",
        badgeLabel = label,
        modifier = modifier
    ) {
            Spacer(modifier = Modifier.height(10.dp))

            val bars = yearlyBreakdown.ifEmpty { listOf(1.0 to 1.0) }
            val maxYearSum = bars.maxOf { it.first + it.second }.coerceAtLeast(1.0)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(86.dp)
                    .pointerInput(bars) {
                        detectTapGestures { offset ->
                            val idx = ((offset.x / size.width.coerceAtLeast(1)) * bars.size)
                                .toInt()
                                .coerceIn(0, bars.lastIndex)
                            selectedYearIdx = idx
                        }
                    }
            ) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    val gap = 3.dp.toPx()
                    val count = bars.size.coerceAtLeast(1)
                    val barW = ((size.width - gap * (count - 1)) / count).coerceAtLeast(3f)
                    bars.forEachIndexed { index, (prin, intr) ->
                        val x = index * (barW + gap)
                        val totalFrac = ((prin + intr) / maxYearSum).toFloat().coerceIn(0.05f, 1f)
                        val totalH = size.height * totalFrac
                        val prinFrac = (prin / (prin + intr).coerceAtLeast(1.0)).toFloat().coerceIn(0f, 1f)
                        val prinH = totalH * prinFrac
                        val intrH = (totalH - prinH).coerceAtLeast(0f)
                        val topY = size.height - totalH

                        if (intrH > 1f) {
                            drawRoundRect(
                                color = tokens.lossInk.copy(alpha = if (selectedYearIdx == index) 1f else 0.78f),
                                topLeft = Offset(x, topY),
                                size = Size(barW, (intrH - 1.dp.toPx()).coerceAtLeast(1f)),
                                cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
                            )
                        }
                        if (prinH > 1f) {
                            drawRoundRect(
                                color = tokens.gainInk.copy(alpha = if (selectedYearIdx == index) 1f else 0.92f),
                                topLeft = Offset(x, topY + intrH),
                                size = Size(barW, prinH),
                                cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(tokens.gainBg)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "From Year $crossoverYear you pay more principal than interest",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = tokens.gainInk
                )
            }
        }
}

/**
 * Interactive Age 25..85 FIRE timeline summary track.
 */
@Composable
fun FireTimelineChart(
    currentAge: Int,
    retireAge: Int,
    modifier: Modifier = Modifier
) {
    val tokens = LocalCalcHubTokens.current
    val saveYears = (retireAge - currentAge).coerceAtLeast(1)
    val retireYears = (85 - retireAge).coerceAtLeast(1)
    val curPct = ((currentAge - 18) / 67f).coerceIn(0.02f, 0.7f)
    val retPct = ((retireAge - 18) / 67f).coerceIn(curPct + 0.08f, 0.96f)

    ChartHeaderSurfaceCard(
        title = "Life Timeline (Age 18–85)",
        badgeLabel = "Save ${saveYears}y · Retire ${retireYears}y",
        modifier = modifier
    ) {
            Spacer(modifier = Modifier.height(10.dp))

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(34.dp)
            ) {
                val trackY = size.height / 2f
                val trackH = 8.dp.toPx()
                val barStart = size.width * curPct
                val barEnd = size.width * retPct
                drawRoundRect(
                    color = tokens.trackBg,
                    topLeft = Offset(0f, trackY - trackH / 2f),
                    size = Size(size.width, trackH),
                    cornerRadius = CornerRadius(trackH / 2f, trackH / 2f)
                )
                drawRoundRect(
                    brush = Brush.horizontalGradient(listOf(tokens.chartStart, tokens.chartEnd)),
                    topLeft = Offset(barStart, trackY - trackH / 2f),
                    size = Size((barEnd - barStart).coerceAtLeast(8.dp.toPx()), trackH),
                    cornerRadius = CornerRadius(trackH / 2f, trackH / 2f)
                )
                drawCircle(color = tokens.surfaceLowest, radius = 9.dp.toPx(), center = Offset(barStart, trackY))
                drawCircle(color = tokens.chartStart, radius = 6.dp.toPx(), center = Offset(barStart, trackY))
                drawCircle(color = tokens.surfaceLowest, radius = 14.dp.toPx(), center = Offset(barEnd, trackY))
                drawCircle(color = tokens.chartEnd, radius = 11.dp.toPx(), center = Offset(barEnd, trackY))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Now $currentAge", style = MaterialTheme.typography.labelMedium, color = tokens.inkMuted)
                Text("Retire @ $retireAge", style = MaterialTheme.typography.labelMedium, color = tokens.inkPrimary)
                Text("Age 85", style = MaterialTheme.typography.labelMedium, color = tokens.inkMuted)
            }
        }
}

/**
 * Compact growth sparkline rendered inside the Home Continue hero banner.
 */
@Composable
fun MiniGrowthSparkline(modifier: Modifier = Modifier) {
    val tokens = LocalCalcHubTokens.current
    val sparkBrush = if (tokens.useGradientFill) {
        Brush.horizontalGradient(listOf(tokens.chartStart, tokens.chartEnd))
    } else {
        Brush.horizontalGradient(listOf(tokens.accentPrimary, tokens.accentPrimary))
    }
    Canvas(modifier = modifier) {
        val pts = listOf(
            Offset(0f, size.height * 0.90f),
            Offset(size.width * 0.35f, size.height * 0.72f),
            Offset(size.width * 0.68f, size.height * 0.44f),
            Offset(size.width * 0.94f, size.height * 0.14f)
        )
        val linePath = buildCatmullRomPath(pts)
        val fillPath = Path().apply {
            addPath(linePath)
            lineTo(pts.last().x, size.height)
            lineTo(pts.first().x, size.height)
            close()
        }
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                listOf(tokens.chartEnd.copy(alpha = 0.24f), Color.Transparent)
            )
        )
        drawPath(
            path = linePath,
            brush = sparkBrush,
            style = Stroke(width = 2.6.dp.toPx(), cap = StrokeCap.Round)
        )
        drawCircle(
            color = tokens.chartEnd,
            radius = 3.2.dp.toPx(),
            center = pts.last()
        )
    }
}

private fun buildCatmullRomPath(pts: List<Offset>): Path {
    val path = Path()
    if (pts.isEmpty()) return path
    path.moveTo(pts.first().x, pts.first().y)
    if (pts.size < 2) return path
    for (i in 0 until pts.lastIndex) {
        val p0 = pts[(i - 1).coerceAtLeast(0)]
        val p1 = pts[i]
        val p2 = pts[i + 1]
        val p3 = pts[(i + 2).coerceAtMost(pts.lastIndex)]
        val c1x = p1.x + (p2.x - p0.x) / 6f
        val c1y = p1.y + (p2.y - p0.y) / 6f
        val c2x = p2.x - (p3.x - p1.x) / 6f
        val c2y = p2.y - (p3.y - p1.y) / 6f
        path.cubicTo(c1x, c1y, c2x, c2y, p2.x, p2.y)
    }
    return path
}

internal fun formatCompactINR(amount: Double, fullFormatter: NumberFormat): String {
    if (!amount.isFinite()) return "₹0"
    val absVal = kotlin.math.abs(amount)
    val sign = if (amount < 0) "-" else ""
    return when {
        absVal >= 10_000_000.0 -> String.format(Locale.US, "%s₹%.2f Cr", sign, absVal / 10_000_000.0)
        absVal >= 100_000.0 -> String.format(Locale.US, "%s₹%.2f L", sign, absVal / 100_000.0)
        else -> fullFormatter.format(amount)
    }
}

@Composable
private fun rememberInrFormatter(): NumberFormat = remember {
    NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN")).apply {
        maximumFractionDigits = 0
    }
}

@Composable
private fun ChartHeaderSurfaceCard(
    title: String,
    badgeLabel: String,
    modifier: Modifier = Modifier,
    badgeHighlighted: Boolean = false,
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit
) {
    val tokens = LocalCalcHubTokens.current
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(tokens.cardCornerRadius),
        color = tokens.surfaceLowest,
        border = androidx.compose.foundation.BorderStroke(1.dp, tokens.cardBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = tokens.inkPrimary
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(tokens.pillCornerRadius))
                        .background(if (badgeHighlighted) tokens.accentPrimary else tokens.surfaceContainer)
                        .padding(horizontal = 9.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = badgeLabel,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (badgeHighlighted) tokens.onAccent else tokens.inkSecondary
                    )
                }
            }
            content()
        }
    }
}
