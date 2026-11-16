package com.hsb.calchub.ui.screens.home_loan_emi

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.hsb.calchub.ui.components.CalculatorScaffold
import com.hsb.calchub.ui.components.LoanAmortizationChart
import com.hsb.calchub.ui.components.NeonCard
import com.hsb.calchub.ui.components.SegmentedPillGroup
import com.hsb.calchub.ui.components.formatCompactINR
import com.hsb.calchub.ui.screens.emi.LoanDonutCard
import com.hsb.calchub.ui.screens.emi.LoanInputsCard
import com.hsb.calchub.ui.screens.emi.ResultCardWithLabels
import com.hsb.calchub.ui.theme.LocalCalcHubTokens
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt

private enum class HomeLoanPlanMode(val label: String) {
    STANDARD("Standard EMI"),
    EXTRA_EMI("+1 Extra EMI / yr")
}

private data class HomeLoanSchedule(
    val yearlyBreakdown: List<Pair<Double, Double>>,
    val crossoverYear: Int,
    val totalInterest: Double,
    val totalMonths: Int
)

private fun simulateHomeLoanSchedule(
    principal: Double,
    annualRate: Double,
    tenureYears: Int,
    monthlyEmi: Double,
    extraEmiEachYear: Boolean
): HomeLoanSchedule {
    val maxMonths = (tenureYears.coerceAtLeast(1) * 12).coerceAtLeast(1)
    val r = (annualRate / 12.0 / 100.0).coerceAtLeast(0.0)
    var balance = principal.coerceAtLeast(0.0)
    var cumInterest = 0.0
    var monthsUsed = 0
    val yearly = mutableListOf<Pair<Double, Double>>()
    var crossover = tenureYears.coerceAtLeast(1)

    var yrPrin = 0.0
    var yrIntr = 0.0

    for (m in 1..maxMonths) {
        if (balance <= 0.01) break
        monthsUsed = m
        val intr = balance * r
        val baseDue = (monthlyEmi - intr).coerceAtLeast(0.0)
        val extra = if (extraEmiEachYear && m % 12 == 0) monthlyEmi else 0.0
        val prinPaid = (baseDue + extra).coerceIn(0.0, balance)
        balance -= prinPaid
        cumInterest += intr
        yrPrin += prinPaid
        yrIntr += intr

        if (m % 12 == 0 || balance <= 0.01 || m == maxMonths) {
            yearly.add(yrPrin to yrIntr)
            val yrIdx = yearly.size
            if (crossover == tenureYears.coerceAtLeast(1) && yrPrin > yrIntr) {
                crossover = yrIdx
            }
            yrPrin = 0.0
            yrIntr = 0.0
        }
    }

    return HomeLoanSchedule(
        yearlyBreakdown = yearly,
        crossoverYear = crossover,
        totalInterest = cumInterest,
        totalMonths = monthsUsed.coerceAtLeast(1)
    )
}

/**
 * Home Loan EMI calculator with optional 1-extra-EMI-per-year prepayment simulator,
 * yearly amortization bars, and principal/interest crossover marker.
 */
@Composable
fun HomeLoanEmiCalculatorScreen(onBackClick: () -> Unit) {
    val userDataRepository = LocalAppContainer.current.userDataRepository

    var mode by rememberSaveable { mutableStateOf(HomeLoanPlanMode.STANDARD) }
    var loanAmount by rememberSaveable { mutableDoubleStateOf(5_000_000.0) }
    var interestRate by rememberSaveable { mutableDoubleStateOf(8.5) }
    var tenureYears by rememberSaveable { mutableDoubleStateOf(20.0) }

    val (emi, baseTotalInterest, baseTotalPayment) = remember(loanAmount, interestRate, tenureYears) {
        CalculatorLogic.calculateHomeLoanEMI(loanAmount, interestRate, tenureYears)
    }

    val yearsInt = tenureYears.roundToInt().coerceAtLeast(1)
    val baseSchedule = remember(loanAmount, interestRate, yearsInt, emi) {
        simulateHomeLoanSchedule(loanAmount, interestRate, yearsInt, emi, extraEmiEachYear = false)
    }
    val prepaySchedule = remember(loanAmount, interestRate, yearsInt, emi) {
        simulateHomeLoanSchedule(loanAmount, interestRate, yearsInt, emi, extraEmiEachYear = true)
    }

    val isPrepay = mode == HomeLoanPlanMode.EXTRA_EMI
    val activeSchedule = if (isPrepay) prepaySchedule else baseSchedule
    val displayedInterest = if (isPrepay) prepaySchedule.totalInterest else baseTotalInterest
    val displayedTotal = if (isPrepay) loanAmount + prepaySchedule.totalInterest else baseTotalPayment
    val interestSaved = (baseTotalInterest - prepaySchedule.totalInterest).coerceAtLeast(0.0)
    val monthsSaved = (yearsInt * 12 - prepaySchedule.totalMonths).coerceAtLeast(0)

    val inrFormat = remember {
        NumberFormat.getCurrencyInstance(Locale.Builder().setLanguage("en").setRegion("IN").build()).apply {
            maximumFractionDigits = 0
        }
    }
    val compactLoan = remember(loanAmount) { formatCompactINR(loanAmount, inrFormat) }
    val compactSaved = remember(interestSaved) { formatCompactINR(interestSaved, inrFormat) }

    LaunchedEffect(loanAmount, interestRate, yearsInt, emi) {
        userDataRepository.updateLastUsed(
            RecentCalculation(
                route = "home_loan_emi",
                calculatorName = "Home Loan EMI",
                paramsSummary = "$compactLoan @ ${String.format(Locale.US, "%.1f", interestRate)}% · $yearsInt yrs",
                formattedValue = "${inrFormat.format(emi)}/mo",
                secondarySummary = "Save $compactSaved with +1 EMI/yr"
            )
        )
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val saveToVault: () -> Unit = {
        userDataRepository.saveScenario(
            SavedScenario(
                id = "home_loan_${System.currentTimeMillis()}",
                route = "home_loan_emi",
                title = "Home Loan $compactLoan",
                summary = "${String.format(Locale.US, "%.1f", interestRate)}% · $yearsInt yrs",
                resultFormatted = "${inrFormat.format(emi)}/mo"
            )
        )
        scope.launch {
            snackbarHostState.showSnackbar("Saved Home Loan scenario to Vault")
        }
    }

    CalculatorScaffold(
        title = "Home Loan EMI",
        onBackClick = onBackClick,
        calculatorId = "home_loan_emi",
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
            SegmentedPillGroup(
                options = HomeLoanPlanMode.entries,
                selectedOption = mode,
                onOptionSelected = { mode = it },
                labelProvider = { it.label },
                modifier = Modifier.fillMaxWidth()
            )

            ResultCardWithLabels(
                label1 = "Monthly EMI",
                value1 = emi,
                label2 = "Principal Amount",
                value2 = loanAmount,
                label3 = if (isPrepay) "Interest (With +1 EMI/yr)" else "Total Interest",
                value3 = displayedInterest,
                label4 = "Total Outflow",
                value4 = displayedTotal
            )

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
                            text = if (isPrepay) "Prepayment Active" else "Prepay Hack: +1 Extra EMI / yr",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = tokens.gainInk
                        )
                        val yrsOff = monthsSaved / 12
                        val mosOff = monthsSaved % 12
                        Text(
                            text = "Closes ${yrsOff}y ${mosOff}m sooner (${prepaySchedule.totalMonths} EMIs vs ${yearsInt * 12})",
                            style = MaterialTheme.typography.bodySmall,
                            color = tokens.inkSecondary
                        )
                    }
                    Text(
                        text = "-$compactSaved",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontFeatureSettings = "tnum"
                        ),
                        color = tokens.gainInk
                    )
                }
            }

            LoanAmortizationChart(
                yearlyBreakdown = activeSchedule.yearlyBreakdown,
                crossoverYear = activeSchedule.crossoverYear
            )

            LoanDonutCard(
                principal = loanAmount,
                interest = displayedInterest
            )

            LoanInputsCard(
                loanAmount = loanAmount,
                onLoanAmountChange = { loanAmount = it },
                interestRate = interestRate,
                onInterestRateChange = { interestRate = it },
                tenureYears = tenureYears,
                onTenureYearsChange = { tenureYears = it },
                loanLabel = "Home Loan Amount",
                loanRange = 500_000.0..50_000_000.0,
                rateRange = 6.0..15.0,
                tenureLabel = "Tenure",
                tenureRange = 5.0..30.0
            )

            SnackbarHost(hostState = snackbarHostState)
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
