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
        // Neon Input Style
        NeonInput(
            value = textValue,
            onValueChange = { newText ->
                textValue = newText
                newText.toDoubleOrNull()?.let { newValue ->
                    if (newValue in range) {
                        onValueChange(newValue)
                    }
                }
            },
            label = label,
            placeholder = "Enter $label",
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth()
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
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
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
        borderColor = MaterialTheme.colorScheme.primary
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "SUMMARY",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            ResultRow("Invested Amount", currencyFormat.format(investedAmount))
            ResultRow("Est. Returns", currencyFormat.format(estimatedReturns))
            
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(16.dp))
            
            ResultRow("Total Value", currencyFormat.format(totalValue), isTotal = true)
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
