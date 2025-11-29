package com.hsb.calchub.ui.screens.inflation

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
fun InflationCalculatorScreen(onBackClick: () -> Unit) {
    var currentPrice by remember { mutableDoubleStateOf(10000.0) }
    var inflationRate by remember { mutableDoubleStateOf(6.0) }
    var years by remember { mutableDoubleStateOf(10.0) }
    val results = CalculatorLogic.calculateInflation(currentPrice, inflationRate, years)
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inflation Calculator") },
                navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.Default.ArrowBack, null) } },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(Modifier.fillMaxSize().padding(innerPadding).padding(16.dp).verticalScroll(rememberScrollState())) {
            CalculatorInput("Current Price", currentPrice, { currentPrice = it }, 100.0..10000000.0, "₹")
            CalculatorInput("Inflation Rate", inflationRate, { inflationRate = it }, 1.0..20.0, "%")
            CalculatorInput("Time Period", years, { years = it }, 1.0..50.0, "Yr")
            
            DonutChart(listOf(
                DonutChartData(currentPrice, MaterialTheme.colorScheme.primary, "Current"),
                DonutChartData(results.second, MaterialTheme.colorScheme.tertiary, "Increase")
            ))
            
            Card(Modifier.fillMaxWidth().padding(vertical = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                Column(Modifier.padding(16.dp)) {
                    ResultRow("Current Price", currencyFormat.format(results.first))
                    ResultRow("Price Increase", currencyFormat.format(results.second))
                    ResultRow("Future Price", currencyFormat.format(results.third), true)
                }
            }
        }
    }
}
