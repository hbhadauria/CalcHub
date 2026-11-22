package com.hsb.calchub.ui.screens.buy_vs_rent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
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
import com.hsb.calchub.ui.components.StarterPreset
import com.hsb.calchub.ui.components.StarterPresetChips
import com.hsb.calchub.ui.components.formatCompactINR
import com.hsb.calchub.ui.components.rememberInrFormat
import com.hsb.calchub.ui.theme.LocalCalcHubTokens
import kotlin.math.roundToInt

/**
 * Screen comparing home purchase wealth against renting + investing down payment and EMI surplus in a SIP.
 */
@Composable
fun BuyVsRentCalculatorScreen(onBackClick: () -> Unit) {
    var propertyPrice by rememberSaveable { mutableDoubleStateOf(10_000_000.0) }
    var downPaymentPct by rememberSaveable { mutableDoubleStateOf(20.0) }
    var loanRatePct by rememberSaveable { mutableDoubleStateOf(8.5) }
    var tenureYears by rememberSaveable { mutableDoubleStateOf(20.0) }
    var monthlyRent by rememberSaveable { mutableDoubleStateOf(30_000.0) }
    var rentEscalationPct by rememberSaveable { mutableDoubleStateOf(6.0) }
    var propertyAppreciationPct by rememberSaveable { mutableDoubleStateOf(6.0) }
    var sipReturnPct by rememberSaveable { mutableDoubleStateOf(12.0) }

    val result = remember(
        propertyPrice,
        downPaymentPct,
        loanRatePct,
        tenureYears,
        monthlyRent,
        rentEscalationPct,
        propertyAppreciationPct,
        sipReturnPct
    ) {
        CalculatorLogic.calculateBuyVsRent(
            propertyPrice = propertyPrice,
            downPaymentPct = downPaymentPct,
            loanRatePct = loanRatePct,
            tenureYears = tenureYears.roundToInt(),
            monthlyRent = monthlyRent,
            rentEscalationPct = rentEscalationPct,
            propertyAppreciationPct = propertyAppreciationPct,
            sipReturnPct = sipReturnPct
        )
    }

    val currencyFormat = rememberInrFormat()

    val verdictText = if (result.isBuyingBetter) {
        "Buying leads by ${formatCompactINR(result.wealthDifference, currencyFormat)}"
    } else {
        "Rent + SIP leads by ${formatCompactINR(result.wealthDifference, currencyFormat)}"
    }

    val shareSummary = remember(propertyPrice, tenureYears, monthlyRent, result, verdictText) {
        buildString {
            append("Buy vs. Rent Analysis — CalcHub\n")
            append("Home Price: ${formatCompactINR(propertyPrice, currencyFormat)} vs Rent: ${currencyFormat.format(monthlyRent)}/mo (${tenureYears.roundToInt()} yrs)\n")
            append("Verdict: $verdictText\n")
            append("Buy Equity: ${formatCompactINR(result.buyNetWealth, currencyFormat)}\n")
            append("Rent + SIP Portfolio: ${formatCompactINR(result.rentInvestNetWealth, currencyFormat)}")
        }
    }

    CalculatorScrollScaffold(
        title = "Buy vs. Rent",
        onBackClick = onBackClick,
        calculatorId = "buy_vs_rent",
        shareSummary = shareSummary
    ) {
        val tokens = LocalCalcHubTokens.current
        NeonCard(modifier = Modifier.fillMaxWidth(), useGradientBorder = true) {
            Text(
                text = verdictText,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = tokens.accentPrimary
            )
            HorizontalDivider(color = tokens.cardBorder, modifier = Modifier.padding(vertical = 6.dp))
            ResultRow("Buy: Future Home Equity", formatCompactINR(result.buyNetWealth, currencyFormat), isTotal = result.isBuyingBetter)
            ResultRow("Rent + SIP: Portfolio", formatCompactINR(result.rentInvestNetWealth, currencyFormat), isTotal = !result.isBuyingBetter)
            ResultRow("Home Loan Monthly EMI", currencyFormat.format(result.monthlyEmi))
            ResultRow("Total Rent Paid", formatCompactINR(result.totalRentPaid, currencyFormat))
        }

        StarterPresetChips(
            presets = listOf(
                StarterPreset("₹60L 2BHK") {
                    propertyPrice = 6_000_000.0
                    monthlyRent = 20_000.0
                },
                StarterPreset("₹1.2Cr Metro") {
                    propertyPrice = 12_000_000.0
                    monthlyRent = 38_000.0
                },
                StarterPreset("₹2Cr Upgrade") {
                    propertyPrice = 20_000_000.0
                    monthlyRent = 60_000.0
                }
            )
        )

        NeonCard {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                CalculatorInput("Property Price", propertyPrice, { propertyPrice = it }, 2_000_000.0..50_000_000.0, "₹")
                CalculatorInput("Down Payment %", downPaymentPct, { downPaymentPct = it }, 10.0..50.0, "%")
                CalculatorInput("Loan Interest Rate", loanRatePct, { loanRatePct = it }, 6.0..15.0, "%")
                CalculatorInput("Loan / Horizon Tenure", tenureYears, { tenureYears = it }, 5.0..30.0, "Yr")
                CalculatorInput("Starting Monthly Rent", monthlyRent, { monthlyRent = it }, 5_000.0..250_000.0, "₹")
                CalculatorInput("Annual Rent Hike", rentEscalationPct, { rentEscalationPct = it }, 0.0..15.0, "%")
                CalculatorInput("Home Appreciation (p.a)", propertyAppreciationPct, { propertyAppreciationPct = it }, 0.0..15.0, "%")
                CalculatorInput("SIP Return (p.a)", sipReturnPct, { sipReturnPct = it }, 4.0..20.0, "%")
                FinanceTermTooltip(
                    term = "Opportunity Cost",
                    explanation = "Compares your future home equity against investing the down payment plus any monthly EMI-minus-rent savings in a SIP."
                )
            }
        }

        InvestedReturnsDonutCard(
            invested = result.buyNetWealth,
            returns = result.rentInvestNetWealth,
            investedLabel = "Buy Equity",
            returnsLabel = "Rent + SIP",
            centerLabelText = "Net Wealth"
        )
    }
}
