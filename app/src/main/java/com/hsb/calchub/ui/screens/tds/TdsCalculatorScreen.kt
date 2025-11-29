package com.hsb.calchub.ui.screens.tds

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
fun TdsCalculatorScreen(onBackClick: () -> Unit) {
    var amount by remember { mutableDoubleStateOf(100000.0) }
    var tdsRate by remember { mutableDoubleStateOf(10.0) }
    val results = CalculatorLogic.calculateTDS(amount, tdsRate)
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TDS Calculator") },
                navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.Default.ArrowBack, null) } },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(Modifier.fillMaxSize().padding(innerPadding).padding(16.dp).verticalScroll(rememberScrollState())) {
            CalculatorInput("Gross Amount", amount, { amount = it }, 1000.0..10000000.0, "₹")
            CalculatorInput("TDS Rate", tdsRate, { tdsRate = it }, 1.0..30.0, "%")
            
            DonutChart(listOf(
                DonutChartData(results.second, MaterialTheme.colorScheme.error, "TDS"),
                DonutChartData(results.third, MaterialTheme.colorScheme.primary, "Net Amount")
            ))
            
            Card(Modifier.fillMaxWidth().padding(vertical = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                Column(Modifier.padding(16.dp)) {
                    ResultRow("Gross Amount", currencyFormat.format(results.first))
                    ResultRow("TDS Deducted", currencyFormat.format(results.second))
                    ResultRow("Net Amount", currencyFormat.format(results.third), true)
                }
            }
        }
    }
}
