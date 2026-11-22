package com.hsb.calchub.ui.screens.post_office_mis

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
import com.hsb.calchub.ui.components.NeonCard
import com.hsb.calchub.ui.components.ResultRow
import com.hsb.calchub.ui.components.rememberInrFormat

/**
 * Screen for Post Office Monthly Income Scheme (MIS) calculation.
 */
@Composable
fun PostOfficeMisCalculatorScreen(onBackClick: () -> Unit) {
    var investment by rememberSaveable { mutableDoubleStateOf(450000.0) }
    var interestRate by rememberSaveable { mutableDoubleStateOf(7.4) }
    val results = remember(investment, interestRate) {
        CalculatorLogic.calculatePostOfficeMIS(investment, interestRate)
    }
    val currencyFormat = rememberInrFormat(maxFractionDigits = 2)

    CalculatorScrollScaffold(
        title = "Post Office MIS",
        onBackClick = onBackClick,
        calculatorId = "post_office_mis"
    ) {
        NeonCard(modifier = Modifier.fillMaxWidth(), useGradientBorder = true) {
            ResultRow("Investment", currencyFormat.format(results.first))
            ResultRow("Monthly Income", currencyFormat.format(results.second), isTotal = true)
            ResultRow("Total Income (5 Yr)", currencyFormat.format(results.third))
        }

        NeonCard {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                CalculatorInput("Investment Amount", investment, { investment = it }, 1000.0..900000.0, "₹")
                CalculatorInput("Interest Rate", interestRate, { interestRate = it }, 1.0..15.0, "%")
            }
        }
    }
}
