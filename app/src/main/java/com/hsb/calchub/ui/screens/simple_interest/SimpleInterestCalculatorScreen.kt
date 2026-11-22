package com.hsb.calchub.ui.screens.simple_interest

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
 * Screen for Simple Interest calculation.
 */
@Composable
fun SimpleInterestCalculatorScreen(onBackClick: () -> Unit) {
    var principal by rememberSaveable { mutableDoubleStateOf(100000.0) }
    var rate by rememberSaveable { mutableDoubleStateOf(8.0) }
    var timeYears by rememberSaveable { mutableDoubleStateOf(5.0) }
    val results = remember(principal, rate, timeYears) {
        CalculatorLogic.calculateSimpleInterest(principal, rate, timeYears)
    }

    StandardInvestmentCalculatorLayout(
        title = "Simple Interest Calculator",
        calculatorId = "simple_interest",
        onBackClick = onBackClick,
        investedAmount = results.first,
        estimatedReturns = results.second,
        totalValue = results.third,
        investedLabel = "Principal",
        returnsLabel = "Interest",
        donutInvestedLabel = "Principal",
        donutReturnsLabel = "Interest"
    ) {
        CalculatorInput("Principal Amount", principal, { principal = it }, 1000.0..10000000.0, "₹")
        CalculatorInput("Interest Rate", rate, { rate = it }, 1.0..30.0, "%")
        CalculatorInput("Time Period", timeYears, { timeYears = it }, 1.0..30.0, "Yr")
    }
}
