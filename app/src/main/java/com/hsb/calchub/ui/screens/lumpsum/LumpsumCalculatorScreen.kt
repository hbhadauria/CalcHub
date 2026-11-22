package com.hsb.calchub.ui.screens.lumpsum

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.hsb.calchub.domain.logic.CalculatorLogic
import com.hsb.calchub.ui.components.CalculatorInput
import com.hsb.calchub.ui.components.CalculatorScrollScaffold
import com.hsb.calchub.ui.components.InflationAdjustedSummaryCard
import com.hsb.calchub.ui.components.InflationAdjustmentControl
import com.hsb.calchub.ui.components.InvestedReturnsDonutCard
import com.hsb.calchub.ui.components.NeonCard
import com.hsb.calchub.ui.components.ResultCard
import com.hsb.calchub.ui.theme.LocalCalcHubTokens

/**
 * Screen for calculating Lumpsum investment returns.
 * Shows result verdict first, followed by compact inputs with optional inflation adjustment,
 * and breakdown chart below.
 */
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

    CalculatorScrollScaffold(
        title = "Lumpsum Calculator",
        onBackClick = onBackClick,
        calculatorId = "lumpsum"
    ) {
        val tokens = LocalCalcHubTokens.current
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

        NeonCard {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
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
                HorizontalDivider(color = tokens.cardBorder)
                InflationAdjustmentControl(
                    enabled = adjustForInflation,
                    onEnabledChange = { adjustForInflation = it },
                    inflationRate = inflationRate,
                    onInflationRateChange = { inflationRate = it }
                )
            }
        }

        InvestedReturnsDonutCard(
            invested = results.first,
            returns = results.second
        )
    }
}
