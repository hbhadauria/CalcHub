package com.hsb.calchub.ui.screens.stock_average

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hsb.calchub.domain.logic.CalculatorLogic
import com.hsb.calchub.ui.components.*
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockAverageCalculatorScreen(onBackClick: () -> Unit) {
    var firstQuantity by remember { mutableDoubleStateOf(100.0) }
    var firstPrice by remember { mutableDoubleStateOf(150.0) }
    var secondQuantity by remember { mutableDoubleStateOf(50.0) }
    var secondPrice by remember { mutableDoubleStateOf(120.0) }
    val results = CalculatorLogic.calculateStockAverage(firstQuantity, firstPrice, secondQuantity, secondPrice)
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Stock Average Calculator") },
                navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.Default.ArrowBack, null) } },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(Modifier.fillMaxSize().padding(innerPadding).padding(16.dp).verticalScroll(rememberScrollState())) {
            Text("First Purchase", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(vertical = 8.dp))
            CalculatorInput("Quantity", firstQuantity, { firstQuantity = it }, 1.0..10000.0, "Qty")
            CalculatorInput("Price per Share", firstPrice, { firstPrice = it }, 1.0..100000.0, "₹")
            
            Text("Second Purchase", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(vertical = 8.dp))
            CalculatorInput("Quantity", secondQuantity, { secondQuantity = it }, 1.0..10000.0, "Qty")
            CalculatorInput("Price per Share", secondPrice, { secondPrice = it }, 1.0..100000.0, "₹")
            
            Card(Modifier.fillMaxWidth().padding(vertical = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                Column(Modifier.padding(16.dp)) {
                    ResultRow("Total Quantity", String.format("%.0f shares", results.first))
                    ResultRow("Average Price", currencyFormat.format(results.second))
                    ResultRow("Total Investment", currencyFormat.format(results.third), true)
                }
            }
        }
    }
}
