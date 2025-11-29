package com.hsb.calchub.ui.screens.emi

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hsb.calchub.domain.logic.CalculatorLogic
import com.hsb.calchub.ui.components.CalculatorInput
import com.hsb.calchub.ui.components.DonutChart
import com.hsb.calchub.ui.components.DonutChartData
import com.hsb.calchub.ui.components.ResultCard
import com.hsb.calchub.ui.components.ResultRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmiCalculatorScreen(onBackClick: () -> Unit) {
    var loanAmount by remember { mutableDoubleStateOf(1000000.0) }
    var interestRate by remember { mutableDoubleStateOf(8.5) }
    var tenureYears by remember { mutableDoubleStateOf(5.0) }

    val results = CalculatorLogic.calculateEMI(loanAmount, interestRate, tenureYears)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("EMI Calculator") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            CalculatorInput(
                label = "Loan Amount",
                value = loanAmount,
                onValueChange = { loanAmount = it },
                range = 10000.0..10000000.0,
                symbol = "₹"
            )

            CalculatorInput(
                label = "Rate of Interest (p.a)",
                value = interestRate,
                onValueChange = { interestRate = it },
                range = 1.0..30.0,
                symbol = "%"
            )

            CalculatorInput(
                label = "Loan Tenure",
                value = tenureYears,
                onValueChange = { tenureYears = it },
                range = 1.0..30.0,
                symbol = "Yr"
            )

            DonutChart(
                data = listOf(
                    DonutChartData(results.first * tenureYears * 12 - results.second, MaterialTheme.colorScheme.primary, "Principal"),
                    DonutChartData(results.second, MaterialTheme.colorScheme.tertiary, "Interest")
                )
            )

            // For EMI, the results are (EMI, Total Interest, Total Amount)
            // We can reuse ResultCard but labels might need adjustment.
            // Let's customize ResultCard or create a new one.
            // For now, let's just use ResultCard and map the values.
            // Invested -> Loan Amount (Not exactly, but close enough for display if we change labels)
            // Actually, let's create a specific card or just use Text rows here.
            // But to keep it consistent, let's use ResultCard but maybe we should make labels dynamic in ResultCard.
            // I'll update ResultCard to take labels.
            
            // Wait, ResultCard takes values. I can't change labels easily without modifying it.
            // Let's modify ResultCard to accept labels.
            
            // Or just create a local card here.
            
            ResultCardWithLabels(
                label1 = "Monthly EMI", value1 = results.first,
                label2 = "Total Interest", value2 = results.second,
                label3 = "Total Amount", value3 = results.third
            )
        }
    }
}

@Composable
fun ResultCardWithLabels(
    label1: String, value1: Double,
    label2: String, value2: Double,
    label3: String, value3: Double
) {
    val currencyFormat = java.text.NumberFormat.getCurrencyInstance(java.util.Locale("en", "IN"))
    
    androidx.compose.material3.Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            ResultRow(label1, currencyFormat.format(value1))
            ResultRow(label2, currencyFormat.format(value2))
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
            ResultRow(label3, currencyFormat.format(value3), isTotal = true)
        }
    }
}
