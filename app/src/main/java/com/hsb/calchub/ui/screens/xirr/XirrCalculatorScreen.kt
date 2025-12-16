package com.hsb.calchub.ui.screens.xirr

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
fun XirrCalculatorScreen(onBackClick: () -> Unit) {
    var investment1 by remember { mutableDoubleStateOf(10000.0) }
    var investment2 by remember { mutableDoubleStateOf(10000.0) }
    var investment3 by remember { mutableDoubleStateOf(10000.0) }
    var returns by remember { mutableDoubleStateOf(35000.0) }
    val results = CalculatorLogic.calculateXIRR(listOf(investment1, investment2, investment3), returns)
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN"))

    CalculatorScaffold(
        title = "XIRR Calculator",
        onBackClick = onBackClick,
        calculatorId = "xirr"
    ) { innerPadding ->
        Column(Modifier.fillMaxSize().padding(innerPadding).padding(16.dp).verticalScroll(rememberScrollState())) {
            Text("Investments", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(vertical = 8.dp))
            CalculatorInput("Investment 1", investment1, { investment1 = it }, 1000.0..1000000.0, "₹")
            CalculatorInput("Investment 2", investment2, { investment2 = it }, 1000.0..1000000.0, "₹")
            CalculatorInput("Investment 3", investment3, { investment3 = it }, 1000.0..1000000.0, "₹")
            CalculatorInput("Current Value", returns, { returns = it }, 1000.0..5000000.0, "₹")
            
            NeonCard(Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
                Column(Modifier.padding(16.dp)) {
                    ResultRow("Total Invested", currencyFormat.format(results.first))
                    ResultRow("Profit", currencyFormat.format(results.second))
                    ResultRow("Return %", String.format("%.2f%%", results.third), true)
                }
            }
        }
    }
}
