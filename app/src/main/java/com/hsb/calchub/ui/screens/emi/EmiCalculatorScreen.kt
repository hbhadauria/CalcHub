package com.hsb.calchub.ui.screens.emi

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
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
import com.hsb.calchub.ui.components.LoanAmortizationChart
import com.hsb.calchub.ui.components.NeonCard
import com.hsb.calchub.ui.components.ResultRow
import com.hsb.calchub.ui.components.formatCompactINR
import com.hsb.calchub.ui.theme.LocalCalcHubTokens
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt

internal data class AmortizationSummary(
    val yearlyBreakdown: List<Pair<Double, Double>>,
    val crossoverYear: Int
)

internal fun computeAmortizationSummary(
    principal: Double,
    annualInterestRate: Double,
    tenureYears: Int,
    monthlyEmi: Double
): AmortizationSummary {
    val safeYears = tenureYears.coerceAtLeast(1)
    val r = (annualInterestRate / 12.0 / 100.0).coerceAtLeast(0.0)
    var balance = principal.coerceAtLeast(0.0)
    val yearly = mutableListOf<Pair<Double, Double>>()
    var crossover = safeYears

    for (yr in 1..safeYears) {
        var yrPrin = 0.0
        var yrIntr = 0.0
        for (m in 1..12) {
            if (balance <= 0.0) break
            val intr = balance * r
            val prin = (monthlyEmi - intr).coerceIn(0.0, balance)
            yrPrin += prin
            yrIntr += intr
            balance -= prin
        }
        yearly.add(yrPrin to yrIntr)
        if (crossover == safeYears && yrPrin > yrIntr) {
            crossover = yr
        }
    }

    return AmortizationSummary(yearlyBreakdown = yearly, crossoverYear = crossover)
}

/**
 * Interactive EMI Calculator displaying Monthly EMI Verdict, yearly Principal-vs-Interest
 * crossover chart, Donut breakdown, and slider inputs.
 */
@Composable
fun EmiCalculatorScreen(onBackClick: () -> Unit) {
    val userDataRepository = LocalAppContainer.current.userDataRepository

    var loanAmount by rememberSaveable { mutableDoubleStateOf(1_000_000.0) }
    var interestRate by rememberSaveable { mutableDoubleStateOf(9.0) }
    var tenureYears by rememberSaveable { mutableDoubleStateOf(10.0) }

    val (emi, totalInterest, totalPayment) = remember(loanAmount, interestRate, tenureYears) {
        CalculatorLogic.calculateEMI(loanAmount, interestRate, tenureYears)
    }

    val yearsInt = tenureYears.roundToInt().coerceAtLeast(1)
    val amortization = remember(loanAmount, interestRate, yearsInt, emi) {
        computeAmortizationSummary(loanAmount, interestRate, yearsInt, emi)
    }

    val inrFormat = remember {
        NumberFormat.getCurrencyInstance(Locale.Builder().setLanguage("en").setRegion("IN").build()).apply {
            maximumFractionDigits = 0
        }
    }
    val compactLoan = remember(loanAmount) { formatCompactINR(loanAmount, inrFormat) }
    val compactInterest = remember(totalInterest) { formatCompactINR(totalInterest, inrFormat) }

    LaunchedEffect(loanAmount, interestRate, tenureYears, emi) {
        userDataRepository.updateLastUsed(
            RecentCalculation(
                route = "emi",
                calculatorName = "EMI Calculator",
                paramsSummary = "$compactLoan @ ${String.format(Locale.US, "%.1f", interestRate)}% · $yearsInt yrs",
                formattedValue = "${inrFormat.format(emi)}/mo",
                secondarySummary = "Total interest $compactInterest"
            )
        )
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val saveToVault: () -> Unit = {
        userDataRepository.saveScenario(
            SavedScenario(
                id = "emi_${System.currentTimeMillis()}",
                route = "emi",
                title = "EMI $compactLoan",
                summary = "${String.format(Locale.US, "%.1f", interestRate)}% · $yearsInt yrs",
                resultFormatted = "${inrFormat.format(emi)}/mo"
            )
        )
        scope.launch {
            snackbarHostState.showSnackbar("Saved EMI scenario to Vault")
        }
    }

    CalculatorScaffold(
        title = "EMI Calculator",
        onBackClick = onBackClick,
        calculatorId = "emi",
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
            ResultCardWithLabels(
                label1 = "Monthly EMI",
                value1 = emi,
                label2 = "Principal Amount",
                value2 = loanAmount,
                label3 = "Total Interest",
                value3 = totalInterest,
                label4 = "Total Payment",
                value4 = totalPayment
            )

            LoanAmortizationChart(
                yearlyBreakdown = amortization.yearlyBreakdown,
                crossoverYear = amortization.crossoverYear
            )

            NeonCard {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    DonutChart(
                        data = listOf(
                            DonutChartData(loanAmount, tokens.gainInk, "Principal"),
                            DonutChartData(totalInterest, tokens.lossInk, "Interest")
                        ),
                        centerLabelText = "Interest Share"
                    )
                }
            }

            NeonCard {
                CalculatorInput(
                    label = "Loan Amount",
                    value = loanAmount,
                    onValueChange = { loanAmount = it },
                    range = 50_000.0..10_000_000.0,
                    symbol = "₹"
                )
                Spacer(modifier = Modifier.height(14.dp))
                CalculatorInput(
                    label = "Interest Rate (p.a)",
                    value = interestRate,
                    onValueChange = { interestRate = it },
                    range = 5.0..20.0,
                    symbol = "%"
                )
                Spacer(modifier = Modifier.height(14.dp))
                CalculatorInput(
                    label = "Loan Tenure",
                    value = tenureYears,
                    onValueChange = { tenureYears = it },
                    range = 1.0..30.0,
                    symbol = "Yr"
                )
            }

            SnackbarHost(hostState = snackbarHostState)
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

/**
 * Hero EMI Verdict Card supporting 3 or 4 labeled financial rows using theme tokens.
 */
@Composable
fun ResultCardWithLabels(
    label1: String,
    value1: Double,
    label2: String,
    value2: Double,
    label3: String,
    value3: Double,
    label4: String? = null,
    value4: Double? = null
) {
    val tokens = LocalCalcHubTokens.current
    val currencyFormat = remember {
        NumberFormat.getCurrencyInstance(Locale.Builder().setLanguage("en").setRegion("IN").build()).apply {
            maximumFractionDigits = 0
        }
    }

    NeonCard(useGradientBorder = true) {
        ResultRow(label = label1, value = currencyFormat.format(if (value1.isFinite()) value1 else 0.0), isTotal = true)
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = tokens.cardBorder)
        Spacer(modifier = Modifier.height(8.dp))
        ResultRow(label = label2, value = currencyFormat.format(if (value2.isFinite()) value2 else 0.0))
        ResultRow(label = label3, value = currencyFormat.format(if (value3.isFinite()) value3 else 0.0))
        if (label4 != null && value4 != null) {
            ResultRow(label = label4, value = currencyFormat.format(if (value4.isFinite()) value4 else 0.0))
        }
    }
}
