package com.hsb.calchub.ui.screens.emi

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hsb.calchub.data.repository.RecentCalculation
import com.hsb.calchub.data.repository.SavedScenario
import com.hsb.calchub.di.LocalAppContainer
import com.hsb.calchub.domain.logic.CalculatorLogic
import com.hsb.calchub.domain.logic.loan.LoanScheduleCalculator
import com.hsb.calchub.ui.components.CalculatorInput
import com.hsb.calchub.ui.components.CalculatorScrollScaffold
import com.hsb.calchub.ui.components.InvestedReturnsDonutCard
import com.hsb.calchub.ui.components.LoanAmortizationChart
import com.hsb.calchub.ui.components.NeonCard
import com.hsb.calchub.ui.components.ResultRow
import com.hsb.calchub.ui.components.formatCompactINR
import com.hsb.calchub.ui.components.rememberInrFormat
import com.hsb.calchub.ui.theme.LocalCalcHubTokens
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.roundToInt

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
        LoanScheduleCalculator.computeAmortizationSummary(loanAmount, interestRate, yearsInt, emi)
    }

    val inrFormat = rememberInrFormat()
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

    CalculatorScrollScaffold(
        title = "EMI Calculator",
        onBackClick = onBackClick,
        calculatorId = "emi",
        onSaveScenario = saveToVault,
        verticalSpacing = 14.dp,
        bottomSpacerHeight = 24.dp
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

        LoanInputsCard(
            loanAmount = loanAmount,
            onLoanAmountChange = { loanAmount = it },
            interestRate = interestRate,
            onInterestRateChange = { interestRate = it },
            tenureYears = tenureYears,
            onTenureYearsChange = { tenureYears = it }
        )

        LoanAmortizationChart(
            yearlyBreakdown = amortization.yearlyBreakdown,
            crossoverYear = amortization.crossoverYear
        )

        LoanDonutCard(
            principal = loanAmount,
            interest = totalInterest
        )

        SnackbarHost(hostState = snackbarHostState)
    }
}

@Composable
internal fun LoanDonutCard(principal: Double, interest: Double) {
    InvestedReturnsDonutCard(
        invested = principal,
        returns = interest,
        investedLabel = "Principal",
        returnsLabel = "Interest",
        centerLabelText = "Interest Share",
        useGainLossColors = true
    )
}

@Composable
internal fun LoanInputsCard(
    loanAmount: Double,
    onLoanAmountChange: (Double) -> Unit,
    interestRate: Double,
    onInterestRateChange: (Double) -> Unit,
    tenureYears: Double,
    onTenureYearsChange: (Double) -> Unit,
    loanLabel: String = "Loan Amount",
    loanRange: ClosedFloatingPointRange<Double> = 50_000.0..10_000_000.0,
    rateRange: ClosedFloatingPointRange<Double> = 5.0..20.0,
    tenureLabel: String = "Loan Tenure",
    tenureRange: ClosedFloatingPointRange<Double> = 1.0..30.0
) {
    NeonCard {
        CalculatorInput(
            label = loanLabel,
            value = loanAmount,
            onValueChange = onLoanAmountChange,
            range = loanRange,
            symbol = "₹"
        )
        Spacer(modifier = Modifier.height(10.dp))
        CalculatorInput(
            label = "Interest Rate (p.a)",
            value = interestRate,
            onValueChange = onInterestRateChange,
            range = rateRange,
            symbol = "%"
        )
        Spacer(modifier = Modifier.height(10.dp))
        CalculatorInput(
            label = tenureLabel,
            value = tenureYears,
            onValueChange = onTenureYearsChange,
            range = tenureRange,
            symbol = "Yr"
        )
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
    val currencyFormat = rememberInrFormat()

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
