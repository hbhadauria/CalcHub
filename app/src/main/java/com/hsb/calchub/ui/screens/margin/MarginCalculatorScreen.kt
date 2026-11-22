package com.hsb.calchub.ui.screens.margin

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
 * Screen for calculating margin requirements for trading.
 */
@Composable
fun MarginCalculatorScreen(onBackClick: () -> Unit) {
    var quantity by rememberSaveable { mutableDoubleStateOf(100.0) }
    var price by rememberSaveable { mutableDoubleStateOf(500.0) }
    var leverage by rememberSaveable { mutableDoubleStateOf(5.0) }
    val results = remember(quantity, price, leverage) {
        CalculatorLogic.calculateMargin(quantity, price, leverage)
    }
    val currencyFormat = rememberInrFormat(maxFractionDigits = 2)

    StandardResultsCalculatorLayout(
        title = "Margin Calculator",
        calculatorId = "margin",
        onBackClick = onBackClick,
        resultsContent = {
            ResultRow("Margin Required", currencyFormat.format(results.first), isTotal = true)
            ResultRow("Exposure", currencyFormat.format(results.second))
            ResultRow("Total Value", currencyFormat.format(results.third))
        },
        inputsContent = {
            CalculatorInput("Quantity", quantity, { quantity = it }, 1.0..10000.0, "Qty")
            CalculatorInput("Price per Share", price, { price = it }, 1.0..100000.0, "₹")
            CalculatorInput("Leverage", leverage, { leverage = it }, 1.0..20.0, "x")
        }
    )
}
