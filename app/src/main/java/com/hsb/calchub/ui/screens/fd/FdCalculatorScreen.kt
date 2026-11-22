package com.hsb.calchub.ui.screens.fd

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
 * Screen for calculating Fixed Deposit (FD) returns.
 */
@Composable
fun FdCalculatorScreen(onBackClick: () -> Unit) {
    var totalInvestment by rememberSaveable { mutableDoubleStateOf(100000.0) }
    var rateOfInterest by rememberSaveable { mutableDoubleStateOf(6.5) }
    var timePeriodYears by rememberSaveable { mutableDoubleStateOf(5.0) }

    val results = remember(totalInvestment, rateOfInterest, timePeriodYears) {
        CalculatorLogic.calculateFD(totalInvestment, rateOfInterest, timePeriodYears)
    }

    StandardInvestmentCalculatorLayout(
        title = "FD Calculator",
        calculatorId = "fd",
        onBackClick = onBackClick,
        investedAmount = results.first,
        estimatedReturns = results.second,
        totalValue = results.third
    ) {
        CalculatorInput(
            label = "Total Investment",
            value = totalInvestment,
            onValueChange = { totalInvestment = it },
            range = 5000.0..10000000.0,
            symbol = "₹"
        )
        CalculatorInput(
            label = "Rate of Interest (p.a)",
            value = rateOfInterest,
            onValueChange = { rateOfInterest = it },
            range = 1.0..15.0,
            symbol = "%"
        )
        CalculatorInput(
            label = "Time Period",
            value = timePeriodYears,
            onValueChange = { timePeriodYears = it },
            range = 1.0..25.0,
            symbol = "Yr"
        )
    }
}
