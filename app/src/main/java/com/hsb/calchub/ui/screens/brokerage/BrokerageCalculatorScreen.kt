package com.hsb.calchub.ui.screens.brokerage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hsb.calchub.domain.logic.CalculatorLogic
import com.hsb.calchub.ui.components.CalculatorInput
import com.hsb.calchub.ui.components.CalculatorScrollScaffold
import com.hsb.calchub.ui.components.NeonCard
import com.hsb.calchub.ui.components.ResultRow
import com.hsb.calchub.ui.components.ThemedCheckboxRow
import com.hsb.calchub.ui.components.rememberInrFormat

/**
 * Screen for calculating Stock Brokerage charges.
 */
@Composable
fun BrokerageCalculatorScreen(onBackClick: () -> Unit) {
    var tradeValue by rememberSaveable { mutableDoubleStateOf(100000.0) }
    var brokerageRate by rememberSaveable { mutableDoubleStateOf(0.03) }
    var isIntraday by rememberSaveable { mutableStateOf(false) }
    val results = remember(tradeValue, brokerageRate, isIntraday) {
        CalculatorLogic.calculateBrokerage(tradeValue, brokerageRate, isIntraday)
    }
    val currencyFormat = rememberInrFormat(maxFractionDigits = 2)

    CalculatorScrollScaffold(
        title = "Brokerage Calculator",
        onBackClick = onBackClick,
        calculatorId = "brokerage"
    ) {
        NeonCard(modifier = Modifier.fillMaxWidth(), useGradientBorder = true) {
            ResultRow("Trade Value", currencyFormat.format(results.first))
            ResultRow("Total Charges", currencyFormat.format(results.second))
            ResultRow("Net Amount", currencyFormat.format(results.third), isTotal = true)
        }

        NeonCard {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                CalculatorInput("Trade Value", tradeValue, { tradeValue = it }, 1000.0..10000000.0, "₹")
                CalculatorInput("Brokerage Rate", brokerageRate, { brokerageRate = it }, 0.01..1.0, "%")
                ThemedCheckboxRow(
                    label = "Intraday Trade",
                    checked = isIntraday,
                    onCheckedChange = { isIntraday = it }
                )
            }
        }
    }
}
