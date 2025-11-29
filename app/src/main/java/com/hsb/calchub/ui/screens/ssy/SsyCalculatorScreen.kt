package com.hsb.calchub.ui.screens.ssy

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hsb.calchub.domain.logic.CalculatorLogic
import com.hsb.calchub.ui.components.CalculatorInput
import com.hsb.calchub.ui.components.DonutChart
import com.hsb.calchub.ui.components.DonutChartData
import com.hsb.calchub.ui.components.ResultCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SsyCalculatorScreen(onBackClick: () -> Unit) {
    var yearlyDeposit by remember { mutableDoubleStateOf(50000.0) }
    var interestRate by remember { mutableDoubleStateOf(8.2) }
    var timePeriodYears by remember { mutableDoubleStateOf(15.0) }

    val results = CalculatorLogic.calculateSSY(yearlyDeposit, interestRate, timePeriodYears)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SSY Calculator") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            CalculatorInput(
                label = "Yearly Deposit",
                value = yearlyDeposit,
                onValueChange = { yearlyDeposit = it },
                range = 250.0..150000.0,
                symbol = "₹"
            )

            CalculatorInput(
                label = "Interest Rate (p.a)",
                value = interestRate,
                onValueChange = { interestRate = it },
                range = 1.0..15.0,
                symbol = "%"
            )

            CalculatorInput(
                label = "Time Period",
                value = timePeriodYears,
                onValueChange = { timePeriodYears = it },
                range = 15.0..21.0,
                symbol = "Yr"
            )

            DonutChart(
                data = listOf(
                    DonutChartData(results.first, MaterialTheme.colorScheme.primary, "Invested"),
                    DonutChartData(results.second, MaterialTheme.colorScheme.tertiary, "Returns")
                )
            )

            ResultCard(
                investedAmount = results.first,
                estimatedReturns = results.second,
                totalValue = results.third
            )
        }
    }
}
