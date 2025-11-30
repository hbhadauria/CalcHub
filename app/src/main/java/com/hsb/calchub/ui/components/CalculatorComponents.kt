package com.hsb.calchub.ui.components

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.text.NumberFormat
import java.util.Locale
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import com.hsb.calchub.ui.theme.NeonGreen
import com.hsb.calchub.ui.theme.NeonPink
import com.hsb.calchub.ui.theme.NeonText
import com.hsb.calchub.ui.theme.NeonSurface

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
        // Label and Input Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = NeonText.copy(alpha = 0.8f)
            )

            // Small Glass Input Box
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
                    color = NeonGreen,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                cursorBrush = SolidColor(NeonGreen),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .width(120.dp)
                            .background(NeonSurface.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .border(1.dp, NeonGreen.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        if (textValue.isEmpty()) {
                            Text(
                                text = "0",
                                color = NeonText.copy(alpha = 0.3f),
                                fontSize = 16.sp
                            )
                        }
                        innerTextField()
                    }
                }
            )
        }

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
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

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
                        imageVector = Icons.Outlined.TrendingUp,
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
