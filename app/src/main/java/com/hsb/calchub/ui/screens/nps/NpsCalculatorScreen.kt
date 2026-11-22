package com.hsb.calchub.ui.screens.nps

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.hsb.calchub.domain.logic.CalculatorLogic
import com.hsb.calchub.ui.components.CalculatorInput
import com.hsb.calchub.ui.components.StandardInvestmentCalculatorLayout

/**
 * Screen for calculating National Pension System (NPS) corpus.
 */
@Composable
fun NpsCalculatorScreen(onBackClick: () -> Unit) {
    var monthlyContribution by rememberSaveable { mutableDoubleStateOf(5000.0) }
    var expectedReturnRate by rememberSaveable { mutableDoubleStateOf(10.0) }
    var currentAge by rememberSaveable { mutableDoubleStateOf(30.0) }
    var retirementAge by rememberSaveable { mutableDoubleStateOf(60.0) }

    val safeRetirementAge = retirementAge.coerceAtLeast(currentAge + 1.0)
    val results = remember(monthlyContribution, expectedReturnRate, currentAge, safeRetirementAge) {
        CalculatorLogic.calculateNPS(monthlyContribution, expectedReturnRate, currentAge, safeRetirementAge)
    }

    StandardInvestmentCalculatorLayout(
        title = "NPS Calculator",
        calculatorId = "nps",
        onBackClick = onBackClick,
        investedAmount = results.first,
        estimatedReturns = results.second,
        totalValue = results.third
    ) {
        CalculatorInput(
            label = "Monthly Contribution",
            value = monthlyContribution,
            onValueChange = { monthlyContribution = it },
            range = 500.0..50000.0,
            symbol = "₹"
        )
        CalculatorInput(
            label = "Expected Return Rate (p.a)",
            value = expectedReturnRate,
            onValueChange = { expectedReturnRate = it },
            range = 1.0..20.0,
            symbol = "%"
        )
        CalculatorInput(
            label = "Current Age",
            value = currentAge,
            onValueChange = { next ->
                currentAge = next
                if (retirementAge <= next) {
                    retirementAge = (next + 1.0).coerceAtMost(70.0)
                }
            },
            range = 18.0..55.0,
            symbol = "Yr"
        )
        CalculatorInput(
            label = "Retirement Age",
            value = safeRetirementAge,
            onValueChange = { retirementAge = it.coerceAtLeast(currentAge + 1.0) },
            range = (currentAge + 1.0).coerceAtMost(69.0)..70.0,
            symbol = "Yr"
        )
    }
}
