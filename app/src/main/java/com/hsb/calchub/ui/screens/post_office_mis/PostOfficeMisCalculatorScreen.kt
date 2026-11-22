package com.hsb.calchub.ui.screens.post_office_mis

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
 * Screen for Post Office Monthly Income Scheme (MIS) calculation.
 */
@Composable
fun PostOfficeMisCalculatorScreen(onBackClick: () -> Unit) {
    var investment by rememberSaveable { mutableDoubleStateOf(450000.0) }
    var interestRate by rememberSaveable { mutableDoubleStateOf(7.4) }
    val results = remember(investment, interestRate) {
        CalculatorLogic.calculatePostOfficeMIS(investment, interestRate)
    }
    val currencyFormat = rememberInrFormat(maxFractionDigits = 2)

    StandardResultsCalculatorLayout(
        title = "Post Office MIS",
        calculatorId = "post_office_mis",
        onBackClick = onBackClick,
        resultsContent = {
            ResultRow("Investment", currencyFormat.format(results.first))
            ResultRow("Monthly Income", currencyFormat.format(results.second), isTotal = true)
            ResultRow("Total Income (5 Yr)", currencyFormat.format(results.third))
        },
        inputsContent = {
            CalculatorInput("Investment Amount", investment, { investment = it }, 1000.0..900000.0, "₹")
            CalculatorInput("Interest Rate", interestRate, { interestRate = it }, 1.0..15.0, "%")
        }
    )
}
