package com.hsb.calchub.ui.screens.rd

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
 * Screen for Recurring Deposit (RD) calculation.
 */
@Composable
fun RdCalculatorScreen(onBackClick: () -> Unit) {
    var monthlyDeposit by rememberSaveable { mutableDoubleStateOf(5000.0) }
    var interestRate by rememberSaveable { mutableDoubleStateOf(6.5) }
    var timePeriodYears by rememberSaveable { mutableDoubleStateOf(5.0) }

    val results = remember(monthlyDeposit, interestRate, timePeriodYears) {
        CalculatorLogic.calculateRD(monthlyDeposit, interestRate, timePeriodYears)
    }

    StandardInvestmentCalculatorLayout(
        title = "RD Calculator",
        calculatorId = "rd",
        onBackClick = onBackClick,
        investedAmount = results.first,
        estimatedReturns = results.second,
        totalValue = results.third
    ) {
        CalculatorInput(
            label = "Monthly Deposit",
            value = monthlyDeposit,
            onValueChange = { monthlyDeposit = it },
            range = 100.0..50000.0,
            symbol = "₹"
        )
        CalculatorInput(
            label = "Interest Rate (p.a)",
            value = interestRate,
            onValueChange = { interestRate = it },
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
