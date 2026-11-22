package com.hsb.calchub.ui.screens.credit_card_emi

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hsb.calchub.domain.logic.CalculatorLogic
import com.hsb.calchub.ui.components.CalculatorInput
import com.hsb.calchub.ui.components.CalculatorScrollScaffold
import com.hsb.calchub.ui.components.DonutChartData
import com.hsb.calchub.ui.components.NeonCard
import com.hsb.calchub.ui.components.ResultRow
import com.hsb.calchub.ui.components.StandardDonutCard
import com.hsb.calchub.ui.components.rememberInrFormat
import com.hsb.calchub.ui.theme.LocalCalcHubTokens

/**
 * Screen comparing fixed monthly Credit Card payoff vs. paying only minimum due (5%).
 */
@Composable
fun CreditCardEmiCalculatorScreen(onBackClick: () -> Unit) {
    var balance by rememberSaveable { mutableDoubleStateOf(100_000.0) }
    var annualRate by rememberSaveable { mutableDoubleStateOf(36.0) }
    var monthlyPayment by rememberSaveable { mutableDoubleStateOf(10_000.0) }

    val result = remember(balance, annualRate, monthlyPayment) {
        CalculatorLogic.calculateCreditCardPayoff(balance, annualRate, monthlyPayment)
    }
    val currencyFormat = rememberInrFormat()

    CalculatorScrollScaffold(
        title = "Credit Card Payoff",
        onBackClick = onBackClick,
        calculatorId = "credit_card_emi"
    ) {
        val tokens = LocalCalcHubTokens.current
        NeonCard(modifier = Modifier.fillMaxWidth(), useGradientBorder = true) {
            if (result.willPayOffFixed) {
                ResultRow("Payoff Time (Fixed)", "${result.fixedMonths} months", isTotal = true)
                ResultRow("Fixed Payment Interest", currencyFormat.format(result.fixedTotalInterest))
                HorizontalDivider(color = tokens.cardBorder, modifier = Modifier.padding(vertical = 6.dp))
                ResultRow("5% Min-Due Time", "${result.minDueMonths} months")
                ResultRow("5% Min-Due Interest", currencyFormat.format(result.minDueTotalInterest))
                ResultRow("Interest Saved vs Min Due", currencyFormat.format(result.interestSaved))
            } else {
                Text(
                    text = "Monthly payment does not cover monthly interest (${currencyFormat.format(balance * annualRate / 1200.0)}/mo). Increase payment to clear balance.",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = tokens.lossInk
                )
            }
        }

        NeonCard {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                CalculatorInput("Card Balance", balance, { balance = it }, 5_000.0..2_000_000.0, "₹")
                CalculatorInput("Annual Interest (APR)", annualRate, { annualRate = it }, 12.0..48.0, "%")
                CalculatorInput("Monthly Payment", monthlyPayment, { monthlyPayment = it }, 1_000.0..200_000.0, "₹")
            }
        }

        if (result.willPayOffFixed) {
            StandardDonutCard(
                data = listOf(
                    DonutChartData(balance, tokens.chartStart, "Principal"),
                    DonutChartData(result.fixedTotalInterest, tokens.lossInk, "Interest")
                ),
                centerLabelText = "Payoff Cost"
            )
        }
    }
}
