package com.hsb.calchub.ui.screens.gratuity

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
 * Screen for calculating Gratuity benefits based on salary and tenure.
 */
@Composable
fun GratuityCalculatorScreen(onBackClick: () -> Unit) {
    var lastSalary by rememberSaveable { mutableDoubleStateOf(50000.0) }
    var yearsOfService by rememberSaveable { mutableDoubleStateOf(10.0) }
    val results = remember(lastSalary, yearsOfService) {
        CalculatorLogic.calculateGratuity(lastSalary, yearsOfService)
    }
    val currencyFormat = rememberInrFormat(maxFractionDigits = 2)

    StandardResultsCalculatorLayout(
        title = "Gratuity Calculator",
        calculatorId = "gratuity",
        onBackClick = onBackClick,
        resultsContent = {
            ResultRow("Total Gratuity", currencyFormat.format(results.first), isTotal = true)
            ResultRow("Tax Free Amount", currencyFormat.format(results.second))
            ResultRow("Taxable Amount", currencyFormat.format(results.third))
        },
        inputsContent = {
            CalculatorInput("Last Drawn Salary", lastSalary, { lastSalary = it }, 10000.0..500000.0, "₹")
            CalculatorInput("Years of Service", yearsOfService, { yearsOfService = it }, 1.0..40.0, "Yr")
        }
    )
}
