package com.hsb.calchub.ui.screens.salary

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
import com.hsb.calchub.ui.components.DonutChartData
import com.hsb.calchub.ui.components.FinanceTermTooltip
import com.hsb.calchub.ui.components.InvestedReturnsDonutCard
import com.hsb.calchub.ui.components.NeonCard
import com.hsb.calchub.ui.components.ResultRow
import com.hsb.calchub.ui.components.SegmentedPillGroup
import com.hsb.calchub.ui.components.StandardDonutCard
import com.hsb.calchub.ui.components.StarterPreset
import com.hsb.calchub.ui.components.StarterPresetChips
import com.hsb.calchub.ui.components.formatCompactINR
import com.hsb.calchub.ui.components.rememberInrFormat
import com.hsb.calchub.ui.theme.LocalCalcHubTokens

private enum class SalaryModeOption(val label: String) {
    CTC("CTC Mode"),
    CUSTOM("Custom")
}

/**
 * Screen for Salary calculation supporting instant CTC-to-in-hand estimation (FY26 New Regime)
 * as well as custom component breakdown.
 */
@Composable
fun SalaryCalculatorScreen(onBackClick: () -> Unit) {
    var mode by rememberSaveable { mutableStateOf(SalaryModeOption.CTC) }

    var annualCtc by rememberSaveable { mutableDoubleStateOf(1_200_000.0) }
    var variableBonusPct by rememberSaveable { mutableDoubleStateOf(10.0) }
    var basicPctOfFixed by rememberSaveable { mutableDoubleStateOf(40.0) }
    var pfMonthlyCap by rememberSaveable { mutableDoubleStateOf(1800.0) }

    var basicSalary by rememberSaveable { mutableDoubleStateOf(40_000.0) }
    var hra by rememberSaveable { mutableDoubleStateOf(16_000.0) }
    var otherAllowances by rememberSaveable { mutableDoubleStateOf(5_000.0) }
    var pf by rememberSaveable { mutableDoubleStateOf(1800.0) }

    val ctcResult = remember(annualCtc, variableBonusPct, basicPctOfFixed, pfMonthlyCap) {
        CalculatorLogic.calculateSalaryFromCtc(
            annualCtc = annualCtc,
            variableBonusPct = variableBonusPct,
            basicPctOfFixed = basicPctOfFixed,
            pfMonthlyCap = pfMonthlyCap
        )
    }
    val customResults = remember(basicSalary, hra, otherAllowances, pf) {
        CalculatorLogic.calculateSalary(basicSalary, hra, otherAllowances, pf)
    }

    val currencyFormat = rememberInrFormat()

    val shareSummary = remember(mode, ctcResult, customResults) {
        if (mode == SalaryModeOption.CTC) {
            buildString {
                append("Salary CTC Breakdown — CalcHub\n")
                append("Annual CTC: ${formatCompactINR(ctcResult.annualCtc, currencyFormat)}\n")
                append("Monthly In-Hand: ${currencyFormat.format(ctcResult.monthlyInHand)}\n")
                append("Monthly Gross: ${currencyFormat.format(ctcResult.monthlyGross)}\n")
                append("PF / PT / Income Tax (mo): ${currencyFormat.format(ctcResult.monthlyPf)} / ${currencyFormat.format(ctcResult.monthlyProfTax)} / ${currencyFormat.format(ctcResult.monthlyIncomeTax)}\n")
                append("Annual Take-Home: ${formatCompactINR(ctcResult.annualInHand, currencyFormat)}")
            }
        } else {
            buildString {
                append("Salary Net Pay — CalcHub\n")
                append("Gross Salary: ${currencyFormat.format(customResults.first)}\n")
                append("Total Deductions: ${currencyFormat.format(customResults.second)}\n")
                append("Net Monthly Salary: ${currencyFormat.format(customResults.third)}")
            }
        }
    }

    CalculatorScrollScaffold(
        title = "Salary Calculator",
        onBackClick = onBackClick,
        calculatorId = "salary",
        shareSummary = shareSummary
    ) {
        val tokens = LocalCalcHubTokens.current
        SegmentedPillGroup(
            options = SalaryModeOption.entries,
            selectedOption = mode,
            onOptionSelected = { mode = it },
            labelProvider = { it.label },
            modifier = Modifier.fillMaxWidth()
        )

        if (mode == SalaryModeOption.CTC) {
            NeonCard(modifier = Modifier.fillMaxWidth(), useGradientBorder = true) {
                ResultRow("Monthly In-Hand", currencyFormat.format(ctcResult.monthlyInHand), isTotal = true)
                HorizontalDivider(color = tokens.cardBorder, modifier = Modifier.padding(vertical = 6.dp))
                ResultRow("Monthly Gross (Fixed)", currencyFormat.format(ctcResult.monthlyGross))
                ResultRow("Employee PF / mo", currencyFormat.format(ctcResult.monthlyPf))
                ResultRow("Prof. Tax / mo", currencyFormat.format(ctcResult.monthlyProfTax))
                ResultRow("Income Tax / mo (FY26 New)", currencyFormat.format(ctcResult.monthlyIncomeTax))
                ResultRow("Annual Take-Home", formatCompactINR(ctcResult.annualInHand, currencyFormat))
            }

            StarterPresetChips(
                presets = listOf(
                    StarterPreset("First Job ₹6L") {
                        annualCtc = 600_000.0
                        variableBonusPct = 0.0
                    },
                    StarterPreset("₹10L CTC") {
                        annualCtc = 1_000_000.0
                        variableBonusPct = 10.0
                    },
                    StarterPreset("₹15L CTC") {
                        annualCtc = 1_500_000.0
                        variableBonusPct = 10.0
                    },
                    StarterPreset("₹24L CTC") {
                        annualCtc = 2_400_000.0
                        variableBonusPct = 15.0
                    }
                )
            )

            NeonCard {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    CalculatorInput("Annual CTC", annualCtc, { annualCtc = it }, 300_000.0..10_000_000.0, "₹")
                    CalculatorInput("Variable Bonus %", variableBonusPct, { variableBonusPct = it }, 0.0..30.0, "%")
                    CalculatorInput("Basic % of Fixed", basicPctOfFixed, { basicPctOfFixed = it }, 30.0..60.0, "%")
                    CalculatorInput("Monthly PF Cap", pfMonthlyCap, { pfMonthlyCap = it }, 0.0..15_000.0, "₹")
                    FinanceTermTooltip(
                        term = "CTC vs. In-Hand",
                        explanation = "CTC includes employer PF and variable bonus; your monthly in-hand deducts employee PF, professional tax, and FY26 New Regime income tax."
                    )
                }
            }

            StandardDonutCard(
                data = listOf(
                    DonutChartData(ctcResult.monthlyInHand, tokens.gainInk, "In-Hand"),
                    DonutChartData(ctcResult.monthlyPf + ctcResult.monthlyProfTax, tokens.chartStart, "PF & PT"),
                    DonutChartData(ctcResult.monthlyIncomeTax, tokens.lossInk, "Tax")
                ),
                centerLabelText = "Monthly Split"
            )
        } else {
            NeonCard(modifier = Modifier.fillMaxWidth(), useGradientBorder = true) {
                ResultRow("Gross Salary", currencyFormat.format(customResults.first))
                ResultRow("Total Deductions", currencyFormat.format(customResults.second))
                ResultRow("Net Salary", currencyFormat.format(customResults.third), isTotal = true)
            }

            NeonCard {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    CalculatorInput("Basic Salary", basicSalary, { basicSalary = it }, 10_000.0..500_000.0, "₹")
                    CalculatorInput("HRA", hra, { hra = it }, 0.0..200_000.0, "₹")
                    CalculatorInput("Other Allowances", otherAllowances, { otherAllowances = it }, 0.0..100_000.0, "₹")
                    CalculatorInput("PF Deduction", pf, { pf = it }, 0.0..50_000.0, "₹")
                }
            }

            InvestedReturnsDonutCard(
                invested = customResults.third,
                returns = customResults.second,
                investedLabel = "Take Home",
                returnsLabel = "Deductions",
                centerLabelText = "Pay Split",
                useGainLossColors = true
            )
        }
    }
}
