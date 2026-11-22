package com.hsb.calchub.ui.screens.epf

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
 * Screen for calculating Employee Provident Fund (EPF) returns.
 */
@Composable
fun EpfCalculatorScreen(onBackClick: () -> Unit) {
    var monthlyContribution by rememberSaveable { mutableDoubleStateOf(5000.0) }
    var interestRate by rememberSaveable { mutableDoubleStateOf(8.25) }
    var timePeriodYears by rememberSaveable { mutableDoubleStateOf(20.0) }

    val results = remember(monthlyContribution, interestRate, timePeriodYears) {
        CalculatorLogic.calculateEPF(monthlyContribution, interestRate, timePeriodYears)
    }

    StandardInvestmentCalculatorLayout(
        title = "EPF Calculator",
        calculatorId = "epf",
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
            range = 1.0..40.0,
            symbol = "Yr"
        )
    }
}
