package com.hsb.calchub.ui.screens.apy

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
fun ApyCalculatorScreen(onBackClick: () -> Unit) {
    var currentAge by remember { mutableDoubleStateOf(25.0) }
    var pensionAmount by remember { mutableDoubleStateOf(5000.0) }
    val results = CalculatorLogic.calculateAPY(currentAge, pensionAmount)
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("APY Calculator") },
                navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.Default.ArrowBack, null) } },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(Modifier.fillMaxSize().padding(innerPadding).padding(16.dp).verticalScroll(rememberScrollState())) {
            CalculatorInput("Current Age", currentAge, { currentAge = it }, 18.0..40.0, "Yr")
            CalculatorInput("Desired Pension", pensionAmount, { pensionAmount = it }, 1000.0..5000.0, "₹")
            
            Card(Modifier.fillMaxWidth().padding(vertical = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                Column(Modifier.padding(16.dp)) {
                    ResultRow("Monthly Contribution", currencyFormat.format(results.first))
                    ResultRow("Total Contribution", currencyFormat.format(results.second))
                    ResultRow("Monthly Pension at 60", currencyFormat.format(results.third), true)
                }
            }
        }
    }
}
