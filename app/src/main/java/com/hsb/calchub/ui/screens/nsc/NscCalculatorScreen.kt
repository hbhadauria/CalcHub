package com.hsb.calchub.ui.screens.nsc

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.hsb.calchub.domain.logic.CalculatorLogic
import com.hsb.calchub.ui.components.CalculatorInput
import com.hsb.calchub.ui.components.StandardInvestmentCalculatorLayout
import com.hsb.calchub.ui.theme.LocalCalcHubTokens

/**
 * Screen for calculating National Savings Certificate (NSC) returns.
 */
@Composable
fun NscCalculatorScreen(onBackClick: () -> Unit) {
    var investment by rememberSaveable { mutableDoubleStateOf(100000.0) }
    var interestRate by rememberSaveable { mutableDoubleStateOf(7.7) }

    val results = remember(investment, interestRate) {
        CalculatorLogic.calculateNSC(investment, interestRate)
    }

    StandardInvestmentCalculatorLayout(
        title = "NSC Calculator",
        calculatorId = "nsc",
        onBackClick = onBackClick,
        investedAmount = results.first,
        estimatedReturns = results.second,
        totalValue = results.third
    ) {
        val tokens = LocalCalcHubTokens.current
        CalculatorInput(
            label = "Investment Amount",
            value = investment,
            onValueChange = { investment = it },
            range = 1000.0..10000000.0,
            symbol = "₹"
        )
        CalculatorInput(
            label = "Interest Rate (p.a)",
            value = interestRate,
            onValueChange = { interestRate = it },
            range = 1.0..15.0,
            symbol = "%"
        )
        Text(
            text = "Fixed 5-year lock-in tenure",
            style = MaterialTheme.typography.labelMedium,
            color = tokens.inkSecondary
        )
    }
}
