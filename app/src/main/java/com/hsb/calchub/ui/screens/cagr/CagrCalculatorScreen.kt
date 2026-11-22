package com.hsb.calchub.ui.screens.cagr

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hsb.calchub.domain.logic.CalculatorLogic
import com.hsb.calchub.ui.components.CalculatorInput
import com.hsb.calchub.ui.components.CalculatorScrollScaffold
import com.hsb.calchub.ui.components.InvestedReturnsDonutCard
import com.hsb.calchub.ui.components.NeonCard
import com.hsb.calchub.ui.components.ResultRow
import com.hsb.calchub.ui.components.rememberInrFormat
import java.util.Locale

/**
 * Screen for calculating Compound Annual Growth Rate (CAGR).
 */
@Composable
fun CagrCalculatorScreen(onBackClick: () -> Unit) {
    var initialValue by rememberSaveable { mutableDoubleStateOf(100000.0) }
    var finalValue by rememberSaveable { mutableDoubleStateOf(200000.0) }
    var years by rememberSaveable { mutableDoubleStateOf(5.0) }
    val results = remember(initialValue, finalValue, years) {
        CalculatorLogic.calculateCAGR(initialValue, finalValue, years)
    }
    val currencyFormat = rememberInrFormat(maxFractionDigits = 2)

    CalculatorScrollScaffold(
        title = "CAGR Calculator",
        onBackClick = onBackClick,
        calculatorId = "cagr"
    ) {
        NeonCard(modifier = Modifier.fillMaxWidth(), useGradientBorder = true) {
            ResultRow("CAGR", String.format(Locale.US, "%.2f%%", results.first), isTotal = true)
            ResultRow("Total Return", String.format(Locale.US, "%.2f%%", results.second))
            ResultRow("Absolute Gain", currencyFormat.format(results.third))
        }

        NeonCard {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                CalculatorInput("Initial Value", initialValue, { initialValue = it }, 1000.0..10000000.0, "₹")
                CalculatorInput("Final Value", finalValue, { finalValue = it }, 1000.0..10000000.0, "₹")
                CalculatorInput("Time Period", years, { years = it }, 1.0..30.0, "Yr")
            }
        }

        InvestedReturnsDonutCard(
            invested = initialValue,
            returns = results.third,
            investedLabel = "Initial",
            returnsLabel = "Gain"
        )
    }
}
