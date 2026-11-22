package com.hsb.calchub.ui.screens.spend_vs_invest

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hsb.calchub.domain.logic.CalculatorLogic
import com.hsb.calchub.ui.components.CalculatorInput
import com.hsb.calchub.ui.components.CalculatorScrollScaffold
import com.hsb.calchub.ui.components.FinanceTermTooltip
import com.hsb.calchub.ui.components.InflationAdjustmentControl
import com.hsb.calchub.ui.components.InvestedReturnsDonutCard
import com.hsb.calchub.ui.components.NeonCard
import com.hsb.calchub.ui.components.ResultRow
import com.hsb.calchub.ui.components.StarterPreset
import com.hsb.calchub.ui.components.StarterPresetChips
import com.hsb.calchub.ui.components.formatCompactINR
import com.hsb.calchub.ui.components.rememberInrFormat
import com.hsb.calchub.ui.theme.LocalCalcHubTokens
import java.util.Locale
import kotlin.math.roundToInt

/**
 * Screen quantifying the compounded SIP opportunity cost of recurring monthly discretionary spending.
 */
@Composable
fun SpendVsInvestCalculatorScreen(onBackClick: () -> Unit) {
    var monthlySpend by rememberSaveable { mutableDoubleStateOf(3_000.0) }
    var expectedReturnPct by rememberSaveable { mutableDoubleStateOf(12.0) }
    var years by rememberSaveable { mutableDoubleStateOf(15.0) }
    var adjustForInflation by rememberSaveable { mutableStateOf(false) }
    var inflationPct by rememberSaveable { mutableDoubleStateOf(6.0) }

    val result = remember(monthlySpend, expectedReturnPct, years, adjustForInflation, inflationPct) {
        CalculatorLogic.calculateSpendVsInvest(
            monthlySpend = monthlySpend,
            expectedReturnPct = expectedReturnPct,
            years = years,
            inflationPct = if (adjustForInflation) inflationPct else 0.0
        )
    }

    val currencyFormat = rememberInrFormat()

    val shareSummary = remember(monthlySpend, expectedReturnPct, years, adjustForInflation, result) {
        buildString {
            append("Spend vs. Invest — CalcHub\n")
            append("Monthly Spend: ${currencyFormat.format(monthlySpend)} · ${years.roundToInt()} yrs @ ${String.format(Locale.US, "%.1f", expectedReturnPct)}%\n")
            append("Future Value If Invested: ${formatCompactINR(result.futureValueIfInvested, currencyFormat)}\n")
            append("Out-of-Pocket Spend: ${formatCompactINR(result.totalSpent, currencyFormat)}\n")
            append("Compounded Gains Missed: ${formatCompactINR(result.opportunityCostGain, currencyFormat)}")
            if (adjustForInflation) {
                append("\nToday's Purchasing Power: ${formatCompactINR(result.inflationAdjustedValue, currencyFormat)}")
            }
        }
    }

    CalculatorScrollScaffold(
        title = "Spend vs. Invest",
        onBackClick = onBackClick,
        calculatorId = "spend_vs_invest",
        shareSummary = shareSummary
    ) {
        val tokens = LocalCalcHubTokens.current
        NeonCard(modifier = Modifier.fillMaxWidth(), useGradientBorder = true) {
            ResultRow("If Invested (Future Value)", formatCompactINR(result.futureValueIfInvested, currencyFormat), isTotal = true)
            HorizontalDivider(color = tokens.cardBorder, modifier = Modifier.padding(vertical = 6.dp))
            ResultRow("Total Cash Spent", formatCompactINR(result.totalSpent, currencyFormat))
            ResultRow("Missed Compounded Gains", formatCompactINR(result.opportunityCostGain, currencyFormat))
            if (adjustForInflation) {
                ResultRow("Today's Value (Inflation-Adj.)", formatCompactINR(result.inflationAdjustedValue, currencyFormat))
            }
        }

        StarterPresetChips(
            presets = listOf(
                StarterPreset("₹1K OTT/Apps") { monthlySpend = 1_000.0 },
                StarterPreset("₹3K Food/Coffee") { monthlySpend = 3_000.0 },
                StarterPreset("₹5K Weekend") { monthlySpend = 5_000.0 },
                StarterPreset("₹10K Upgrade") { monthlySpend = 10_000.0 }
            )
        )

        NeonCard {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                CalculatorInput("Monthly Recurring Spend", monthlySpend, { monthlySpend = it }, 500.0..100_000.0, "₹")
                CalculatorInput("Expected Return (p.a)", expectedReturnPct, { expectedReturnPct = it }, 1.0..25.0, "%")
                CalculatorInput("Horizon", years, { years = it }, 1.0..40.0, "Yr")
                HorizontalDivider(color = tokens.cardBorder)
                InflationAdjustmentControl(
                    enabled = adjustForInflation,
                    onEnabledChange = { adjustForInflation = it },
                    inflationRate = inflationPct,
                    onInflationRateChange = { inflationPct = it }
                )
                FinanceTermTooltip(
                    term = "Opportunity Cost",
                    explanation = "Every recurring spend has a hidden cost: the wealth it could have compounded into if invested instead."
                )
            }
        }

        InvestedReturnsDonutCard(
            invested = result.totalSpent,
            returns = result.opportunityCostGain,
            investedLabel = "Total Spend",
            returnsLabel = "Missed Gains",
            centerLabelText = "Cost Split"
        )
    }
}
