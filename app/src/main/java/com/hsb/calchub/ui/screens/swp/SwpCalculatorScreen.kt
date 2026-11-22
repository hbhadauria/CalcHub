package com.hsb.calchub.ui.screens.swp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hsb.calchub.domain.logic.CalculatorLogic
import com.hsb.calchub.ui.components.CalculatorInput
import com.hsb.calchub.ui.components.CalculatorScrollScaffold
import com.hsb.calchub.ui.components.NeonCard
import com.hsb.calchub.ui.components.ResultRow
import com.hsb.calchub.ui.components.rememberInrFormat

/**
 * Screen for Systematic Withdrawal Plan (SWP) calculation.
 */
@Composable
fun SwpCalculatorScreen(onBackClick: () -> Unit) {
    var totalInvestment by rememberSaveable { mutableDoubleStateOf(500000.0) }
    var withdrawalPerMonth by rememberSaveable { mutableDoubleStateOf(5000.0) }
    var expectedReturnRate by rememberSaveable { mutableDoubleStateOf(8.0) }
    var timePeriodYears by rememberSaveable { mutableDoubleStateOf(10.0) }

    val results = remember(totalInvestment, withdrawalPerMonth, expectedReturnRate, timePeriodYears) {
        CalculatorLogic.calculateSWP(totalInvestment, withdrawalPerMonth, expectedReturnRate, timePeriodYears)
    }
    val currencyFormat = rememberInrFormat()

    CalculatorScrollScaffold(
        title = "SWP Calculator",
        onBackClick = onBackClick,
        calculatorId = "swp"
    ) {
        NeonCard(modifier = Modifier.fillMaxWidth(), useGradientBorder = true) {
            ResultRow("Initial Investment", currencyFormat.format(results.first))
            ResultRow("Total Withdrawn", currencyFormat.format(results.second))
            ResultRow("Final Balance", currencyFormat.format(results.third), isTotal = true)
        }

        NeonCard {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                CalculatorInput(
                    label = "Total Investment",
                    value = totalInvestment,
                    onValueChange = { totalInvestment = it },
                    range = 10000.0..10000000.0,
                    symbol = "₹"
                )
                CalculatorInput(
                    label = "Withdrawal per Month",
                    value = withdrawalPerMonth,
                    onValueChange = { withdrawalPerMonth = it },
                    range = 500.0..100000.0,
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
                    range = 1.0..30.0,
                    symbol = "Yr"
                )
            }
        }
    }
}
