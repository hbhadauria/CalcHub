package com.hsb.calchub.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hsb.calchub.ui.theme.NeonGreen
import com.hsb.calchub.ui.theme.NeonPink
import com.hsb.calchub.ui.theme.NeonSurface
import com.hsb.calchub.ui.theme.NeonText
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CalculatorInput(
    label: String,
    value: Double,
    onValueChange: (Double) -> Unit,
    range: ClosedFloatingPointRange<Double>,
    symbol: String,
    modifier: Modifier = Modifier
) {
    // We keep the internal state for the text field to allow free typing
    var textValue by remember(value) { mutableStateOf(value.toInt().toString()) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        // Label
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = NeonText.copy(alpha = 0.7f),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // NeonInput-styled TextField
        BasicTextField(
            value = textValue,
            onValueChange = { newText ->
                textValue = newText
                newText.toDoubleOrNull()?.let { newValue ->
                    if (newValue in range) {
                        onValueChange(newValue)
                    }
                }
            },
            textStyle = TextStyle(
                color = NeonText,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            cursorBrush = SolidColor(NeonGreen),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(NeonSurface, RoundedCornerShape(12.dp))
                        .border(1.dp, NeonGreen.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            if (textValue.isEmpty()) {
                                Text(
                                    text = "0",
                                    color = NeonText.copy(alpha = 0.3f),
                                    fontSize = 18.sp
                                )
                            }
                            innerTextField()
                        }
                        if (symbol.isNotEmpty()) {
                            Text(
                                text = symbol,
                                color = NeonGreen,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Custom Slider
        Slider(
            value = value.toFloat(),
            onValueChange = {
                onValueChange(it.toDouble())
                textValue = it.toInt().toString()
            },
            valueRange = range.start.toFloat()..range.endInclusive.toFloat(),
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = NeonGreen,
                activeTrackColor = NeonPink,
                inactiveTrackColor = NeonSurface,
                activeTickColor = NeonPink,
                inactiveTickColor = NeonSurface
            )
        )
    }
}

@Composable
fun ResultCard(
    investedAmount: Double,
    estimatedReturns: Double,
    totalValue: Double
) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN"))

    NeonCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        borderColor = NeonGreen.copy(alpha = 0.5f)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Invested Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.AttachMoney, // Using AttachMoney as generic currency/investment icon
                        contentDescription = null,
                        tint = NeonPink,
                        modifier = Modifier.size(24.dp).background(NeonPink.copy(alpha = 0.1f), RoundedCornerShape(50)).padding(4.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Invested",
                        style = MaterialTheme.typography.bodyLarge,
                        color = NeonText.copy(alpha = 0.8f)
                    )
                }
                Text(
                    text = currencyFormat.format(investedAmount),
                    style = MaterialTheme.typography.titleLarge,
                    color = NeonPink,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = NeonGreen.copy(alpha = 0.1f))
            Spacer(modifier = Modifier.height(16.dp))
            
            // Returns Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.TrendingUp,
                        contentDescription = null,
                        tint = NeonGreen,
                        modifier = Modifier.size(24.dp).background(NeonGreen.copy(alpha = 0.1f), RoundedCornerShape(50)).padding(4.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Returns",
                        style = MaterialTheme.typography.bodyLarge,
                        color = NeonText.copy(alpha = 0.8f)
                    )
                }
                Text(
                    text = currencyFormat.format(estimatedReturns),
                    style = MaterialTheme.typography.titleLarge,
                    color = NeonGreen,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = NeonGreen.copy(alpha = 0.1f))
            Spacer(modifier = Modifier.height(16.dp))
            
            // Total Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.AccountBalanceWallet,
                        contentDescription = null,
                        tint = NeonText,
                        modifier = Modifier.size(24.dp).background(NeonText.copy(alpha = 0.1f), RoundedCornerShape(50)).padding(4.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Total Value",
                        style = MaterialTheme.typography.titleMedium,
                        color = NeonText
                    )
                }
                Text(
                    text = currencyFormat.format(totalValue),
                    style = MaterialTheme.typography.headlineMedium,
                    color = NeonText,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ResultRow(label: String, value: String, isTotal: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = if (isTotal) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyLarge,
            color = if (isTotal) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = value,
            style = if (isTotal) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.titleMedium,
            fontWeight = if (isTotal) FontWeight.Black else FontWeight.Bold,
            color = if (isTotal) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}
