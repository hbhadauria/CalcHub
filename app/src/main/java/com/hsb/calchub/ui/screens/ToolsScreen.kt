package com.hsb.calchub.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hsb.calchub.data.repository.SavedScenario
import com.hsb.calchub.di.LocalAppContainer
import com.hsb.calchub.domain.logic.CalculatorLogic
import com.hsb.calchub.domain.model.CalculatorCategory
import com.hsb.calchub.domain.model.allCalculators
import com.hsb.calchub.domain.model.categoryForRoute
import com.hsb.calchub.ui.components.CalculatorInput
import com.hsb.calchub.ui.components.NeonCard
import com.hsb.calchub.ui.components.NeonSearch
import com.hsb.calchub.ui.components.ResultRow
import com.hsb.calchub.ui.components.SegmentedPillGroup
import com.hsb.calchub.ui.components.formatCompactINR
import com.hsb.calchub.ui.components.label
import com.hsb.calchub.ui.theme.CategoryPalettes
import com.hsb.calchub.ui.theme.LocalCalcHubTokens
import java.text.NumberFormat
import java.util.Locale

private enum class ToolsLabTab(val label: String) {
    UTILITIES("Utilities"),
    COMPARE("Compare"),
    VAULT("Vault"),
    FORMULAS("Formulas")
}

/**
 * Distinct Tools Lab implementing all 4 approved Decision D8 sub-tabs:
 * 1. Utilities (INR Words/Units converter, Rule of 72/114/144, Inflation-adjusted real return)
 * 2. Compare (Monthly Surplus Face-Off: Invest in SIP vs Prepay Home Loan)
 * 3. Vault (Saved Offline Scenarios with 1-tap open, delete, & reset)
 * 4. Formulas (Searchable 34-calculator formula directory with direct launch)
 */
@Composable
fun ToolsScreen(
    onCalculatorClick: (String) -> Unit
) {
    val tokens = LocalCalcHubTokens.current
    val userDataRepository = LocalAppContainer.current.userDataRepository
    val selectedToolsIdx by userDataRepository.selectedToolsTab.collectAsStateWithLifecycle()
    val activeTab = ToolsLabTab.entries.getOrElse(selectedToolsIdx) { ToolsLabTab.UTILITIES }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Tools Lab",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = tokens.inkPrimary
        )
        Text(
            text = "Quick utilities, strategy compare, local vault & 30+ formulas",
            style = MaterialTheme.typography.bodySmall,
            color = tokens.inkSecondary
        )

        Spacer(modifier = Modifier.height(12.dp))

        SegmentedPillGroup(
            options = ToolsLabTab.entries,
            selectedOption = activeTab,
            onOptionSelected = { tab -> userDataRepository.setSelectedToolsTab(tab.ordinal) },
            labelProvider = { it.label },
            compact = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        when (activeTab) {
            ToolsLabTab.UTILITIES -> ToolsUtilitiesSection()
            ToolsLabTab.COMPARE -> ToolsCompareSection(onCalculatorClick = onCalculatorClick)
            ToolsLabTab.VAULT -> ToolsVaultSection(onCalculatorClick = onCalculatorClick)
            ToolsLabTab.FORMULAS -> ToolsFormulasSection(onCalculatorClick = onCalculatorClick)
        }
    }
}

@Composable
private fun ToolsUtilitiesSection() {
    val tokens = LocalCalcHubTokens.current
    val inrFormat = remember {
        NumberFormat.getCurrencyInstance(Locale.Builder().setLanguage("en").setRegion("IN").build()).apply {
            maximumFractionDigits = 0
        }
    }

    var amountInr by rememberSaveable { mutableDoubleStateOf(2_500_000.0) }
    var expectedReturn by rememberSaveable { mutableDoubleStateOf(12.0) }
    var inflationRate by rememberSaveable { mutableDoubleStateOf(6.0) }

    val lakhs = amountInr / 100_000.0
    val crores = amountInr / 10_000_000.0
    val millions = amountInr / 1_000_000.0
    val usdApprox = amountInr / 84.0
    val indianWords = remember(amountInr) { formatIndianWords(amountInr) }

    val safeRate = expectedReturn.coerceAtLeast(0.5)
    val years2x = 72.0 / safeRate
    val years3x = 114.0 / safeRate
    val years4x = 144.0 / safeRate

    val realReturnPct = (((1.0 + expectedReturn / 100.0) / (1.0 + inflationRate / 100.0)) - 1.0) * 100.0

    LazyColumn(
        contentPadding = PaddingValues(bottom = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item(key = "util_inr_converter") {
            NeonCard(useGradientBorder = true) {
                Text(
                    text = "INR Units & Indian Words",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = tokens.inkPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))
                CalculatorInput(
                    label = "Amount (INR)",
                    value = amountInr,
                    onValueChange = { amountInr = it },
                    range = 1_000.0..50_000_000.0,
                    symbol = "₹"
                )
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = tokens.surfaceLow,
                    border = BorderStroke(1.dp, tokens.cardBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = indianWords,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = tokens.accentPrimary
                        )
                        ResultRow("Lakhs", String.format(Locale.US, "%.2f L", lakhs))
                        ResultRow("Crores", String.format(Locale.US, "%.3f Cr", crores))
                        ResultRow("Millions (Intl)", String.format(Locale.US, "%.2f M", millions))
                        ResultRow("USD (approx ₹84/$)", String.format(Locale.US, "$%,.0f", usdApprox), isTotal = true)
                    }
                }
            }
        }

        item(key = "util_rule_72") {
            NeonCard {
                Text(
                    text = "Rule of 72 · 114 · 144 & Real Return",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = tokens.inkPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))
                CalculatorInput(
                    label = "Nominal Annual Return",
                    value = expectedReturn,
                    onValueChange = { expectedReturn = it },
                    range = 4.0..24.0,
                    symbol = "%"
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RuleStatTile("2× Money", String.format(Locale.US, "%.1f yrs", years2x), Modifier.weight(1f))
                    RuleStatTile("3× Money", String.format(Locale.US, "%.1f yrs", years3x), Modifier.weight(1f))
                    RuleStatTile("4× Money", String.format(Locale.US, "%.1f yrs", years4x), Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(12.dp))
                CalculatorInput(
                    label = "Expected Inflation",
                    value = inflationRate,
                    onValueChange = { inflationRate = it },
                    range = 2.0..12.0,
                    symbol = "%"
                )
                Spacer(modifier = Modifier.height(6.dp))
                ResultRow(
                    label = "Inflation-Adjusted Real Return",
                    value = String.format(Locale.US, "%+.2f%% / yr", realReturnPct),
                    isTotal = true
                )
                Text(
                    text = "Example: ${formatCompactINR(amountInr, inrFormat)} grows at ${String.format(Locale.US, "%.2f%%", realReturnPct)} real purchasing power.",
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.inkSecondary
                )
            }
        }
    }
}

@Composable
private fun RuleStatTile(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    val tokens = LocalCalcHubTokens.current
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = tokens.surfaceLow,
        border = BorderStroke(1.dp, tokens.cardBorder)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = tokens.inkSecondary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontFeatureSettings = "tnum"
                ),
                color = tokens.accentPrimary
            )
        }
    }
}

@Composable
private fun ToolsCompareSection(
    onCalculatorClick: (String) -> Unit
) {
    val tokens = LocalCalcHubTokens.current
    val inrFormat = remember {
        NumberFormat.getCurrencyInstance(Locale.Builder().setLanguage("en").setRegion("IN").build()).apply {
            maximumFractionDigits = 0
        }
    }

    var monthlySurplus by rememberSaveable { mutableDoubleStateOf(15_000.0) }
    var horizonYears by rememberSaveable { mutableDoubleStateOf(15.0) }
    var sipReturnRate by rememberSaveable { mutableDoubleStateOf(12.0) }
    var loanInterestRate by rememberSaveable { mutableDoubleStateOf(8.5) }

    val (investedTotal, sipGain, sipFinal) = remember(monthlySurplus, sipReturnRate, horizonYears) {
        CalculatorLogic.calculateSIP(monthlySurplus, sipReturnRate, horizonYears)
    }
    val (_, loanInterestSaved, loanTotalAvoided) = remember(monthlySurplus, loanInterestRate, horizonYears) {
        CalculatorLogic.calculateSIP(monthlySurplus, loanInterestRate, horizonYears)
    }

    val delta = sipFinal - loanTotalAvoided
    val sipWins = delta >= 0.0

    LazyColumn(
        contentPadding = PaddingValues(bottom = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item(key = "compare_verdict") {
            NeonCard(useGradientBorder = true) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SIP vs Home Loan Prepay",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = tokens.inkPrimary
                    )
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = tokens.gainBg
                    ) {
                        Text(
                            text = if (sipWins) "SIP leads" else "Prepay leads",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = tokens.gainInk,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (sipWins) {
                        "Investing beats prepaying by ${formatCompactINR(delta, inrFormat)}"
                    } else {
                        "Prepaying loan beats SIP by ${formatCompactINR(-delta, inrFormat)}"
                    },
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontFeatureSettings = "tnum"
                    ),
                    color = tokens.inkPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        color = tokens.surfaceLow,
                        border = BorderStroke(1.dp, tokens.cardBorder)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Invest via SIP", style = MaterialTheme.typography.labelMedium, color = tokens.inkSecondary)
                            Text(
                                text = formatCompactINR(sipFinal, inrFormat),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFeatureSettings = "tnum"),
                                color = tokens.gainInk
                            )
                            Text(
                                text = "+${formatCompactINR(sipGain, inrFormat)} gain",
                                style = MaterialTheme.typography.labelSmall,
                                color = tokens.inkMuted
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        color = tokens.surfaceLow,
                        border = BorderStroke(1.dp, tokens.cardBorder)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Prepay Home Loan", style = MaterialTheme.typography.labelMedium, color = tokens.inkSecondary)
                            Text(
                                text = formatCompactINR(loanTotalAvoided, inrFormat),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFeatureSettings = "tnum"),
                                color = tokens.accentPrimary
                            )
                            Text(
                                text = "${formatCompactINR(loanInterestSaved, inrFormat)} int. saved",
                                style = MaterialTheme.typography.labelSmall,
                                color = tokens.inkMuted
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                ResultRow("Total Principal Contributed", formatCompactINR(investedTotal, inrFormat))
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onCalculatorClick("sip") },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = tokens.accentPrimary,
                            contentColor = tokens.onAccent
                        ),
                        shape = RoundedCornerShape(tokens.pillCornerRadius)
                    ) {
                        Text("Open SIP")
                    }
                    OutlinedButton(
                        onClick = { onCalculatorClick("home_loan_emi") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(tokens.pillCornerRadius)
                    ) {
                        Text("Home Loan", color = tokens.inkPrimary)
                    }
                }
            }
        }

        item(key = "compare_inputs") {
            NeonCard {
                CalculatorInput(
                    label = "Monthly Surplus",
                    value = monthlySurplus,
                    onValueChange = { monthlySurplus = it },
                    range = 2_000.0..100_000.0,
                    symbol = "₹"
                )
                Spacer(modifier = Modifier.height(12.dp))
                CalculatorInput(
                    label = "Horizon",
                    value = horizonYears,
                    onValueChange = { horizonYears = it },
                    range = 5.0..25.0,
                    symbol = "Yr"
                )
                Spacer(modifier = Modifier.height(12.dp))
                CalculatorInput(
                    label = "SIP Return Rate",
                    value = sipReturnRate,
                    onValueChange = { sipReturnRate = it },
                    range = 8.0..18.0,
                    symbol = "%"
                )
                Spacer(modifier = Modifier.height(12.dp))
                CalculatorInput(
                    label = "Home Loan Rate",
                    value = loanInterestRate,
                    onValueChange = { loanInterestRate = it },
                    range = 7.0..12.0,
                    symbol = "%"
                )
            }
        }
    }
}

@Composable
private fun ToolsVaultSection(
    onCalculatorClick: (String) -> Unit
) {
    val appContainer = LocalAppContainer.current
    val userDataRepository = appContainer.userDataRepository
    val tokens = LocalCalcHubTokens.current

    val scenarios by userDataRepository.savedScenarios.collectAsStateWithLifecycle()

    LazyColumn(
        contentPadding = PaddingValues(bottom = 18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item(key = "vault_header") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Saved Scenarios (${scenarios.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = tokens.inkPrimary
                )
                TextButton(
                    onClick = { userDataRepository.resetDefaultScenarios() }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Refresh,
                        contentDescription = null,
                        tint = tokens.accentPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reset Presets", color = tokens.accentPrimary)
                }
            }
        }

        if (scenarios.isEmpty()) {
            item(key = "vault_empty") {
                NeonCard {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Your scenario vault is empty",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = tokens.inkPrimary
                        )
                        Text(
                            text = "Tap the Save bookmark inside any calculator to snapshot scenarios offline.",
                            style = MaterialTheme.typography.bodySmall,
                            color = tokens.inkSecondary
                        )
                        Button(
                            onClick = { userDataRepository.resetDefaultScenarios() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = tokens.accentPrimary,
                                contentColor = tokens.onAccent
                            ),
                            shape = RoundedCornerShape(tokens.pillCornerRadius)
                        ) {
                            Text("Restore 4 Default Scenarios")
                        }
                    }
                }
            }
        } else {
            items(scenarios, key = { it.id }) { scenario ->
                VaultScenarioCard(
                    scenario = scenario,
                    onOpen = { onCalculatorClick(scenario.route) },
                    onDelete = { userDataRepository.removeScenario(scenario.id) }
                )
            }
        }
    }
}

@Composable
private fun VaultScenarioCard(
    scenario: SavedScenario,
    onOpen: () -> Unit,
    onDelete: () -> Unit
) {
    val tokens = LocalCalcHubTokens.current
    val cat = remember(scenario.route) { categoryForRoute(scenario.route) }
    val palette = remember(cat) { CategoryPalettes.forCategory(cat) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(tokens.cardCornerRadius)),
        shape = RoundedCornerShape(tokens.cardCornerRadius),
        color = tokens.surfaceLowest,
        border = BorderStroke(1.dp, tokens.cardBorder),
        onClick = onOpen
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(palette.badgeGradStart, palette.badgeGradEnd)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = cat.label().take(2).uppercase(),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = scenario.title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = tokens.inkPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = scenario.summary,
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.inkSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = tokens.surfaceLow
            ) {
                Text(
                    text = scenario.resultFormatted,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontFeatureSettings = "tnum"
                    ),
                    color = tokens.accentPrimary,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.DeleteOutline,
                    contentDescription = "Delete ${scenario.title}",
                    tint = tokens.inkMuted,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun ToolsFormulasSection(
    onCalculatorClick: (String) -> Unit
) {
    val tokens = LocalCalcHubTokens.current
    var formulaSearch by rememberSaveable { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf<CalculatorCategory?>(null) }

    val categories = remember {
        listOf<CalculatorCategory?>(null) + CalculatorCategory.entries.filter { it != CalculatorCategory.POPULAR }
    }

    val filtered = remember(formulaSearch, selectedCategory) {
        allCalculators.filter { calc ->
            val matchesCat = selectedCategory == null || calc.category == selectedCategory
            val q = formulaSearch.trim()
            val matchesQ = q.isEmpty() ||
                calc.name.contains(q, ignoreCase = true) ||
                calc.formula.contains(q, ignoreCase = true) ||
                calc.subtitle.contains(q, ignoreCase = true)
            matchesCat && matchesQ
        }
    }

    LazyColumn(
        contentPadding = PaddingValues(bottom = 18.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item(key = "formula_search") {
            NeonSearch(
                query = formulaSearch,
                onQueryChange = { formulaSearch = it },
                placeholder = "Search 30+ financial formulas (FV, EMI, XIRR...)"
            )
        }

        item(key = "formula_cats") {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { cat ->
                    val selected = selectedCategory == cat
                    FilterChip(
                        selected = selected,
                        onClick = { selectedCategory = cat },
                        label = {
                            Text(
                                text = cat?.label() ?: "All 30+",
                                style = MaterialTheme.typography.labelMedium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = tokens.accentContainer,
                            selectedLabelColor = tokens.onAccentContainer
                        )
                    )
                }
            }
        }

        items(filtered, key = { it.route }) { calc ->
            val catPalette = remember(calc.category) { CategoryPalettes.forCategory(calc.category) }
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(tokens.cardCornerRadius)),
                shape = RoundedCornerShape(tokens.cardCornerRadius),
                color = tokens.surfaceLowest,
                border = BorderStroke(1.dp, tokens.cardBorder),
                onClick = { onCalculatorClick(calc.route) }
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        Brush.linearGradient(
                                            listOf(catPalette.badgeGradStart, catPalette.badgeGradEnd)
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = calc.icon,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = calc.name,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = tokens.inkPrimary
                                )
                                Text(
                                    text = calc.category.label(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = tokens.inkMuted
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Try",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = tokens.accentPrimary
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                                contentDescription = null,
                                tint = tokens.accentPrimary,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = tokens.surfaceLow
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 7.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = calc.formula.ifBlank { calc.subtitle },
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = tokens.inkPrimary,
                                modifier = Modifier.weight(1f),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (calc.sampleValue.isNotBlank()) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = calc.sampleValue,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontFeatureSettings = "tnum"
                                    ),
                                    color = tokens.gainInk
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatIndianWords(amount: Double): String {
    if (!amount.isFinite() || amount <= 0.0) return "Zero Rupees"
    val crores = (amount / 10_000_000.0).toInt()
    val remAfterCr = amount - crores * 10_000_000.0
    val lakhs = (remAfterCr / 100_000.0).toInt()
    val remAfterLakh = remAfterCr - lakhs * 100_000.0
    val thousands = (remAfterLakh / 1_000.0).toInt()
    val units = (remAfterLakh - thousands * 1_000.0).toInt()

    val parts = buildList {
        if (crores > 0) add("$crores Crore")
        if (lakhs > 0) add("$lakhs Lakh")
        if (thousands > 0) add("$thousands Thousand")
        if (units > 0 && isEmpty()) add("$units")
    }
    return parts.joinToString(" ") + " Rupees"
}
