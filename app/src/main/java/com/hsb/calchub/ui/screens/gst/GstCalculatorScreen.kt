package com.hsb.calchub.ui.screens.gst

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.hsb.calchub.domain.logic.CalculatorLogic
import com.hsb.calchub.ui.components.CalculatorInput
import com.hsb.calchub.ui.components.ResultRow
import com.hsb.calchub.ui.components.StandardResultsCalculatorLayout
import com.hsb.calchub.ui.components.ThemedCheckboxRow
import com.hsb.calchub.ui.components.rememberInrFormat

/**
 * Screen for calculating Goods and Services Tax (GST).
 */
@Composable
fun GstCalculatorScreen(onBackClick: () -> Unit) {
    var amount by rememberSaveable { mutableDoubleStateOf(10000.0) }
    var gstRate by rememberSaveable { mutableDoubleStateOf(18.0) }
    var isInclusive by rememberSaveable { mutableStateOf(false) }
    val results = remember(amount, gstRate, isInclusive) {
        CalculatorLogic.calculateGST(amount, gstRate, isInclusive)
    }
    val currencyFormat = rememberInrFormat(maxFractionDigits = 2)

    StandardResultsCalculatorLayout(
        title = "GST Calculator",
        calculatorId = "gst",
        onBackClick = onBackClick,
        resultsContent = {
            ResultRow("Base Amount", currencyFormat.format(results.first))
            ResultRow("GST Amount", currencyFormat.format(results.second))
            ResultRow("Total Amount", currencyFormat.format(results.third), isTotal = true)
        },
        inputsContent = {
            CalculatorInput("Amount", amount, { amount = it }, 100.0..10000000.0, "₹")
            CalculatorInput("GST Rate", gstRate, { gstRate = it }, 0.0..28.0, "%")
            ThemedCheckboxRow(
                label = "GST Inclusive",
                checked = isInclusive,
                onCheckedChange = { isInclusive = it }
            )
        }
    )
}
