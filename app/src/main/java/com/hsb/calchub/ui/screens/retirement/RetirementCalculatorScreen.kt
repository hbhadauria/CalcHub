package com.hsb.calchub.ui.screens.retirement

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hsb.calchub.data.repository.RecentCalculation
import com.hsb.calchub.data.repository.SavedScenario
import com.hsb.calchub.di.LocalAppContainer
import com.hsb.calchub.domain.logic.CalculatorLogic
import com.hsb.calchub.ui.components.CalculatorInput
import com.hsb.calchub.ui.components.CalculatorScaffold
import com.hsb.calchub.ui.components.DonutChart
import com.hsb.calchub.ui.components.DonutChartData
import com.hsb.calchub.ui.components.FireTimelineChart
import com.hsb.calchub.ui.components.NeonCard
import com.hsb.calchub.ui.components.OdometerText
import com.hsb.calchub.ui.components.ResultRow
import com.hsb.calchub.ui.components.formatCompactINR
import com.hsb.calchub.ui.theme.LocalCalcHubTokens
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt

/**
 * Interactive Retirement & FIRE planner featuring target corpus verdict, Age 18–85 timeline chart,
 * total contribution vs growth breakdown, and safe age invariant guards.
 */
@Composable
fun RetirementCalculatorScreen(onBackClick: () -> Unit) {
    val userDataRepository = LocalAppContainer.current.userDataRepository

    var currentAge by rememberSaveable { mutableDoubleStateOf(28.0) }
    var retirementAge by rememberSaveable { mutableDoubleStateOf(55.0) }
    var monthlyExpenses by rememberSaveable { mutableDoubleStateOf(60_000.0) }
    var inflationRate by rememberSaveable { mutableDoubleStateOf(6.0) }
    var expectedReturn by rememberSaveable { mutableDoubleStateOf(12.0) }

    val safeCurrentAge = currentAge.coerceIn(18.0, 69.0)
    val safeRetireAge = retirementAge.coerceIn(safeCurrentAge + 1.0, 75.0)
    val yearsToRetire = (safeRetireAge - safeCurrentAge).coerceAtLeast(1.0)

    val (monthlySip, corpusNeeded, futureAnnualExpenses) = remember(
        safeCurrentAge,
        safeRetireAge,
        monthlyExpenses,
        inflationRate,
        expectedReturn
    ) {
        CalculatorLogic.calculateRetirement(
            currentAge = safeCurrentAge,
            retirementAge = safeRetireAge,
            monthlyExpenses = monthlyExpenses,
            inflationRate = inflationRate,
            expectedReturn = expectedReturn
        )
    }

    val safeMonthlySip = if (monthlySip.isFinite()) monthlySip.coerceAtLeast(0.0) else 0.0
    val safeCorpus = if (corpusNeeded.isFinite()) corpusNeeded.coerceAtLeast(0.0) else 0.0
    val totalContributed = safeMonthlySip * yearsToRetire * 12.0
    val wealthCompounded = (safeCorpus - totalContributed).coerceAtLeast(0.0)

    val inrFormat = remember {
        NumberFormat.getCurrencyInstance(Locale.Builder().setLanguage("en").setRegion("IN").build()).apply {
            maximumFractionDigits = 0
        }
    }
    val compactCorpus = remember(safeCorpus) { formatCompactINR(safeCorpus, inrFormat) }
    val compactSip = remember(safeMonthlySip) { formatCompactINR(safeMonthlySip, inrFormat) }

    LaunchedEffect(safeCurrentAge, safeRetireAge, monthlyExpenses, compactCorpus) {
        userDataRepository.updateLastUsed(
            RecentCalculation(
                route = "retirement",
                calculatorName = "FIRE / Retirement",
                paramsSummary = "Age ${safeCurrentAge.roundToInt()} → ${safeRetireAge.roundToInt()} · ${inrFormat.format(monthlyExpenses)}/mo",
                formattedValue = compactCorpus,
                secondarySummary = "SIP needed: $compactSip/mo"
            )
        )
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val saveToVault: () -> Unit = {
        userDataRepository.saveScenario(
            SavedScenario(
                id = "fire_${System.currentTimeMillis()}",
                route = "retirement",
                title = "FIRE @ ${safeRetireAge.roundToInt()}",
                summary = "${inrFormat.format(monthlyExpenses)}/mo · 25x corpus",
                resultFormatted = compactCorpus
            )
        )
        scope.launch {
            snackbarHostState.showSnackbar("Saved FIRE plan ($compactCorpus) to Vault")
        }
    }

    CalculatorScaffold(
        title = "Retirement / FIRE",
        onBackClick = onBackClick,
        calculatorId = "retirement",
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
            NeonCard(
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
                useGradientBorder = true
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(tokens.accentPrimary)
                    )
                    Text(
                        text = "TARGET CORPUS AT AGE ${safeRetireAge.roundToInt()}",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = tokens.accentPrimary
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                OdometerText(
                    text = compactCorpus,
                    style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = tokens.inkPrimary,
                    highlightLedger = true,
                    useDotMatrixInMatrixSkin = true
                )
                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = tokens.cardBorder)
                Spacer(modifier = Modifier.height(10.dp))
                ResultRow("Monthly SIP Needed", "${inrFormat.format(safeMonthlySip)}/mo", isTotal = true)
                ResultRow(
                    "Annual Expense @ ${safeRetireAge.roundToInt()}",
                    formatCompactINR(if (futureAnnualExpenses.isFinite()) futureAnnualExpenses else 0.0, inrFormat)
                )
                ResultRow("Accumulation Horizon", "${yearsToRetire.roundToInt()} yrs")
            }

            FireTimelineChart(
                currentAge = safeCurrentAge.roundToInt(),
                retireAge = safeRetireAge.roundToInt()
            )

            NeonCard {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    DonutChart(
                        data = listOf(
                            DonutChartData(totalContributed, tokens.chartStart, "SIP Invested"),
                            DonutChartData(wealthCompounded, tokens.chartEnd, "Compounding")
                        ),
                        centerLabelText = "Corpus Split"
                    )
                }
            }

            NeonCard {
                CalculatorInput(
                    label = "Current Age",
                    value = safeCurrentAge,
                    onValueChange = { next ->
                        currentAge = next
                        if (retirementAge <= next) {
                            retirementAge = (next + 1.0).coerceAtMost(75.0)
                        }
                    },
                    range = 18.0..60.0,
                    symbol = "Yr"
                )
                Spacer(modifier = Modifier.height(14.dp))
                CalculatorInput(
                    label = "Target Retirement Age",
                    value = safeRetireAge,
                    onValueChange = { next ->
                        retirementAge = next.coerceAtLeast(safeCurrentAge + 1.0)
                    },
                    range = (safeCurrentAge + 1.0).coerceAtMost(74.0)..75.0,
                    symbol = "Yr"
                )
                Spacer(modifier = Modifier.height(14.dp))
                CalculatorInput(
                    label = "Current Monthly Expenses",
                    value = monthlyExpenses,
                    onValueChange = { monthlyExpenses = it },
                    range = 10_000.0..300_000.0,
                    symbol = "₹"
                )
                Spacer(modifier = Modifier.height(14.dp))
                CalculatorInput(
                    label = "Expected Inflation (p.a)",
                    value = inflationRate,
                    onValueChange = { inflationRate = it },
                    range = 3.0..12.0,
                    symbol = "%"
                )
                Spacer(modifier = Modifier.height(14.dp))
                CalculatorInput(
                    label = "Expected Return (p.a)",
                    value = expectedReturn,
                    onValueChange = { expectedReturn = it },
                    range = 6.0..18.0,
                    symbol = "%"
                )
            }

            SnackbarHost(hostState = snackbarHostState)
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
