package com.hsb.calchub.ui.screens.freelance_tax

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.unit.dp
import com.hsb.calchub.domain.logic.CalculatorLogic
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
import java.util.Locale
import kotlin.math.roundToInt

/**
 * Screen for Section 44ADA freelance / side-hustle tax estimation and post-tax hourly rate.
 */
@Composable
fun FreelanceTaxCalculatorScreen(onBackClick: () -> Unit) {
    var annualGrossReceipts by rememberSaveable { mutableDoubleStateOf(1_800_000.0) }
    var billableHoursPerWeek by rememberSaveable { mutableDoubleStateOf(30.0) }
    var workingWeeksPerYear by rememberSaveable { mutableDoubleStateOf(48.0) }

    val result = remember(annualGrossReceipts, billableHoursPerWeek, workingWeeksPerYear) {
        CalculatorLogic.calculateFreelanceTax(
            annualGrossReceipts = annualGrossReceipts,
            billableHoursPerWeek = billableHoursPerWeek,
            workingWeeksPerYear = workingWeeksPerYear.roundToInt()
        )
    }

    val currencyFormat = rememberInrFormat()

    val shareSummary = remember(annualGrossReceipts, result) {
        buildString {
            append("Freelance 44ADA Tax — CalcHub\n")
            append("Gross Receipts: ${formatCompactINR(result.grossReceipts, currencyFormat)}\n")
            append("Monthly Take-Home: ${currencyFormat.format(result.monthlyTakeHome)}\n")
            append("Estimated FY26 Tax: ${currencyFormat.format(result.estimatedTax)} (${String.format(Locale.US, "%.1f", result.effectiveTaxRatePct)}%)\n")
            append("Post-Tax Hourly Rate: ${currencyFormat.format(result.postTaxHourlyRate)}/hr")
        }
    }

    CalculatorScrollScaffold(
        title = "Freelance Tax (44ADA)",
        onBackClick = onBackClick,
        calculatorId = "freelance_tax",
        shareSummary = shareSummary
    ) {
        val tokens = LocalCalcHubTokens.current
        NeonCard(modifier = Modifier.fillMaxWidth(), useGradientBorder = true) {
            ResultRow("Monthly Take-Home", currencyFormat.format(result.monthlyTakeHome), isTotal = true)
            HorizontalDivider(color = tokens.cardBorder, modifier = Modifier.padding(vertical = 6.dp))
            ResultRow("Taxable Profit (50% 44ADA)", formatCompactINR(result.presumptiveTaxableIncome, currencyFormat))
            ResultRow(
                label = "FY26 Tax (${String.format(Locale.US, "%.1f", result.effectiveTaxRatePct)}% eff.)",
                value = currencyFormat.format(result.estimatedTax)
            )
            ResultRow("Post-Tax Hourly Rate", "${currencyFormat.format(result.postTaxHourlyRate)}/hr")
            ResultRow("Tax Saved vs. No Deduction", formatCompactINR(result.taxSavedVsRegular, currencyFormat))
            if (result.exceeds44AdaLimit) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Exceeds ₹75L Sec 44ADA digital limit; regular audit/tax rules may apply.",
                    style = MaterialTheme.typography.labelSmall,
                    color = tokens.lossInk
                )
            }
        }

        StarterPresetChips(
            presets = listOf(
                StarterPreset("₹8L Side Hustle") {
                    annualGrossReceipts = 800_000.0
                    billableHoursPerWeek = 15.0
                },
                StarterPreset("₹18L Solo Dev") {
                    annualGrossReceipts = 1_800_000.0
                    billableHoursPerWeek = 30.0
                },
                StarterPreset("₹40L Consultant") {
                    annualGrossReceipts = 4_000_000.0
                    billableHoursPerWeek = 35.0
                }
            )
        )

        NeonCard {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                CalculatorInput("Annual Gross Receipts", annualGrossReceipts, { annualGrossReceipts = it }, 100_000.0..10_000_000.0, "₹")
                CalculatorInput("Billable Hours / Week", billableHoursPerWeek, { billableHoursPerWeek = it }, 5.0..70.0, "Hr")
                CalculatorInput("Working Weeks / Year", workingWeeksPerYear, { workingWeeksPerYear = it }, 12.0..52.0, "Wk")
                FinanceTermTooltip(
                    term = "Section 44ADA",
                    explanation = "Eligible Indian freelancers & professionals with receipts up to ₹75L can declare 50% of gross receipts as taxable profit without maintaining detailed books."
                )
            }
        }

        InvestedReturnsDonutCard(
            invested = result.annualTakeHome,
            returns = result.estimatedTax,
            investedLabel = "Take-Home",
            returnsLabel = "Tax",
            centerLabelText = "Annual Split",
            useGainLossColors = true
        )
    }
}
