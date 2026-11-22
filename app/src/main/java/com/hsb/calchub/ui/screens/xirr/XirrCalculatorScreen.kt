package com.hsb.calchub.ui.screens.xirr

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.hsb.calchub.ui.components.NeonCard
import com.hsb.calchub.ui.components.ResultRow
import com.hsb.calchub.ui.components.rememberInrFormat
import com.hsb.calchub.ui.theme.LocalCalcHubTokens
import java.util.Locale

/**
 * Screen for Extended Internal Rate of Return (XIRR) calculation.
 */
@Composable
fun XirrCalculatorScreen(onBackClick: () -> Unit) {
    var investment1 by rememberSaveable { mutableDoubleStateOf(10000.0) }
    var investment2 by rememberSaveable { mutableDoubleStateOf(10000.0) }
    var investment3 by rememberSaveable { mutableDoubleStateOf(10000.0) }
    var returns by rememberSaveable { mutableDoubleStateOf(35000.0) }
    val results = remember(investment1, investment2, investment3, returns) {
        CalculatorLogic.calculateXIRR(listOf(investment1, investment2, investment3), returns)
    }
    val currencyFormat = rememberInrFormat(maxFractionDigits = 2)

    CalculatorScrollScaffold(
        title = "XIRR Calculator",
        onBackClick = onBackClick,
        calculatorId = "xirr"
    ) {
        val tokens = LocalCalcHubTokens.current
        NeonCard(modifier = Modifier.fillMaxWidth(), useGradientBorder = true) {
            ResultRow("Total Invested", currencyFormat.format(results.first))
            ResultRow("Profit", currencyFormat.format(results.second))
            ResultRow("Return %", String.format(Locale.US, "%.2f%%", results.third), isTotal = true)
        }

        NeonCard {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Cashflows",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = tokens.accentPrimary
                )
                CalculatorInput("Investment 1", investment1, { investment1 = it }, 1000.0..1000000.0, "₹")
                CalculatorInput("Investment 2", investment2, { investment2 = it }, 1000.0..1000000.0, "₹")
                CalculatorInput("Investment 3", investment3, { investment3 = it }, 1000.0..1000000.0, "₹")
                CalculatorInput("Current Value", returns, { returns = it }, 1000.0..5000000.0, "₹")
            }
        }
    }
}
