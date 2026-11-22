package com.hsb.calchub.ui.screens.flat_vs_reducing

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
 * Screen for comparing Flat Rate vs Reducing Rate loans.
 */
@Composable
fun FlatVsReducingCalculatorScreen(onBackClick: () -> Unit) {
    var loanAmount by rememberSaveable { mutableDoubleStateOf(500000.0) }
    var flatRate by rememberSaveable { mutableDoubleStateOf(10.0) }
    var tenureYears by rememberSaveable { mutableDoubleStateOf(5.0) }
    val results = remember(loanAmount, flatRate, tenureYears) {
        CalculatorLogic.calculateFlatVsReducing(loanAmount, flatRate, tenureYears)
    }
    val currencyFormat = rememberInrFormat(maxFractionDigits = 2)

    StandardResultsCalculatorLayout(
        title = "Flat vs Reducing Rate",
        calculatorId = "flat_vs_reducing",
        onBackClick = onBackClick,
        resultsContent = {
            ResultRow("Flat Rate EMI", currencyFormat.format(results.first))
            ResultRow("Reducing Rate EMI", currencyFormat.format(results.second))
            ResultRow("Monthly Savings (Reducing)", currencyFormat.format(results.third), isTotal = true)
        },
        inputsContent = {
            CalculatorInput("Loan Amount", loanAmount, { loanAmount = it }, 10000.0..10000000.0, "₹")
            CalculatorInput("Annual Interest Rate", flatRate, { flatRate = it }, 1.0..30.0, "%")
            CalculatorInput("Tenure", tenureYears, { tenureYears = it }, 1.0..30.0, "Yr")
        }
    )
}
