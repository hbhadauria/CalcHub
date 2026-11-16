package com.hsb.calchub.ui.screens.income_tax

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
import androidx.compose.material3.HorizontalDivider
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
import com.hsb.calchub.ui.components.CalculatorScaffold
import com.hsb.calchub.ui.components.DonutChart
import com.hsb.calchub.ui.components.DonutChartData
import com.hsb.calchub.ui.components.NeonCard
import com.hsb.calchub.ui.components.OdometerText
import com.hsb.calchub.ui.components.ResultRow
import com.hsb.calchub.ui.components.SegmentedPillGroup
import com.hsb.calchub.ui.components.formatCompactINR
import com.hsb.calchub.ui.theme.LocalCalcHubTokens
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.abs

private enum class TaxRegimeTab(val label: String) {
    NEW_FY26("New Regime FY26"),
    OLD_REGIME("Old Regime")
}

/**
 * Simplified FY 2025-26 New Tax Regime estimation with Rs 75,000 standard deduction,
 * Budget 2025 slabs, Sec 87A rebate up to Rs 12L taxable income (with marginal relief),
 * and 4% Health & Education Cess.
 */
private fun estimateFy26NewRegimeTax(annualIncome: Double): Double {
    val taxable = (annualIncome - 75_000.0).coerceAtLeast(0.0)
    if (taxable <= 1_200_000.0) return 0.0

    var slabTax = 0.0
    val bands = listOf(
        400_000.0 to 0.0,
        400_000.0 to 0.05,
        400_000.0 to 0.10,
        400_000.0 to 0.15,
        400_000.0 to 0.20,
        400_000.0 to 0.25
    )
    var remaining = taxable
    for ((bandWidth, rate) in bands) {
        val slice = remaining.coerceAtMost(bandWidth)
        slabTax += slice * rate
        remaining -= slice
        if (remaining <= 0.0) break
    }
    if (remaining > 0.0) {
        slabTax += remaining * 0.30
    }

    val marginalCap = (taxable - 1_200_000.0).coerceAtLeast(0.0)
    val taxAfterRelief = minOf(slabTax, marginalCap.coerceAtLeast(0.0).let {
        if (taxable <= 1_275_000.0) minOf(slabTax, it) else slabTax
    })
    return taxAfterRelief * 1.04
}

/**
 * Interactive Income Tax Calculator featuring FY26 New Regime vs Old Regime face-off comparison,
 * winner savings banner, Donut outflow chart, and Vault persistence.
 */
@Composable
fun IncomeTaxCalculatorScreen(onBackClick: () -> Unit) {
    val userDataRepository = LocalAppContainer.current.userDataRepository

    var selectedRegime by rememberSaveable { mutableStateOf(TaxRegimeTab.NEW_FY26) }
    var annualIncome by rememberSaveable { mutableDoubleStateOf(1_800_000.0) }
    var deductions by rememberSaveable { mutableDoubleStateOf(250_000.0) }

    val (_, oldTax, oldNetIncome) = remember(annualIncome, deductions) {
        CalculatorLogic.calculateIncomeTax(annualIncome, deductions)
    }
    val newTax = remember(annualIncome) {
        estimateFy26NewRegimeTax(annualIncome)
    }
    val newNetIncome = (annualIncome - newTax).coerceAtLeast(0.0)

    val newRegimeWins = newTax <= oldTax
    val savingsDelta = abs(oldTax - newTax)

    val shownTax = if (selectedRegime == TaxRegimeTab.NEW_FY26) newTax else oldTax
    val shownNet = if (selectedRegime == TaxRegimeTab.NEW_FY26) newNetIncome else oldNetIncome
    val effectiveRate = if (annualIncome > 0.0) (shownTax / annualIncome) * 100.0 else 0.0

    val inrFormat = remember {
        NumberFormat.getCurrencyInstance(Locale.Builder().setLanguage("en").setRegion("IN").build()).apply {
            maximumFractionDigits = 0
        }
    }
    val compactIncome = remember(annualIncome) { formatCompactINR(annualIncome, inrFormat) }
    val compactTax = remember(shownTax) { formatCompactINR(shownTax, inrFormat) }
    val compactDelta = remember(savingsDelta) { formatCompactINR(savingsDelta, inrFormat) }

    LaunchedEffect(annualIncome, deductions, selectedRegime, compactTax) {
        userDataRepository.updateLastUsed(
            RecentCalculation(
                route = "income_tax",
                calculatorName = "Income Tax FY26",
                paramsSummary = "$compactIncome CTC · ${selectedRegime.label}",
                formattedValue = compactTax,
                secondarySummary = if (newRegimeWins) "New Regime saves $compactDelta" else "Old Regime saves $compactDelta"
            )
        )
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val saveToVault: () -> Unit = {
        userDataRepository.saveScenario(
            SavedScenario(
                id = "tax_${System.currentTimeMillis()}",
                route = "income_tax",
                title = "Tax on $compactIncome",
                summary = "${selectedRegime.label} · 80C/D ${formatCompactINR(deductions, inrFormat)}",
                resultFormatted = compactTax
            )
        )
        scope.launch {
            snackbarHostState.showSnackbar("Saved Tax scenario ($compactTax) to Vault")
        }
    }

    CalculatorScaffold(
        title = "Income Tax FY26",
        onBackClick = onBackClick,
        calculatorId = "income_tax",
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
                options = TaxRegimeTab.entries,
                selectedOption = selectedRegime,
                onOptionSelected = { selectedRegime = it },
                labelProvider = { it.label },
                modifier = Modifier.fillMaxWidth()
            )

            NeonCard(useGradientBorder = true) {
                Text(
                    text = "${selectedRegime.label.uppercase()} · ESTIMATED TAX",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = tokens.accentPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                OdometerText(
                    text = inrFormat.format(shownTax),
                    style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = tokens.inkPrimary,
                    highlightLedger = true,
                    useDotMatrixInMatrixSkin = true
                )
                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = tokens.cardBorder)
                Spacer(modifier = Modifier.height(10.dp))
                ResultRow("Net Take-Home (Post Tax)", inrFormat.format(shownNet), isTotal = true)
                ResultRow("Effective Tax Rate", String.format(Locale.US, "%.1f%%", effectiveRate))
            }

            // Face-Off Comparison Banner
            Surface(
                shape = RoundedCornerShape(tokens.cardCornerRadius),
                color = tokens.gainBg,
                border = BorderStroke(1.dp, tokens.gainInk.copy(alpha = 0.30f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (newRegimeWins) "Winner: New Regime (FY26)" else "Winner: Old Regime",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = tokens.gainInk
                        )
                        Text(
                            text = "Saves $compactDelta",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontFeatureSettings = "tnum"
                            ),
                            color = tokens.gainInk
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            color = tokens.surfaceLowest
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("New FY26 (₹75K Std)", style = MaterialTheme.typography.labelSmall, color = tokens.inkSecondary)
                                Text(
                                    text = formatCompactINR(newTax, inrFormat),
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontFeatureSettings = "tnum"),
                                    color = tokens.inkPrimary
                                )
                            }
                        }
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            color = tokens.surfaceLowest
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Old Regime Slabs", style = MaterialTheme.typography.labelSmall, color = tokens.inkSecondary)
                                Text(
                                    text = formatCompactINR(oldTax, inrFormat),
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontFeatureSettings = "tnum"),
                                    color = tokens.inkPrimary
                                )
                            }
                        }
                    }
                }
            }

            NeonCard {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    DonutChart(
                        data = listOf(
                            DonutChartData(shownNet, tokens.gainInk, "Net Pay"),
                            DonutChartData(shownTax, tokens.lossInk, "Tax")
                        ),
                        centerLabelText = "Tax Outflow"
                    )
                }
            }

            NeonCard {
                CalculatorInput(
                    label = "Gross Annual Income",
                    value = annualIncome,
                    onValueChange = { annualIncome = it },
                    range = 300_000.0..10_000_000.0,
                    symbol = "₹"
                )
                Spacer(modifier = Modifier.height(14.dp))
                CalculatorInput(
                    label = "Old Regime Deductions (80C/80D/HRA)",
                    value = deductions,
                    onValueChange = { deductions = it },
                    range = 0.0..1_000_000.0,
                    symbol = "₹"
                )
            }

            SnackbarHost(hostState = snackbarHostState)
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
