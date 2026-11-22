package com.hsb.calchub.ui.screens.capital_gains

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hsb.calchub.domain.logic.CalculatorLogic
import com.hsb.calchub.ui.components.CalculatorInput
import com.hsb.calchub.ui.components.CalculatorScrollScaffold
import com.hsb.calchub.ui.components.FinanceTermTooltip
import com.hsb.calchub.ui.components.InvestedReturnsDonutCard
import com.hsb.calchub.ui.components.NeonCard
import com.hsb.calchub.ui.components.ResultRow
import com.hsb.calchub.ui.components.SegmentedPillGroup
import com.hsb.calchub.ui.components.rememberInrFormat
import com.hsb.calchub.ui.theme.LocalCalcHubTokens
import java.util.Locale
import kotlin.math.roundToInt

private enum class AssetTypeOption(val label: String, val isEquity: Boolean) {
    EQUITY("Equity / MF", true),
    OTHER("Debt / Property", false)
}

/**
 * Screen for FY 2025-26 Short-Term and Long-Term Capital Gains Tax calculation.
 */
@Composable
fun CapitalGainsCalculatorScreen(onBackClick: () -> Unit) {
    var assetType by rememberSaveable { mutableStateOf(AssetTypeOption.EQUITY) }
    var purchaseValue by rememberSaveable { mutableDoubleStateOf(300_000.0) }
    var saleValue by rememberSaveable { mutableDoubleStateOf(550_000.0) }
    var holdingMonths by rememberSaveable { mutableDoubleStateOf(18.0) }

    val result = remember(purchaseValue, saleValue, holdingMonths, assetType) {
        CalculatorLogic.calculateCapitalGains(
            purchaseValue = purchaseValue,
            saleValue = saleValue,
            holdingMonths = holdingMonths.roundToInt(),
            isEquity = assetType.isEquity
        )
    }
    val currencyFormat = rememberInrFormat()

    val shareSummary = remember(assetType, purchaseValue, saleValue, holdingMonths, result) {
        val termLabel = if (result.isLongTerm) "Long-Term (LTCG)" else "Short-Term (STCG)"
        buildString {
            append("Capital Gains Tax (${assetType.label}) — CalcHub\n")
            append("$termLabel · ${holdingMonths.roundToInt()} months\n")
            append("Capital Gain: ${currencyFormat.format(result.capitalGain)}\n")
            append("Tax Payable: ${currencyFormat.format(result.totalTax)}\n")
            append("Net Post-Tax Proceeds: ${currencyFormat.format(result.netProceeds)}")
        }
    }

    CalculatorScrollScaffold(
        title = "Capital Gains Tax",
        onBackClick = onBackClick,
        calculatorId = "capital_gains",
        shareSummary = shareSummary
    ) {
        val tokens = LocalCalcHubTokens.current
        SegmentedPillGroup(
            options = AssetTypeOption.entries,
            selectedOption = assetType,
            onOptionSelected = { assetType = it },
            labelProvider = { it.label },
            modifier = Modifier.fillMaxWidth()
        )

        NeonCard(modifier = Modifier.fillMaxWidth(), useGradientBorder = true) {
            val termLabel = if (result.isLongTerm) "Long-Term (LTCG)" else "Short-Term (STCG)"
            Text(
                text = "$termLabel · ${String.format(Locale.US, "%.1f", result.baseRatePercent)}% + 4% cess",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = tokens.accentPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            ResultRow("Capital Gain / Loss", currencyFormat.format(result.capitalGain))
            ResultRow("Taxable Gain", currencyFormat.format(result.taxableGain))
            ResultRow("Tax Payable", currencyFormat.format(result.totalTax), isTotal = true)
            HorizontalDivider(color = tokens.cardBorder, modifier = Modifier.padding(vertical = 6.dp))
            ResultRow("Net Post-Tax Proceeds", currencyFormat.format(result.netProceeds))
        }

        NeonCard {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                CalculatorInput(
                    label = "Purchase Value",
                    value = purchaseValue,
                    onValueChange = { purchaseValue = it },
                    range = 1000.0..10_000_000.0,
                    symbol = "₹"
                )
                CalculatorInput(
                    label = "Sale Value",
                    value = saleValue,
                    onValueChange = { saleValue = it },
                    range = 1000.0..20_000_000.0,
                    symbol = "₹"
                )
                CalculatorInput(
                    label = "Holding Period",
                    value = holdingMonths,
                    onValueChange = { holdingMonths = it },
                    range = 1.0..120.0,
                    symbol = "Mo"
                )
                FinanceTermTooltip(
                    term = "LTCG ₹1.25L Exemption",
                    explanation = "Long-term equity gains up to ₹1.25 lakh per financial year are tax-free; gains above that are taxed at 12.5%."
                )
            }
        }

        if (result.capitalGain > 0.0) {
            InvestedReturnsDonutCard(
                invested = (result.capitalGain - result.totalTax).coerceAtLeast(0.0),
                returns = result.totalTax,
                investedLabel = "Net Gain",
                returnsLabel = "Tax",
                centerLabelText = "Gain Split",
                useGainLossColors = true
            )
        }
    }
}
