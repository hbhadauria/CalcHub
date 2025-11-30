package com.hsb.calchub.ui.screens.compound_interest

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompoundInterestCalculatorScreen(onBackClick: () -> Unit) {
    var principal by remember { mutableDoubleStateOf(100000.0) }
    var rate by remember { mutableDoubleStateOf(8.0) }
    var timeYears by remember { mutableDoubleStateOf(5.0) }
    var compoundingFrequency by remember { mutableDoubleStateOf(4.0) }
    val results = CalculatorLogic.calculateCompoundInterest(principal, rate, timeYears, compoundingFrequency.toInt())

    CalculatorScaffold(
        title = "Compound Interest Calculator",
        onBackClick = onBackClick,
        calculatorId = "compound_interest"
    ) { innerPadding ->
        Column(Modifier.fillMaxSize().padding(innerPadding).padding(16.dp).verticalScroll(rememberScrollState())) {
            CalculatorInput("Principal Amount", principal, { principal = it }, 1000.0..10000000.0, "₹")
            CalculatorInput("Interest Rate", rate, { rate = it }, 1.0..30.0, "%")
            CalculatorInput("Time Period", timeYears, { timeYears = it }, 1.0..30.0, "Yr")
            CalculatorInput("Compounding Frequency", compoundingFrequency, { compoundingFrequency = it }, 1.0..12.0, "/yr")
            
            DonutChart(listOf(
                DonutChartData(results.first, MaterialTheme.colorScheme.primary, "Principal"),
                DonutChartData(results.second, MaterialTheme.colorScheme.tertiary, "Interest")
            ))
            
            ResultCard(results.first, results.second, results.third)
        }
    }
}
