package com.hsb.calchub.ui.screens.margin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hsb.calchub.domain.logic.CalculatorLogic
import com.hsb.calchub.ui.components.*
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarginCalculatorScreen(onBackClick: () -> Unit) {
    var quantity by remember { mutableDoubleStateOf(100.0) }
    var price by remember { mutableDoubleStateOf(500.0) }
    var leverage by remember { mutableDoubleStateOf(5.0) }
    val results = CalculatorLogic.calculateMargin(quantity, price, leverage)
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN"))

    CalculatorScaffold(
        title = "Margin Calculator",
        onBackClick = onBackClick,
        calculatorId = "margin"
    ) { innerPadding ->
        Column(Modifier.fillMaxSize().padding(innerPadding).padding(16.dp).verticalScroll(rememberScrollState())) {
            CalculatorInput("Quantity", quantity, { quantity = it }, 1.0..10000.0, "Qty")
            CalculatorInput("Price per Share", price, { price = it }, 1.0..100000.0, "₹")
            CalculatorInput("Leverage", leverage, { leverage = it }, 1.0..20.0, "x")
            
            NeonCard(Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
                Column(Modifier.padding(16.dp)) {
                    ResultRow("Margin Required", currencyFormat.format(results.first))
                    ResultRow("Exposure", currencyFormat.format(results.second))
                    ResultRow("Total Value", currencyFormat.format(results.third), true)
                }
            }
        }
    }
}
