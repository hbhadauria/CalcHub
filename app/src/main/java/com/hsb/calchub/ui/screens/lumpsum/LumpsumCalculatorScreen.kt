package com.hsb.calchub.ui.screens.lumpsum

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hsb.calchub.domain.logic.CalculatorLogic
import com.hsb.calchub.ui.components.CalculatorInput
import com.hsb.calchub.ui.components.CalculatorScaffold
import com.hsb.calchub.ui.components.DonutChart
import com.hsb.calchub.ui.components.DonutChartData
import com.hsb.calchub.ui.components.InflationAdjustedSummaryCard
import com.hsb.calchub.ui.components.InflationAdjustmentControl
import com.hsb.calchub.ui.components.NeonCard
import com.hsb.calchub.ui.components.ResultCard

/**
 * Screen for calculating Lumpsum investment returns.
 * Shows investment growth over time with optional inflation adjustment to today's purchasing power.
 *
 * @param onBackClick Callback triggered when the back button is clicked.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LumpsumCalculatorScreen(onBackClick: () -> Unit) {
    var totalInvestment by rememberSaveable { mutableDoubleStateOf(100000.0) }
    var expectedReturnRate by rememberSaveable { mutableDoubleStateOf(12.0) }
    var timePeriodYears by rememberSaveable { mutableDoubleStateOf(10.0) }
    var adjustForInflation by rememberSaveable { mutableStateOf(false) }
    var inflationRate by rememberSaveable { mutableDoubleStateOf(6.0) }

    val results = remember(totalInvestment, expectedReturnRate, timePeriodYears) {
        CalculatorLogic.calculateLumpsum(totalInvestment, expectedReturnRate, timePeriodYears)
    }
    val realTotalValue = remember(results.third, inflationRate, timePeriodYears, adjustForInflation) {
        if (adjustForInflation) {
            CalculatorLogic.adjustForInflation(results.third, inflationRate, timePeriodYears)
        } else {
            results.third
        }
    }

    CalculatorScaffold(
        title = "Lumpsum Calculator",
        onBackClick = onBackClick,
        calculatorId = "lumpsum"
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CalculatorInput(
                label = "Total Investment",
                value = totalInvestment,
                onValueChange = { totalInvestment = it },
                range = 1000.0..10000000.0,
                symbol = "₹"
            )

            CalculatorInput(
                label = "Expected Return Rate (p.a)",
                value = expectedReturnRate,
                onValueChange = { expectedReturnRate = it },
                range = 1.0..30.0,
                symbol = "%"
            )

            CalculatorInput(
                label = "Time Period",
                value = timePeriodYears,
                onValueChange = { timePeriodYears = it },
                range = 1.0..40.0,
                symbol = "Yr"
            )

            NeonCard {
                InflationAdjustmentControl(
                    enabled = adjustForInflation,
                    onEnabledChange = { adjustForInflation = it },
                    inflationRate = inflationRate,
                    onInflationRateChange = { inflationRate = it }
                )
            }

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

            if (adjustForInflation) {
                InflationAdjustedSummaryCard(
                    nominalValue = results.third,
                    realValue = realTotalValue,
                    investedAmount = results.first,
                    inflationRate = inflationRate,
                    years = timePeriodYears
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
