package com.hsb.calchub.ui.screens.fire

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
import com.hsb.calchub.domain.logic.retirement.FireCalculator
import com.hsb.calchub.ui.components.CalculatorInput
import com.hsb.calchub.ui.components.CalculatorScrollScaffold
import com.hsb.calchub.ui.components.FinanceTermTooltip
import com.hsb.calchub.ui.components.InvestedReturnsDonutCard
import com.hsb.calchub.ui.components.NeonCard
import com.hsb.calchub.ui.components.ResultRow
import com.hsb.calchub.ui.components.StarterPreset
import com.hsb.calchub.ui.components.StarterPresetChips
import com.hsb.calchub.ui.components.formatCompactINR
import com.hsb.calchub.ui.components.rememberInrFormat
import com.hsb.calchub.ui.theme.LocalCalcHubTokens
import kotlin.math.roundToInt

/**
 * Screen for FIRE & Coast-FIRE planning in inflation-adjusted real terms.
 */
@Composable
fun FireCalculatorScreen(onBackClick: () -> Unit) {
    var currentAge by rememberSaveable { mutableDoubleStateOf(26.0) }
    var monthlyExpenses by rememberSaveable { mutableDoubleStateOf(50_000.0) }
    var currentCorpus by rememberSaveable { mutableDoubleStateOf(500_000.0) }
    var monthlySip by rememberSaveable { mutableDoubleStateOf(25_000.0) }
    var expectedReturnPct by rememberSaveable { mutableDoubleStateOf(12.0) }
    var inflationPct by rememberSaveable { mutableDoubleStateOf(6.0) }
    var safeWithdrawalRatePct by rememberSaveable { mutableDoubleStateOf(4.0) }

    val result = remember(
        currentAge,
        monthlyExpenses,
        currentCorpus,
        monthlySip,
        expectedReturnPct,
        inflationPct,
        safeWithdrawalRatePct
    ) {
        CalculatorLogic.calculateFire(
            FireCalculator.FireInput(
                currentAge = currentAge.roundToInt(),
                monthlyExpenses = monthlyExpenses,
                currentCorpus = currentCorpus,
                monthlySip = monthlySip,
                expectedReturnPct = expectedReturnPct,
                inflationPct = inflationPct,
                safeWithdrawalRatePct = safeWithdrawalRatePct
            )
        )
    }

    val currencyFormat = rememberInrFormat()

    val fireTimelineLabel = if (result.yearsToFire == 0) {
        "Achieved now!"
    } else {
        "${result.yearsToFire} yrs (Age ${result.fireAge})"
    }

    val coastStatusLabel = if (result.isCoastFireReached) {
        "Reached!"
    } else {
        "Gap: ${formatCompactINR(result.coastFireGap, currencyFormat)}"
    }

    val shareSummary = remember(monthlyExpenses, currentCorpus, monthlySip, result, fireTimelineLabel, coastStatusLabel) {
        buildString {
            append("FIRE & Coast-FIRE Plan — CalcHub\n")
            append("Monthly Expenses (today): ${currencyFormat.format(monthlyExpenses)}\n")
            append("Target FIRE Corpus: ${formatCompactINR(result.fireNumber, currencyFormat)}\n")
            append("Estimated Timeline: $fireTimelineLabel\n")
            append("Coast-FIRE Target (Age 60): ${formatCompactINR(result.coastFireNumber, currencyFormat)} ($coastStatusLabel)")
        }
    }

    CalculatorScrollScaffold(
        title = "FIRE & Coast-FIRE",
        onBackClick = onBackClick,
        calculatorId = "fire",
        shareSummary = shareSummary
    ) {
        val tokens = LocalCalcHubTokens.current
        NeonCard(modifier = Modifier.fillMaxWidth(), useGradientBorder = true) {
            ResultRow("Target FIRE Corpus", formatCompactINR(result.fireNumber, currencyFormat), isTotal = true)
            HorizontalDivider(color = tokens.cardBorder, modifier = Modifier.padding(vertical = 6.dp))
            ResultRow("Time to FIRE", fireTimelineLabel)
            ResultRow("Coast-FIRE Target (Age 60)", formatCompactINR(result.coastFireNumber, currencyFormat))
            ResultRow("Coast-FIRE Status", coastStatusLabel)
        }

        StarterPresetChips(
            presets = listOf(
                StarterPreset("Starter ₹40K/mo") {
                    currentAge = 24.0
                    monthlyExpenses = 40_000.0
                    currentCorpus = 200_000.0
                    monthlySip = 15_000.0
                },
                StarterPreset("Metro ₹80K/mo") {
                    currentAge = 27.0
                    monthlyExpenses = 80_000.0
                    currentCorpus = 1_000_000.0
                    monthlySip = 35_000.0
                },
                StarterPreset("Fat FIRE ₹1.5L/mo") {
                    currentAge = 30.0
                    monthlyExpenses = 150_000.0
                    currentCorpus = 3_000_000.0
                    monthlySip = 75_000.0
                }
            )
        )

        NeonCard {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                CalculatorInput("Current Age", currentAge, { currentAge = it }, 18.0..55.0, "Yr")
                CalculatorInput("Monthly Expenses (Today)", monthlyExpenses, { monthlyExpenses = it }, 10_000.0..500_000.0, "₹")
                CalculatorInput("Existing Portfolio", currentCorpus, { currentCorpus = it }, 0.0..50_000_000.0, "₹")
                CalculatorInput("Monthly Investment (SIP)", monthlySip, { monthlySip = it }, 0.0..500_000.0, "₹")
                CalculatorInput("Expected Return (p.a)", expectedReturnPct, { expectedReturnPct = it }, 4.0..24.0, "%")
                CalculatorInput("Inflation Rate", inflationPct, { inflationPct = it }, 2.0..12.0, "%")
                CalculatorInput("Safe Withdrawal Rate", safeWithdrawalRatePct, { safeWithdrawalRatePct = it }, 2.5..6.0, "%")
                FinanceTermTooltip(
                    term = "What is Coast-FIRE?",
                    explanation = "The amount you need invested today so compounding alone reaches your FIRE goal by age 60 without adding another rupee."
                )
            }
        }

        InvestedReturnsDonutCard(
            invested = currentCorpus.coerceAtLeast(0.0),
            returns = (result.fireNumber - currentCorpus).coerceAtLeast(0.0),
            investedLabel = "Current",
            returnsLabel = "To Grow",
            centerLabelText = "FIRE Progress"
        )
    }
}
