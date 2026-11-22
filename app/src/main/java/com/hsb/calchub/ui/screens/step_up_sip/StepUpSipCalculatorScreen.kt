package com.hsb.calchub.ui.screens.step_up_sip

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hsb.calchub.data.repository.RecentCalculation
import com.hsb.calchub.data.repository.SavedScenario
import com.hsb.calchub.di.LocalAppContainer
import com.hsb.calchub.domain.logic.CalculatorLogic
import com.hsb.calchub.ui.components.CalculatorInput
import com.hsb.calchub.ui.components.CalculatorScrollScaffold
import com.hsb.calchub.ui.components.InflationAdjustedSummaryCard
import com.hsb.calchub.ui.components.InflationAdjustmentControl
import com.hsb.calchub.ui.components.NeonCard
import com.hsb.calchub.ui.components.ResultCard
import com.hsb.calchub.ui.components.ResultRow
import com.hsb.calchub.ui.components.SegmentedPillGroup
import com.hsb.calchub.ui.components.WealthGrowthSplineChart
import com.hsb.calchub.ui.components.formatCompactINR
import com.hsb.calchub.ui.components.rememberInrFormat
import com.hsb.calchub.ui.screens.sip.SipCoreInputsSection
import com.hsb.calchub.ui.screens.sip.SipGainsDonut
import com.hsb.calchub.ui.theme.LocalCalcHubTokens
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.roundToInt

private enum class StepUpSipMode(val label: String) {
    STEP_UP("Step-Up SIP"),
    REGULAR("Regular SIP")
}

/**
 * Interactive Step-Up SIP Calculator with annual top-up compounding, Regular SIP delta comparison,
 * optional inflation adjustment, and Canvas + PathMeasure spline visualization.
 */
@Composable
fun StepUpSipCalculatorScreen(onBackClick: () -> Unit) {
    val userDataRepository = LocalAppContainer.current.userDataRepository

    var mode by rememberSaveable { mutableStateOf(StepUpSipMode.STEP_UP) }
    var monthlyInvestment by rememberSaveable { mutableDoubleStateOf(10_000.0) }
    var annualStepUpPct by rememberSaveable { mutableDoubleStateOf(10.0) }
    var expectedReturnRate by rememberSaveable { mutableDoubleStateOf(12.0) }
    var timePeriodYears by rememberSaveable { mutableDoubleStateOf(15.0) }
    var adjustForInflation by rememberSaveable { mutableStateOf(false) }
    var inflationRate by rememberSaveable { mutableDoubleStateOf(6.0) }

    val yearsInt = timePeriodYears.roundToInt().coerceAtLeast(1)
    val stepUpProj = remember(monthlyInvestment, annualStepUpPct, expectedReturnRate, yearsInt) {
        CalculatorLogic.calculateStepUpSIP(monthlyInvestment, annualStepUpPct, expectedReturnRate, yearsInt)
    }
    val (regInvested, regReturns, regTotal) = remember(monthlyInvestment, expectedReturnRate, timePeriodYears) {
        CalculatorLogic.calculateSIP(monthlyInvestment, expectedReturnRate, timePeriodYears)
    }
    val regularPoints = remember(monthlyInvestment, expectedReturnRate, yearsInt) {
        buildList {
            add(0.0 to 0.0)
            for (y in 1..yearsInt) {
                val (invY, _, valY) = CalculatorLogic.calculateSIP(
                    monthlyInvestment,
                    expectedReturnRate,
                    y.toDouble()
                )
                add(invY to valY)
            }
        }
    }

    val shownInvested = if (mode == StepUpSipMode.STEP_UP) stepUpProj.investedAmount else regInvested
    val shownReturns = if (mode == StepUpSipMode.STEP_UP) stepUpProj.estimatedReturns else regReturns
    val shownTotal = if (mode == StepUpSipMode.STEP_UP) stepUpProj.totalValue else regTotal
    val shownPoints = if (mode == StepUpSipMode.STEP_UP) stepUpProj.yearlyPoints else regularPoints
    val stepUpAdvantage = (stepUpProj.totalValue - regTotal).coerceAtLeast(0.0)

    val shownRealTotal = remember(shownTotal, inflationRate, yearsInt, adjustForInflation) {
        if (adjustForInflation) {
            CalculatorLogic.adjustForInflation(shownTotal, inflationRate, yearsInt.toDouble())
        } else {
            shownTotal
        }
    }

    val inrFormat = rememberInrFormat()
    val compactTotal = remember(shownTotal) { formatCompactINR(shownTotal, inrFormat) }
    val compactAdvantage = remember(stepUpAdvantage) { formatCompactINR(stepUpAdvantage, inrFormat) }

    LaunchedEffect(monthlyInvestment, annualStepUpPct, expectedReturnRate, yearsInt, compactTotal) {
        userDataRepository.updateLastUsed(
            RecentCalculation(
                route = "step_up_sip",
                calculatorName = "Step-up SIP",
                paramsSummary = "${inrFormat.format(monthlyInvestment)}/mo · +${annualStepUpPct.roundToInt()}%/yr · $yearsInt yrs",
                formattedValue = compactTotal,
                secondarySummary = "+$compactAdvantage vs flat SIP"
            )
        )
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val saveToVault: () -> Unit = {
        userDataRepository.saveScenario(
            SavedScenario(
                id = "step_up_sip_${System.currentTimeMillis()}",
                route = "step_up_sip",
                title = "Step-up SIP +${annualStepUpPct.roundToInt()}%",
                summary = "${inrFormat.format(monthlyInvestment)}/mo · $yearsInt yrs @ ${String.format(Locale.US, "%.1f", expectedReturnRate)}%",
                resultFormatted = compactTotal
            )
        )
        scope.launch {
            snackbarHostState.showSnackbar("Saved Step-Up SIP ($compactTotal) to Vault")
        }
    }

    CalculatorScrollScaffold(
        title = "Step-up SIP Calculator",
        onBackClick = onBackClick,
        calculatorId = "step_up_sip",
        onSaveScenario = saveToVault,
        verticalSpacing = 14.dp,
        bottomSpacerHeight = 24.dp
    ) {
        val tokens = LocalCalcHubTokens.current
        SegmentedPillGroup(
            options = StepUpSipMode.entries,
            selectedOption = mode,
            onOptionSelected = { mode = it },
            labelProvider = { it.label },
            modifier = Modifier.fillMaxWidth()
        )

        ResultCard(
            investedAmount = shownInvested,
            estimatedReturns = shownReturns,
            totalValue = shownTotal,
            primaryLabel = if (mode == StepUpSipMode.STEP_UP) "Step-Up Maturity" else "Regular Maturity",
            investedLabel = "Total Invested",
            returnsLabel = "Est. Returns",
            onSaveScenario = saveToVault
        )

        if (adjustForInflation) {
            InflationAdjustedSummaryCard(
                nominalValue = shownTotal,
                realValue = shownRealTotal,
                investedAmount = shownInvested,
                inflationRate = inflationRate,
                years = yearsInt.toDouble()
            )
        }

        Surface(
            shape = RoundedCornerShape(tokens.cardCornerRadius),
            color = tokens.gainBg,
            border = BorderStroke(1.dp, tokens.gainInk.copy(alpha = 0.30f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Step-Up Advantage (+${annualStepUpPct.roundToInt()}%/yr)",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = tokens.gainInk
                    )
                    Text(
                        text = "Year $yearsInt monthly SIP: ${inrFormat.format(stepUpProj.finalMonthlySip)}/mo",
                        style = MaterialTheme.typography.bodySmall,
                        color = tokens.inkSecondary
                    )
                }
                Text(
                    text = "+$compactAdvantage",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontFeatureSettings = "tnum"
                    ),
                    color = tokens.gainInk
                )
            }
        }

        SipCoreInputsSection(
            monthlyInvestment = monthlyInvestment,
            onMonthlyInvestmentChange = { monthlyInvestment = it },
            expectedReturnRate = expectedReturnRate,
            onExpectedReturnRateChange = { expectedReturnRate = it },
            timePeriodYears = timePeriodYears,
            onTimePeriodYearsChange = { timePeriodYears = it },
            monthlyLabel = "Starting Monthly SIP",
            returnLabel = "Expected Return (p.a)",
            afterMonthlyContent = {
                CalculatorInput(
                    label = "Annual Step-Up",
                    value = annualStepUpPct,
                    onValueChange = { annualStepUpPct = it },
                    range = 0.0..30.0,
                    symbol = "%"
                )
            },
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
            title = "Compounding Trajectory",
            points = shownPoints
        )

        NeonCard {
            ResultRow("Flat Regular SIP Maturity", formatCompactINR(regTotal, inrFormat))
            ResultRow("Step-Up SIP Maturity", formatCompactINR(stepUpProj.totalValue, inrFormat), isTotal = true)
        }

        SipGainsDonut(
            invested = shownInvested,
            returns = shownReturns
        )

        SnackbarHost(hostState = snackbarHostState)
    }
}
