package com.hsb.calchub.ui.screens.goal_sip

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
 * Screen for Goal-Based (Reverse) SIP planning: computes monthly SIP needed to reach a target corpus.
 */
@Composable
fun GoalSipCalculatorScreen(onBackClick: () -> Unit) {
    var targetCorpus by rememberSaveable { mutableDoubleStateOf(10_000_000.0) }
    var expectedReturnRate by rememberSaveable { mutableDoubleStateOf(12.0) }
    var timePeriodYears by rememberSaveable { mutableDoubleStateOf(15.0) }
    var adjustForInflation by rememberSaveable { mutableStateOf(false) }
    var inflationRate by rememberSaveable { mutableDoubleStateOf(6.0) }

    val result = remember(targetCorpus, expectedReturnRate, timePeriodYears, adjustForInflation, inflationRate) {
        CalculatorLogic.calculateGoalSIP(
            targetCorpus = targetCorpus,
            expectedReturnRate = expectedReturnRate,
            timePeriodYears = timePeriodYears,
            inflationRate = if (adjustForInflation) inflationRate else 0.0
        )
    }
    val currencyFormat = rememberInrFormat()

    val shareSummary = remember(targetCorpus, expectedReturnRate, timePeriodYears, adjustForInflation, inflationRate, result) {
        buildString {
            append("Goal SIP Plan — CalcHub\n")
            append("Monthly SIP Needed: ${currencyFormat.format(result.requiredMonthlySip)}/mo\n")
            append("Target: ${formatCompactINR(result.inflationAdjustedTarget, currencyFormat)} in ${timePeriodYears.roundToInt()} yrs @ ${String.format(Locale.US, "%.1f", expectedReturnRate)}%\n")
            append("Total Invested: ${formatCompactINR(result.totalInvested, currencyFormat)}\n")
            append("Est. Wealth Gain: ${formatCompactINR(result.wealthGain, currencyFormat)}")
        }
    }

    CalculatorScrollScaffold(
        title = "Goal SIP Planner",
        onBackClick = onBackClick,
        calculatorId = "goal_sip",
        shareSummary = shareSummary
    ) {
        val tokens = LocalCalcHubTokens.current
        NeonCard(modifier = Modifier.fillMaxWidth(), useGradientBorder = true) {
            ResultRow("Monthly SIP Required", "${currencyFormat.format(result.requiredMonthlySip)}/mo", isTotal = true)
            HorizontalDivider(color = tokens.cardBorder, modifier = Modifier.padding(vertical = 6.dp))
            ResultRow(
                label = if (adjustForInflation) "Inflation-Adjusted Goal" else "Target Corpus",
                value = formatCompactINR(result.inflationAdjustedTarget, currencyFormat)
            )
            ResultRow("Total SIP Invested", formatCompactINR(result.totalInvested, currencyFormat))
            ResultRow("Est. Wealth Gain", formatCompactINR(result.wealthGain, currencyFormat))
        }

        StarterPresetChips(
            presets = listOf(
                StarterPreset("₹25L Car/Home") {
                    targetCorpus = 2_500_000.0
                    expectedReturnRate = 12.0
                    timePeriodYears = 7.0
                },
                StarterPreset("₹1 Cr Goal") {
                    targetCorpus = 10_000_000.0
                    expectedReturnRate = 12.0
                    timePeriodYears = 15.0
                },
                StarterPreset("₹5 Cr Freedom") {
                    targetCorpus = 50_000_000.0
                    expectedReturnRate = 12.0
                    timePeriodYears = 25.0
                }
            )
        )

        NeonCard {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                CalculatorInput(
                    label = "Target Corpus (Today's Value)",
                    value = targetCorpus,
                    onValueChange = { targetCorpus = it },
                    range = 100_000.0..100_000_000.0,
                    symbol = "₹"
                )
                CalculatorInput(
                    label = "Expected Return (p.a)",
                    value = expectedReturnRate,
                    onValueChange = { expectedReturnRate = it },
                    range = 1.0..30.0,
                    symbol = "%"
                )
                CalculatorInput(
                    label = "Time Horizon",
                    value = timePeriodYears,
                    onValueChange = { timePeriodYears = it },
                    range = 1.0..40.0,
                    symbol = "Yr"
                )
                HorizontalDivider(color = tokens.cardBorder)
                InflationAdjustmentControl(
                    enabled = adjustForInflation,
                    onEnabledChange = { adjustForInflation = it },
                    inflationRate = inflationRate,
                    onInflationRateChange = { inflationRate = it }
                )
            }
        }

        InvestedReturnsDonutCard(
            invested = result.totalInvested,
            returns = result.wealthGain,
            centerLabelText = "Goal Funding"
        )
    }
}
