package com.hsb.calchub.ui.screens.gst

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hsb.calchub.domain.logic.CalculatorLogic
import com.hsb.calchub.ui.components.*
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GstCalculatorScreen(onBackClick: () -> Unit) {
    var amount by remember { mutableDoubleStateOf(10000.0) }
    var gstRate by remember { mutableDoubleStateOf(18.0) }
    var isInclusive by remember { mutableStateOf(false) }
    val results = CalculatorLogic.calculateGST(amount, gstRate, isInclusive)
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    CalculatorScaffold(
        title = "GST Calculator",
        onBackClick = onBackClick,
        calculatorId = "gst"
    ) { innerPadding ->
        Column(Modifier.fillMaxSize().padding(innerPadding).padding(16.dp).verticalScroll(rememberScrollState())) {
            CalculatorInput("Amount", amount, { amount = it }, 100.0..10000000.0, "₹")
            CalculatorInput("GST Rate", gstRate, { gstRate = it }, 0.0..28.0, "%")
            
            Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Checkbox(isInclusive, { isInclusive = it })
                Text("GST Inclusive")
            }
            
            NeonCard(Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
                Column(Modifier.padding(16.dp)) {
                    ResultRow("Base Amount", currencyFormat.format(results.first))
                    ResultRow("GST Amount", currencyFormat.format(results.second))
                    ResultRow("Total Amount", currencyFormat.format(results.third), true)
                }
            }
        }
    }
}
