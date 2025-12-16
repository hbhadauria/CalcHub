package com.hsb.calchub.ui.screens.gratuity

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
fun GratuityCalculatorScreen(onBackClick: () -> Unit) {
    var lastSalary by remember { mutableDoubleStateOf(50000.0) }
    var yearsOfService by remember { mutableDoubleStateOf(10.0) }
    val results = CalculatorLogic.calculateGratuity(lastSalary, yearsOfService)
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN"))

    CalculatorScaffold(
        title = "Gratuity Calculator",
        onBackClick = onBackClick,
        calculatorId = "gratuity"
    ) { innerPadding ->
        Column(Modifier.fillMaxSize().padding(innerPadding).padding(16.dp).verticalScroll(rememberScrollState())) {
            CalculatorInput("Last Drawn Salary", lastSalary, { lastSalary = it }, 10000.0..500000.0, "₹")
            CalculatorInput("Years of Service", yearsOfService, { yearsOfService = it }, 1.0..40.0, "Yr")
            
            NeonCard(Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
                Column(Modifier.padding(16.dp)) {
                    ResultRow("Total Gratuity", currencyFormat.format(results.first))
                    ResultRow("Tax Free Amount", currencyFormat.format(results.second))
                    ResultRow("Taxable Amount", currencyFormat.format(results.third), true)
                }
            }
        }
    }
}
