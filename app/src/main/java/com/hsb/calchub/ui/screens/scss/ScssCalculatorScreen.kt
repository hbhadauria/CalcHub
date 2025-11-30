package com.hsb.calchub.ui.screens.scss

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
fun ScssCalculatorScreen(onBackClick: () -> Unit) {
    var investment by remember { mutableDoubleStateOf(500000.0) }
    var interestRate by remember { mutableDoubleStateOf(8.2) }
    val results = CalculatorLogic.calculateSCSS(investment, interestRate)
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    CalculatorScaffold(
        title = "SCSS Calculator",
        onBackClick = onBackClick,
        calculatorId = "scss"
    ) { innerPadding ->
        Column(Modifier.fillMaxSize().padding(innerPadding).padding(16.dp).verticalScroll(rememberScrollState())) {
            CalculatorInput("Investment Amount", investment, { investment = it }, 1000.0..3000000.0, "₹")
            CalculatorInput("Interest Rate", interestRate, { interestRate = it }, 1.0..15.0, "%")
            
            DonutChart(listOf(
                DonutChartData(investment, MaterialTheme.colorScheme.primary, "Principal"),
                DonutChartData(results.second, MaterialTheme.colorScheme.tertiary, "Interest")
            ))
            
            NeonCard(Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
                Column(Modifier.padding(16.dp)) {
                    ResultRow("Investment", currencyFormat.format(results.first))
                    ResultRow("Total Interest (5 Yr)", currencyFormat.format(results.second))
                    ResultRow("Maturity Value", currencyFormat.format(results.third), true)
                }
            }
        }
    }
}
