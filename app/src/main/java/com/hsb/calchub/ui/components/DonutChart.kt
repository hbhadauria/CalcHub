package com.hsb.calchub.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class DonutChartData(
    val value: Double,
    val color: Color,
    val label: String
)

@Composable
fun DonutChart(
    data: List<DonutChartData>,
    modifier: Modifier = Modifier,
    chartSize: Dp = 200.dp,
    strokeWidth: Dp = 30.dp
) {
    val total = data.sumOf { it.value }
    val proportions = data.map { it.value / total }
    val sweepAngles = proportions.map { it * 360f }

    val animation = remember { Animatable(0f) }

    LaunchedEffect(data) {
        animation.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000, easing = LinearEasing)
        )
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(chartSize),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxWidth().aspectRatio(1f)) {
                var startAngle = -90f
                val stroke = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Butt)

                sweepAngles.forEachIndexed { index, sweepAngle ->
                    drawArc(
                        color = data[index].color,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle.toFloat() * animation.value,
                        useCenter = false,
                        style = stroke,
                        size = Size(size.width, size.height),
                        topLeft = Offset(0f, 0f)
                    )
                    startAngle += sweepAngle.toFloat()
                }
            }
        }
        
        Spacer(modifier = Modifier.size(16.dp))
        
        // Legend
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            data.forEach { item ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Canvas(modifier = Modifier.size(12.dp)) {
                        drawCircle(color = item.color)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = item.label, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
