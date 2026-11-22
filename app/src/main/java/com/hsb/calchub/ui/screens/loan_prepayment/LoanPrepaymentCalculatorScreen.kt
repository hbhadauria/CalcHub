package com.hsb.calchub.ui.screens.loan_prepayment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hsb.calchub.domain.logic.CalculatorLogic
import com.hsb.calchub.ui.components.CalculatorInput
import com.hsb.calchub.ui.components.CalculatorScrollScaffold
import com.hsb.calchub.ui.components.InvestedReturnsDonutCard
import com.hsb.calchub.ui.components.NeonCard
import com.hsb.calchub.ui.components.ResultRow
import com.hsb.calchub.ui.components.formatCompactINR
import com.hsb.calchub.ui.components.rememberInrFormat
import com.hsb.calchub.ui.theme.LocalCalcHubTokens

/**
 * Screen for simulating Loan Prepayment savings from extra monthly EMIs and lump-sum prepayments.
 */
@Composable
fun LoanPrepaymentCalculatorScreen(onBackClick: () -> Unit) {
    var loanAmount by rememberSaveable { mutableDoubleStateOf(3_000_000.0) }
    var interestRate by rememberSaveable { mutableDoubleStateOf(8.5) }
    var tenureYears by rememberSaveable { mutableDoubleStateOf(20.0) }
    var extraMonthly by rememberSaveable { mutableDoubleStateOf(5_000.0) }
    var lumpSum by rememberSaveable { mutableDoubleStateOf(200_000.0) }

    val result = remember(loanAmount, interestRate, tenureYears, extraMonthly, lumpSum) {
        CalculatorLogic.calculateLoanPrepayment(
            loanAmount = loanAmount,
            interestRate = interestRate,
            tenureYears = tenureYears,
            extraMonthlyPayment = extraMonthly,
            lumpSumPrepayment = lumpSum
        )
    }
    val currencyFormat = rememberInrFormat()

    CalculatorScrollScaffold(
        title = "Loan Prepayment",
        onBackClick = onBackClick,
        calculatorId = "loan_prepayment"
    ) {
        val tokens = LocalCalcHubTokens.current
        NeonCard(modifier = Modifier.fillMaxWidth(), useGradientBorder = true) {
            ResultRow("Total Interest Saved", formatCompactINR(result.interestSaved, currencyFormat), isTotal = true)
            HorizontalDivider(color = tokens.cardBorder, modifier = Modifier.padding(vertical = 6.dp))
            val yrsSaved = result.monthsSaved / 12
            val mosSaved = result.monthsSaved % 12
            ResultRow("Tenure Reduced By", "${yrsSaved}y ${mosSaved}m (${result.monthsSaved} EMIs)")
            ResultRow("Monthly Base EMI", "${currencyFormat.format(result.baseEmi)}/mo")
            ResultRow("New Total Interest", formatCompactINR(result.newTotalInterest, currencyFormat))
        }

        NeonCard {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                CalculatorInput("Loan Amount", loanAmount, { loanAmount = it }, 100_000.0..50_000_000.0, "₹")
                CalculatorInput("Interest Rate (p.a)", interestRate, { interestRate = it }, 5.0..20.0, "%")
                CalculatorInput("Tenure", tenureYears, { tenureYears = it }, 1.0..30.0, "Yr")
                HorizontalDivider(color = tokens.cardBorder)
                CalculatorInput("Extra Monthly Payment", extraMonthly, { extraMonthly = it }, 0.0..200_000.0, "₹")
                CalculatorInput("One-Time Lump Sum (Yr 1)", lumpSum, { lumpSum = it }, 0.0..5_000_000.0, "₹")
            }
        }

        InvestedReturnsDonutCard(
            invested = result.interestSaved,
            returns = result.newTotalInterest,
            investedLabel = "Interest Saved",
            returnsLabel = "Interest Paid",
            centerLabelText = "Interest Impact",
            useGainLossColors = true
        )
    }
}
