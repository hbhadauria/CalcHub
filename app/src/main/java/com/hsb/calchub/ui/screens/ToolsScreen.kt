package com.hsb.calchub.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hsb.calchub.domain.model.CalculatorCategory
import com.hsb.calchub.domain.model.allCalculators
import com.hsb.calchub.ui.components.NeonCard
import com.hsb.calchub.ui.theme.NeonGreen
import com.hsb.calchub.ui.theme.NeonPink
import com.hsb.calchub.ui.theme.NeonText

@Composable
fun ToolsScreen(onCalculatorClick: (String) -> Unit) {
    val categorizedCalculators = allCalculators.groupBy { it.category }
    val categories = CalculatorCategory.values()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Text(
                text = "TOOLS",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = NeonGreen
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        items(categories) { category ->
            val calculators = categorizedCalculators[category] ?: emptyList()
            if (calculators.isNotEmpty()) {
                Column {
                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = NeonPink,
                            letterSpacing = 1.sp
                        ),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    calculators.forEach { calculator ->
                        NeonCard(
                            onClick = { onCalculatorClick(calculator.route) },
                            modifier = Modifier.padding(bottom = 12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                calculator.icon?.let { icon ->
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = NeonGreen,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                }
                                Text(
                                    text = calculator.name,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Medium,
                                        color = NeonText
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
