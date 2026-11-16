package com.hsb.calchub.ui.screens.sip

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hsb.calchub.data.repository.RecentCalculation
import com.hsb.calchub.data.repository.SavedScenario
import com.hsb.calchub.di.LocalAppContainer
import com.hsb.calchub.domain.logic.CalculatorLogic
import com.hsb.calchub.ui.components.CalculatorInput
import com.hsb.calchub.ui.components.CalculatorScaffold
import com.hsb.calchub.ui.components.DonutChart
import com.hsb.calchub.ui.components.DonutChartData
import com.hsb.calchub.ui.components.NeonCard
import com.hsb.calchub.ui.components.ResultCard
import com.hsb.calchub.ui.components.WealthGrowthSplineChart
import com.hsb.calchub.ui.components.formatCompactINR
import com.hsb.calchub.ui.theme.LocalCalcHubTokens
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt

/**
 * Interactive SIP Wealth Builder featuring above-the-fold verdict card,
 * scrubbable Canvas + PathMeasure spline growth chart, donut breakdown, and Vault saving.
 */
@Composable
fun SipCalculatorScreen(onBackClick: () -> Unit) {
    val userDataRepository = LocalAppContainer.current.userDataRepository

    var monthlyInvestment by rememberSaveable { mutableDoubleStateOf(10_000.0) }
    var expectedReturnRate by rememberSaveable { mutableDoubleStateOf(12.0) }
    var timePeriodYears by rememberSaveable { mutableDoubleStateOf(15.0) }

    val (investedAmount, estimatedReturns, totalValue) = remember(
        monthlyInvestment,
        expectedReturnRate,
        timePeriodYears
    ) {
        CalculatorLogic.calculateSIP(monthlyInvestment, expectedReturnRate, timePeriodYears)
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

    val inrFormat = remember {
        NumberFormat.getCurrencyInstance(Locale.Builder().setLanguage("en").setRegion("IN").build()).apply {
            maximumFractionDigits = 0
        }
    }
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

    CalculatorScaffold(
        title = "SIP Calculator",
        onBackClick = onBackClick,
        calculatorId = "sip",
        onSaveScenario = saveToVault
    ) { paddingValues ->
        val tokens = LocalCalcHubTokens.current
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
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

            WealthGrowthSplineChart(
                title = "Wealth Projection (Scrub to Inspect)",
                points = yearlyPoints
            )

            NeonCard {
                SipGainsDonut(
                    invested = investedAmount,
                    returns = estimatedReturns
                )
            }

            SipCoreInputsSection(
                monthlyInvestment = monthlyInvestment,
                onMonthlyInvestmentChange = { monthlyInvestment = it },
                expectedReturnRate = expectedReturnRate,
                onExpectedReturnRateChange = { expectedReturnRate = it },
                timePeriodYears = timePeriodYears,
                onTimePeriodYearsChange = { timePeriodYears = it }
            )

            SnackbarHost(hostState = snackbarHostState)
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
internal fun SipGainsDonut(invested: Double, returns: Double) {
    val tokens = com.hsb.calchub.ui.theme.LocalCalcHubTokens.current
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DonutChart(
            data = listOf(
                DonutChartData(invested, tokens.chartStart, "Invested"),
                DonutChartData(returns, tokens.chartEnd, "Returns")
            ),
            centerLabelText = "Gains Share"
        )
    }
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
    afterMonthlyContent: (@Composable () -> Unit)? = null
) {
    NeonCard {
        CalculatorInput(
            label = monthlyLabel,
            value = monthlyInvestment,
            onValueChange = onMonthlyInvestmentChange,
            range = 500.0..100_000.0,
            symbol = "₹"
        )
        if (afterMonthlyContent != null) {
            Spacer(modifier = Modifier.height(14.dp))
            afterMonthlyContent()
        }
        Spacer(modifier = Modifier.height(14.dp))
        CalculatorInput(
            label = returnLabel,
            value = expectedReturnRate,
            onValueChange = onExpectedReturnRateChange,
            range = 1.0..30.0,
            symbol = "%"
        )
        Spacer(modifier = Modifier.height(14.dp))
        CalculatorInput(
            label = "Time Period",
            value = timePeriodYears,
            onValueChange = onTimePeriodYearsChange,
            range = 1.0..40.0,
            symbol = "Yr"
        )
    }
}
