package com.hsb.calchub.ui.screens.sip

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hsb.calchub.data.repository.RecentCalculation
import com.hsb.calchub.data.repository.SavedScenario
import com.hsb.calchub.di.LocalAppContainer
import com.hsb.calchub.domain.logic.CalculatorLogic
import com.hsb.calchub.ui.components.CalculatorInput
import com.hsb.calchub.ui.components.CalculatorScrollScaffold
import com.hsb.calchub.ui.components.InflationAdjustedSummaryCard
import com.hsb.calchub.ui.components.InflationAdjustmentControl
import com.hsb.calchub.ui.components.InvestedReturnsDonutCard
import com.hsb.calchub.ui.components.NeonCard
import com.hsb.calchub.ui.components.ResultCard
import com.hsb.calchub.ui.components.StarterPreset
import com.hsb.calchub.ui.components.StarterPresetChips
import com.hsb.calchub.ui.components.WealthGrowthSplineChart
import com.hsb.calchub.ui.components.formatCompactINR
import com.hsb.calchub.ui.components.rememberInrFormat
import com.hsb.calchub.ui.theme.LocalCalcHubTokens
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.roundToInt

/**
 * Interactive SIP Wealth Builder featuring above-the-fold verdict card, optional inflation adjustment,
 * scrubbable Canvas + PathMeasure spline growth chart, donut breakdown, and Vault saving.
 */
@Composable
fun SipCalculatorScreen(onBackClick: () -> Unit) {
    val userDataRepository = LocalAppContainer.current.userDataRepository

    var monthlyInvestment by rememberSaveable { mutableDoubleStateOf(10_000.0) }
    var expectedReturnRate by rememberSaveable { mutableDoubleStateOf(12.0) }
    var timePeriodYears by rememberSaveable { mutableDoubleStateOf(15.0) }
    var adjustForInflation by rememberSaveable { mutableStateOf(false) }
    var inflationRate by rememberSaveable { mutableDoubleStateOf(6.0) }

    val (investedAmount, estimatedReturns, totalValue) = remember(
        monthlyInvestment,
        expectedReturnRate,
        timePeriodYears
    ) {
        CalculatorLogic.calculateSIP(monthlyInvestment, expectedReturnRate, timePeriodYears)
    }

    val realTotalValue = remember(totalValue, inflationRate, timePeriodYears, adjustForInflation) {
        if (adjustForInflation) {
            CalculatorLogic.adjustForInflation(totalValue, inflationRate, timePeriodYears)
        } else {
            totalValue
        }
    }

    val yearlyPoints = remember(monthlyInvestment, expectedReturnRate, timePeriodYears) {
        val totalYrs = timePeriodYears.roundToInt().coerceAtLeast(1)
        buildList {
            add(0.0 to 0.0)
            for (y in 1..totalYrs) {
                val (invY, _, valY) = CalculatorLogic.calculateSIP(
                    monthlyInvestment,
                    expectedReturnRate,
                    y.toDouble()
                )
                add(invY to valY)
            }
        }
    }

    val inrFormat = rememberInrFormat()
    val compactTotal = remember(totalValue) { formatCompactINR(totalValue, inrFormat) }
    val compactGain = remember(estimatedReturns) { formatCompactINR(estimatedReturns, inrFormat) }

    LaunchedEffect(monthlyInvestment, expectedReturnRate, timePeriodYears, compactTotal) {
        userDataRepository.updateLastUsed(
            RecentCalculation(
                route = "sip",
                calculatorName = "SIP",
                paramsSummary = "${inrFormat.format(monthlyInvestment)}/mo · ${timePeriodYears.roundToInt()} yrs · ${String.format(Locale.US, "%.1f", expectedReturnRate)}%",
                formattedValue = compactTotal,
                secondarySummary = "+$compactGain est. wealth gain"
            )
        )
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val saveToVault: () -> Unit = {
        userDataRepository.saveScenario(
            SavedScenario(
                id = "sip_${System.currentTimeMillis()}",
                route = "sip",
                title = "SIP ${inrFormat.format(monthlyInvestment)}/mo",
                summary = "${timePeriodYears.roundToInt()} yrs @ ${String.format(Locale.US, "%.1f", expectedReturnRate)}%",
                resultFormatted = compactTotal
            )
        )
        scope.launch {
            snackbarHostState.showSnackbar("Saved SIP scenario ($compactTotal) to Vault")
        }
    }

    val shareSummary = remember(monthlyInvestment, expectedReturnRate, timePeriodYears, totalValue, investedAmount, estimatedReturns, adjustForInflation, inflationRate, realTotalValue) {
        buildString {
            append("SIP Plan — CalcHub\n")
            append("Monthly SIP: ${inrFormat.format(monthlyInvestment)} · ${timePeriodYears.roundToInt()} yrs @ ${String.format(Locale.US, "%.1f", expectedReturnRate)}%\n")
            append("Total Invested: ${inrFormat.format(investedAmount)}\n")
            append("Est. Returns: ${inrFormat.format(estimatedReturns)}\n")
            append("Expected Maturity: ${inrFormat.format(totalValue)} ($compactTotal)")
            if (adjustForInflation) {
                append("\nToday's Value (${String.format(Locale.US, "%.1f", inflationRate)}% inf.): ${inrFormat.format(realTotalValue)}")
            }
        }
    }

    CalculatorScrollScaffold(
        title = "SIP Calculator",
        onBackClick = onBackClick,
        calculatorId = "sip",
        onSaveScenario = saveToVault,
        shareSummary = shareSummary,
        verticalSpacing = 14.dp,
        bottomSpacerHeight = 24.dp
    ) {
        ResultCard(
            investedAmount = investedAmount,
            estimatedReturns = estimatedReturns,
            totalValue = totalValue,
            primaryLabel = "Expected Maturity",
            investedLabel = "Total Invested",
            returnsLabel = "Est. Returns",
            onSaveScenario = saveToVault
        )

        StarterPresetChips(
            presets = listOf(
                StarterPreset("₹5K Starter") {
                    monthlyInvestment = 5_000.0
                    expectedReturnRate = 12.0
                    timePeriodYears = 15.0
                },
                StarterPreset("₹15K Growth") {
                    monthlyInvestment = 15_000.0
                    expectedReturnRate = 12.0
                    timePeriodYears = 15.0
                },
                StarterPreset("₹25K Aggressive") {
                    monthlyInvestment = 25_000.0
                    expectedReturnRate = 14.0
                    timePeriodYears = 20.0
                }
            )
        )

        if (adjustForInflation) {
            InflationAdjustedSummaryCard(
                nominalValue = totalValue,
                realValue = realTotalValue,
                investedAmount = investedAmount,
                inflationRate = inflationRate,
                years = timePeriodYears
            )
        }

        SipCoreInputsSection(
            monthlyInvestment = monthlyInvestment,
            onMonthlyInvestmentChange = { monthlyInvestment = it },
            expectedReturnRate = expectedReturnRate,
            onExpectedReturnRateChange = { expectedReturnRate = it },
            timePeriodYears = timePeriodYears,
            onTimePeriodYearsChange = { timePeriodYears = it },
            footerContent = {
                InflationAdjustmentControl(
                    enabled = adjustForInflation,
                    onEnabledChange = { adjustForInflation = it },
                    inflationRate = inflationRate,
                    onInflationRateChange = { inflationRate = it }
                )
            }
        )

        WealthGrowthSplineChart(
            title = "Wealth Projection (Scrub to Inspect)",
            points = yearlyPoints
        )

        SipGainsDonut(
            invested = investedAmount,
            returns = estimatedReturns
        )

        SnackbarHost(hostState = snackbarHostState)
    }
}

@Composable
internal fun SipGainsDonut(invested: Double, returns: Double) {
    InvestedReturnsDonutCard(
        invested = invested,
        returns = returns,
        centerLabelText = "Gains Share"
    )
}

@Composable
internal fun SipCoreInputsSection(
    monthlyInvestment: Double,
    onMonthlyInvestmentChange: (Double) -> Unit,
    expectedReturnRate: Double,
    onExpectedReturnRateChange: (Double) -> Unit,
    timePeriodYears: Double,
    onTimePeriodYearsChange: (Double) -> Unit,
    monthlyLabel: String = "Monthly Investment",
    returnLabel: String = "Expected Return Rate (p.a)",
    afterMonthlyContent: (@Composable () -> Unit)? = null,
    footerContent: (@Composable () -> Unit)? = null
) {
    val tokens = LocalCalcHubTokens.current
    NeonCard {
        CalculatorInput(
            label = monthlyLabel,
            value = monthlyInvestment,
            onValueChange = onMonthlyInvestmentChange,
            range = 500.0..100_000.0,
            symbol = "₹"
        )
        if (afterMonthlyContent != null) {
            Spacer(modifier = Modifier.height(10.dp))
            afterMonthlyContent()
        }
        Spacer(modifier = Modifier.height(10.dp))
        CalculatorInput(
            label = returnLabel,
            value = expectedReturnRate,
            onValueChange = onExpectedReturnRateChange,
            range = 1.0..30.0,
            symbol = "%"
        )
        Spacer(modifier = Modifier.height(10.dp))
        CalculatorInput(
            label = "Time Period",
            value = timePeriodYears,
            onValueChange = onTimePeriodYearsChange,
            range = 1.0..40.0,
            symbol = "Yr"
        )
        if (footerContent != null) {
            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = tokens.cardBorder)
            Spacer(modifier = Modifier.height(10.dp))
            footerContent()
        }
    }
}
