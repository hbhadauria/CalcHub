package com.hsb.calchub.ui.screens.simple_interest

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
import com.hsb.calchub.ui.components.CalculatorInput
import com.hsb.calchub.ui.components.CalculatorScaffold
import com.hsb.calchub.ui.components.DonutChart
import com.hsb.calchub.ui.components.DonutChartData
import com.hsb.calchub.ui.components.ResultCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleInterestCalculatorScreen(onBackClick: () -> Unit) {
    var principal by remember { mutableDoubleStateOf(100000.0) }
    var rate by remember { mutableDoubleStateOf(8.0) }
    var timeYears by remember { mutableDoubleStateOf(5.0) }
    val results = CalculatorLogic.calculateSimpleInterest(principal, rate, timeYears)

    CalculatorScaffold(
        title = "Simple Interest Calculator",
        onBackClick = onBackClick,
        calculatorId = "simple_interest"
    ) { innerPadding ->
        Column(Modifier.fillMaxSize().padding(innerPadding).padding(16.dp).verticalScroll(rememberScrollState())) {
            CalculatorInput("Principal Amount", principal, { principal = it }, 1000.0..10000000.0, "₹")
            CalculatorInput("Interest Rate", rate, { rate = it }, 1.0..30.0, "%")
            CalculatorInput("Time Period", timeYears, { timeYears = it }, 1.0..30.0, "Yr")
            
            DonutChart(listOf(
                DonutChartData(results.first, MaterialTheme.colorScheme.primary, "Principal"),
                DonutChartData(results.second, MaterialTheme.colorScheme.tertiary, "Interest")
            ))
            
            ResultCard(results.first, results.second, results.third)
        }
    }
}
