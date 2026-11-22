package com.hsb.calchub.ui.screens.stock_average

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.hsb.calchub.ui.components.NeonCard
import com.hsb.calchub.ui.components.ResultRow
import com.hsb.calchub.ui.components.rememberInrFormat
import com.hsb.calchub.ui.theme.LocalCalcHubTokens
import java.util.Locale

/**
 * Screen for Stock Average calculation.
 */
@Composable
fun StockAverageCalculatorScreen(onBackClick: () -> Unit) {
    var firstQuantity by rememberSaveable { mutableDoubleStateOf(100.0) }
    var firstPrice by rememberSaveable { mutableDoubleStateOf(150.0) }
    var secondQuantity by rememberSaveable { mutableDoubleStateOf(50.0) }
    var secondPrice by rememberSaveable { mutableDoubleStateOf(120.0) }
    val results = remember(firstQuantity, firstPrice, secondQuantity, secondPrice) {
        CalculatorLogic.calculateStockAverage(firstQuantity, firstPrice, secondQuantity, secondPrice)
    }
    val currencyFormat = rememberInrFormat(maxFractionDigits = 2)

    CalculatorScrollScaffold(
        title = "Stock Average Calculator",
        onBackClick = onBackClick,
        calculatorId = "stock_average"
    ) {
        val tokens = LocalCalcHubTokens.current
        NeonCard(modifier = Modifier.fillMaxWidth(), useGradientBorder = true) {
            ResultRow("Total Quantity", String.format(Locale.US, "%.0f shares", results.first))
            ResultRow("Average Price", currencyFormat.format(results.second), isTotal = true)
            ResultRow("Total Investment", currencyFormat.format(results.third))
        }

        NeonCard {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "First Purchase",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = tokens.accentPrimary
                )
                CalculatorInput("Quantity", firstQuantity, { firstQuantity = it }, 1.0..10000.0, "Qty")
                CalculatorInput("Price per Share", firstPrice, { firstPrice = it }, 1.0..100000.0, "₹")

                HorizontalDivider(color = tokens.cardBorder)

                Text(
                    text = "Second Purchase",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = tokens.accentPrimary
                )
                CalculatorInput("Quantity", secondQuantity, { secondQuantity = it }, 1.0..10000.0, "Qty")
                CalculatorInput("Price per Share", secondPrice, { secondPrice = it }, 1.0..100000.0, "₹")
            }
        }
    }
}
