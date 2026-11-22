package com.hsb.calchub.ui.screens.tds

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
 * Screen for Tax Deducted at Source (TDS) calculation.
 */
@Composable
fun TdsCalculatorScreen(onBackClick: () -> Unit) {
    var amount by rememberSaveable { mutableDoubleStateOf(100000.0) }
    var tdsRate by rememberSaveable { mutableDoubleStateOf(10.0) }
    val results = remember(amount, tdsRate) {
        CalculatorLogic.calculateTDS(amount, tdsRate)
    }
    val currencyFormat = rememberInrFormat(maxFractionDigits = 2)

    StandardResultsCalculatorLayout(
        title = "TDS Calculator",
        calculatorId = "tds",
        onBackClick = onBackClick,
        resultsContent = {
            ResultRow("Gross Amount", currencyFormat.format(results.first))
            ResultRow("TDS Deducted", currencyFormat.format(results.second))
            ResultRow("Net Amount", currencyFormat.format(results.third), isTotal = true)
        },
        inputsContent = {
            CalculatorInput("Gross Amount", amount, { amount = it }, 1000.0..10000000.0, "₹")
            CalculatorInput("TDS Rate", tdsRate, { tdsRate = it }, 1.0..30.0, "%")
        },
        footerContent = {
            InvestedReturnsDonutCard(
                invested = results.third,
                returns = results.second,
                investedLabel = "Net Amount",
                returnsLabel = "TDS",
                useGainLossColors = true
            )
        }
    )
}
