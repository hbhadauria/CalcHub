package com.hsb.calchub.ui.screens.ppf

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
 * Screen for Public Provident Fund (PPF) calculation.
 */
@Composable
fun PpfCalculatorScreen(onBackClick: () -> Unit) {
    var yearlyInvestment by rememberSaveable { mutableDoubleStateOf(100000.0) }
    var interestRate by rememberSaveable { mutableDoubleStateOf(7.1) }
    var timePeriodYears by rememberSaveable { mutableDoubleStateOf(15.0) }

    val results = remember(yearlyInvestment, interestRate, timePeriodYears) {
        CalculatorLogic.calculatePPF(yearlyInvestment, interestRate, timePeriodYears)
    }

    StandardInvestmentCalculatorLayout(
        title = "PPF Calculator",
        calculatorId = "ppf",
        onBackClick = onBackClick,
        investedAmount = results.first,
        estimatedReturns = results.second,
        totalValue = results.third
    ) {
        CalculatorInput(
            label = "Yearly Investment",
            value = yearlyInvestment,
            onValueChange = { yearlyInvestment = it },
            range = 500.0..150000.0,
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
            range = 15.0..50.0,
            symbol = "Yr"
        )
    }
}
