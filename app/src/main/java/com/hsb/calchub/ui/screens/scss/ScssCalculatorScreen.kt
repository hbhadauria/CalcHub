package com.hsb.calchub.ui.screens.scss

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.hsb.calchub.domain.logic.CalculatorLogic
import com.hsb.calchub.ui.components.CalculatorInput
import com.hsb.calchub.ui.components.InvestedReturnsDonutCard
import com.hsb.calchub.ui.components.ResultRow
import com.hsb.calchub.ui.components.StandardResultsCalculatorLayout
import com.hsb.calchub.ui.components.rememberInrFormat

/**
 * Screen for Senior Citizen Savings Scheme (SCSS) calculation.
 */
@Composable
fun ScssCalculatorScreen(onBackClick: () -> Unit) {
    var investment by rememberSaveable { mutableDoubleStateOf(500000.0) }
    var interestRate by rememberSaveable { mutableDoubleStateOf(8.2) }
    val results = remember(investment, interestRate) {
        CalculatorLogic.calculateSCSS(investment, interestRate)
    }
    val currencyFormat = rememberInrFormat(maxFractionDigits = 2)

    StandardResultsCalculatorLayout(
        title = "SCSS Calculator",
        calculatorId = "scss",
        onBackClick = onBackClick,
        resultsContent = {
            ResultRow("Investment", currencyFormat.format(results.first))
            ResultRow("Total Interest (5 Yr)", currencyFormat.format(results.second))
            ResultRow("Maturity Value", currencyFormat.format(results.third), isTotal = true)
        },
        inputsContent = {
            CalculatorInput("Investment Amount", investment, { investment = it }, 1000.0..3000000.0, "₹")
            CalculatorInput("Interest Rate", interestRate, { interestRate = it }, 1.0..15.0, "%")
        },
        footerContent = {
            InvestedReturnsDonutCard(
                invested = investment,
                returns = results.second,
                investedLabel = "Principal",
                returnsLabel = "Interest"
            )
        }
    )
}
