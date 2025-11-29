package com.hsb.calchub.ui.screens.cagr

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
fun CagrCalculatorScreen(onBackClick: () -> Unit) {
    var initialValue by remember { mutableDoubleStateOf(100000.0) }
    var finalValue by remember { mutableDoubleStateOf(200000.0) }
    var years by remember { mutableDoubleStateOf(5.0) }
    val results = CalculatorLogic.calculateCAGR(initialValue, finalValue, years)
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CAGR Calculator") },
                navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.Default.ArrowBack, null) } },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(Modifier.fillMaxSize().padding(innerPadding).padding(16.dp).verticalScroll(rememberScrollState())) {
            CalculatorInput("Initial Value", initialValue, { initialValue = it }, 1000.0..10000000.0, "₹")
            CalculatorInput("Final Value", finalValue, { finalValue = it }, 1000.0..10000000.0, "₹")
            CalculatorInput("Time Period", years, { years = it }, 1.0..30.0, "Yr")
            
            DonutChart(listOf(
                DonutChartData(initialValue, MaterialTheme.colorScheme.primary, "Initial"),
                DonutChartData(results.third, MaterialTheme.colorScheme.tertiary, "Gain")
            ))
            
            Card(Modifier.fillMaxWidth().padding(vertical = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                Column(Modifier.padding(16.dp)) {
                    ResultRow("CAGR", String.format("%.2f%%", results.first))
                    ResultRow("Total Return", String.format("%.2f%%", results.second))
                    ResultRow("Absolute Gain", currencyFormat.format(results.third), true)
                }
            }
        }
    }
}
