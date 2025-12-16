package com.hsb.calchub.ui.screens.flat_vs_reducing

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
fun FlatVsReducingCalculatorScreen(onBackClick: () -> Unit) {
    var loanAmount by remember { mutableDoubleStateOf(500000.0) }
    var flatRate by remember { mutableDoubleStateOf(10.0) }
    var tenureYears by remember { mutableDoubleStateOf(5.0) }
    val results = CalculatorLogic.calculateFlatVsReducing(loanAmount, flatRate, tenureYears)
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN"))

    CalculatorScaffold(
        title = "Flat vs Reducing Rate",
        onBackClick = onBackClick,
        calculatorId = "flat_vs_reducing"
    ) { innerPadding ->
        Column(Modifier.fillMaxSize().padding(innerPadding).padding(16.dp).verticalScroll(rememberScrollState())) {
            CalculatorInput("Loan Amount", loanAmount, { loanAmount = it }, 10000.0..10000000.0, "₹")
            CalculatorInput("Flat Interest Rate", flatRate, { flatRate = it }, 1.0..30.0, "%")
            CalculatorInput("Tenure", tenureYears, { tenureYears = it }, 1.0..30.0, "Yr")
            
            NeonCard(Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
                Column(Modifier.padding(16.dp)) {
                    ResultRow("Flat Rate EMI", currencyFormat.format(results.first))
                    ResultRow("Reducing Rate EMI", currencyFormat.format(results.second))
                    ResultRow("Difference", currencyFormat.format(results.third), true)
                }
            }
        }
    }
}
