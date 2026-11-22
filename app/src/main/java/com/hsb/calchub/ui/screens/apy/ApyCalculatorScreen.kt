package com.hsb.calchub.ui.screens.apy

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.hsb.calchub.domain.logic.CalculatorLogic
import com.hsb.calchub.ui.components.CalculatorInput
import com.hsb.calchub.ui.components.ResultRow
import com.hsb.calchub.ui.components.StandardResultsCalculatorLayout
import com.hsb.calchub.ui.components.rememberInrFormat

/**
 * Screen for calculating Atal Pension Yojana (APY) contributions and returns.
 */
@Composable
fun ApyCalculatorScreen(onBackClick: () -> Unit) {
    var currentAge by rememberSaveable { mutableDoubleStateOf(25.0) }
    var pensionAmount by rememberSaveable { mutableDoubleStateOf(5000.0) }
    val results = remember(currentAge, pensionAmount) {
        CalculatorLogic.calculateAPY(currentAge, pensionAmount)
    }
    val currencyFormat = rememberInrFormat(maxFractionDigits = 2)

    StandardResultsCalculatorLayout(
        title = "APY Calculator",
        calculatorId = "apy",
        onBackClick = onBackClick,
        resultsContent = {
            ResultRow("Monthly Contribution", currencyFormat.format(results.first), isTotal = true)
            ResultRow("Total Contribution", currencyFormat.format(results.second))
            ResultRow("Monthly Pension at 60", currencyFormat.format(results.third))
        },
        inputsContent = {
            CalculatorInput("Current Age", currentAge, { currentAge = it }, 18.0..40.0, "Yr")
            CalculatorInput("Desired Pension", pensionAmount, { pensionAmount = it }, 1000.0..5000.0, "₹")
        }
    )
}
