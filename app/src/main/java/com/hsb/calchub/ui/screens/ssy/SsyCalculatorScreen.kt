package com.hsb.calchub.ui.screens.ssy

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
 * Screen for Sukanya Samriddhi Yojana (SSY) calculation.
 */
@Composable
fun SsyCalculatorScreen(onBackClick: () -> Unit) {
    var yearlyDeposit by rememberSaveable { mutableDoubleStateOf(50000.0) }
    var interestRate by rememberSaveable { mutableDoubleStateOf(8.2) }
    var timePeriodYears by rememberSaveable { mutableDoubleStateOf(15.0) }

    val results = remember(yearlyDeposit, interestRate, timePeriodYears) {
        CalculatorLogic.calculateSSY(yearlyDeposit, interestRate, timePeriodYears)
    }

    StandardInvestmentCalculatorLayout(
        title = "SSY Calculator",
        calculatorId = "ssy",
        onBackClick = onBackClick,
        investedAmount = results.first,
        estimatedReturns = results.second,
        totalValue = results.third
    ) {
        CalculatorInput(
            label = "Yearly Deposit",
            value = yearlyDeposit,
            onValueChange = { yearlyDeposit = it },
            range = 250.0..150000.0,
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
            label = "Maturity Period (15y deposit)",
            value = timePeriodYears,
            onValueChange = { timePeriodYears = it },
            range = 15.0..21.0,
            symbol = "Yr"
        )
    }
}
