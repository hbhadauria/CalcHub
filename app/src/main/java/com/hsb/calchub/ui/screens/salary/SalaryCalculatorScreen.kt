package com.hsb.calchub.ui.screens.salary

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
fun SalaryCalculatorScreen(onBackClick: () -> Unit) {
    var basicSalary by remember { mutableDoubleStateOf(40000.0) }
    var hra by remember { mutableDoubleStateOf(16000.0) }
    var otherAllowances by remember { mutableDoubleStateOf(5000.0) }
    var pf by remember { mutableDoubleStateOf(4800.0) }
    val results = CalculatorLogic.calculateSalary(basicSalary, hra, otherAllowances, pf)
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    CalculatorScaffold(
        title = "Salary Calculator",
        onBackClick = onBackClick,
        calculatorId = "salary"
    ) { innerPadding ->
        Column(Modifier.fillMaxSize().padding(innerPadding).padding(16.dp).verticalScroll(rememberScrollState())) {
            CalculatorInput("Basic Salary", basicSalary, { basicSalary = it }, 10000.0..500000.0, "₹")
            CalculatorInput("HRA", hra, { hra = it }, 0.0..200000.0, "₹")
            CalculatorInput("Other Allowances", otherAllowances, { otherAllowances = it }, 0.0..100000.0, "₹")
            CalculatorInput("PF Deduction", pf, { pf = it }, 0.0..50000.0, "₹")
            
            DonutChart(listOf(
                DonutChartData(results.second, MaterialTheme.colorScheme.error, "Deductions"),
                DonutChartData(results.third, MaterialTheme.colorScheme.primary, "Take Home")
            ))
            
            NeonCard(Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
                Column(Modifier.padding(16.dp)) {
                    ResultRow("Gross Salary", currencyFormat.format(results.first))
                    ResultRow("Total Deductions", currencyFormat.format(results.second))
                    ResultRow("Net Salary", currencyFormat.format(results.third), true)
                }
            }
        }
    }
}
