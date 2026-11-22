package com.hsb.calchub.ui.screens.hra

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
 * Screen for calculating House Rent Allowance (HRA) exemption.
 */
@Composable
fun HraCalculatorScreen(onBackClick: () -> Unit) {
    var basicSalary by rememberSaveable { mutableDoubleStateOf(500000.0) }
    var hraReceived by rememberSaveable { mutableDoubleStateOf(200000.0) }
    var rentPaid by rememberSaveable { mutableDoubleStateOf(180000.0) }
    var isMetroCity by rememberSaveable { mutableStateOf(true) }

    val results = remember(basicSalary, hraReceived, rentPaid, isMetroCity) {
        CalculatorLogic.calculateHRA(basicSalary, hraReceived, rentPaid, isMetroCity)
    }
    val currencyFormat = rememberInrFormat(maxFractionDigits = 2)

    CalculatorScrollScaffold(
        title = "HRA Calculator",
        onBackClick = onBackClick,
        calculatorId = "hra"
    ) {
        NeonCard(modifier = Modifier.fillMaxWidth(), useGradientBorder = true) {
            ResultRow("HRA Received", currencyFormat.format(results.first))
            ResultRow("Exempted HRA", currencyFormat.format(results.second), isTotal = true)
            ResultRow("Taxable HRA", currencyFormat.format(results.third))
        }

        NeonCard {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                CalculatorInput("Basic Salary (Annual)", basicSalary, { basicSalary = it }, 100000.0..5000000.0, "₹")
                CalculatorInput("HRA Received (Annual)", hraReceived, { hraReceived = it }, 0.0..2000000.0, "₹")
                CalculatorInput("Rent Paid (Annual)", rentPaid, { rentPaid = it }, 0.0..2000000.0, "₹")
                ThemedCheckboxRow(
                    label = "Living in Metro City (50% rule)",
                    checked = isMetroCity,
                    onCheckedChange = { isMetroCity = it }
                )
            }
        }
    }
}
