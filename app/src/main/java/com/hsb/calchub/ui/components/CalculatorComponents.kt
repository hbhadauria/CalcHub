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
    var textValue by remember(value) { mutableStateOf(value.toInt().toString()) }
    var isEditing by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isEditing) {
                    OutlinedTextField(
                        value = textValue,
                        onValueChange = { newText ->
                            textValue = newText
                            newText.toDoubleOrNull()?.let { newValue ->
                                if (newValue in range) {
                                    onValueChange(newValue)
                                }
                            }
                        },
                        modifier = Modifier.width(120.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { isEditing = false }
                        ),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyMedium,
                        suffix = { Text(symbol) }
                    )
                } else {
                    Text(
                        text = "${value.toInt()} $symbol",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                IconButton(onClick = { isEditing = !isEditing }) {
                    Icon(
                        imageVector = if (isEditing) Icons.Default.Check else Icons.Default.Edit,
                        contentDescription = if (isEditing) "Done" else "Edit",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Slider(
            value = value.toFloat(),
            onValueChange = { 
                onValueChange(it.toDouble())
                textValue = it.toInt().toString()
            },
            valueRange = range.start.toFloat()..range.endInclusive.toFloat(),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun ResultCard(
    investedAmount: Double,
    estimatedReturns: Double,
    totalValue: Double
) {
    val currencyFormat = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
        NumberFormat.getCurrencyInstance(Locale.of("en", "IN"))
    } else {
        NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            ResultRow("Invested Amount", currencyFormat.format(investedAmount))
            ResultRow("Est. Returns", currencyFormat.format(estimatedReturns))
            Spacer(modifier = Modifier.height(8.dp))
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
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = if (isTotal) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium
        )
        Text(
            text = value,
            style = if (isTotal) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal
        )
    }
}
