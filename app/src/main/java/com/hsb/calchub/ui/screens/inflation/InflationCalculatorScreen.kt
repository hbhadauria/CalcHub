package com.hsb.calchub.ui.screens.inflation

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
 * Screen for calculating the impact of inflation over time.
 */
@Composable
fun InflationCalculatorScreen(onBackClick: () -> Unit) {
    var currentPrice by rememberSaveable { mutableDoubleStateOf(10000.0) }
    var inflationRate by rememberSaveable { mutableDoubleStateOf(6.0) }
    var years by rememberSaveable { mutableDoubleStateOf(10.0) }
    val results = remember(currentPrice, inflationRate, years) {
        CalculatorLogic.calculateInflation(currentPrice, inflationRate, years)
    }
    val currencyFormat = rememberInrFormat(maxFractionDigits = 2)

    StandardResultsCalculatorLayout(
        title = "Inflation Calculator",
        calculatorId = "inflation",
        onBackClick = onBackClick,
        resultsContent = {
            ResultRow("Current Price", currencyFormat.format(results.first))
            ResultRow("Price Increase", currencyFormat.format(results.second))
            ResultRow("Future Price", currencyFormat.format(results.third), isTotal = true)
        },
        inputsContent = {
            CalculatorInput("Current Price", currentPrice, { currentPrice = it }, 100.0..10000000.0, "₹")
            CalculatorInput("Inflation Rate", inflationRate, { inflationRate = it }, 1.0..20.0, "%")
            CalculatorInput("Time Period", years, { years = it }, 1.0..50.0, "Yr")
        },
        footerContent = {
            InvestedReturnsDonutCard(
                invested = currentPrice,
                returns = results.second,
                investedLabel = "Current",
                returnsLabel = "Increase"
            )
        }
    )
}
