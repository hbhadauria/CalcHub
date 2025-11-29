package com.hsb.calchub.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.width
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hsb.calchub.domain.model.Calculator
import com.hsb.calchub.domain.model.CalculatorCategory
import com.hsb.calchub.domain.model.allCalculators
import com.hsb.calchub.ui.components.NeoPopCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onCalculatorClick: (String) -> Unit) {
    val popularCalculators = allCalculators.filter { it.isPopular }
    val categorizedCalculators = allCalculators.groupBy { it.category }
    val categories = listOf(
        CalculatorCategory.INVESTMENT,
        CalculatorCategory.LOAN,
        CalculatorCategory.TAX,
        CalculatorCategory.RETIREMENT,
        CalculatorCategory.TRADING,
        CalculatorCategory.OTHER
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "CALCHUB",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    ) 
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(bottom = 16.dp), // Bottom padding only, others handled in items
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 1. Popular Section (Horizontal Carousel)
            item(span = { GridItemSpan(2) }) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    SectionHeader("POPULAR")
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(popularCalculators) { calculator ->
                            PopularCalculatorCard(calculator, onCalculatorClick)
                        }
                    }
                }
            }

            // 2. Categorized Sections
            categories.forEach { category ->
                val calculators = categorizedCalculators[category] ?: emptyList()
                if (calculators.isNotEmpty()) {
                    // Category Header
                    item(span = { GridItemSpan(2) }) {
                        SectionHeader(category.name)
                    }
                    
                    // Calculator Items
                    items(calculators) { calculator ->
                        // Add padding to items to simulate content padding of grid
                        // Since we can't easily mix padding in LazyVerticalGrid items directly without affecting spacing
                        // We rely on the grid's arrangement, but we need horizontal padding for the grid itself.
                        // However, we didn't set contentPadding horizontal in LazyVerticalGrid to allow full width headers.
                        // So we wrap card in a box with padding? No, better to set grid padding and use negative padding for headers?
                        // Or just set padding on the grid and let headers be padded too.
                        // Let's try setting padding on the grid and see if headers look okay.
                        // Actually, headers usually look better aligned with content.
                        
                        // Wait, I removed contentPadding from LazyVerticalGrid above to handle it here?
                        // Let's revert to standard padding and just have headers aligned.
                        
                        CalculatorCard(calculator, onCalculatorClick)
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            letterSpacing = 1.sp
        ),
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun PopularCalculatorCard(calculator: Calculator, onClick: (String) -> Unit) {
    com.hsb.calchub.ui.components.NeoPopCard(
        modifier = Modifier
            .width(160.dp)
            .aspectRatio(1.4f)
            .clickable { onClick(calculator.route) },
        backgroundColor = MaterialTheme.colorScheme.primaryContainer, // Highlight popular ones
        borderColor = MaterialTheme.colorScheme.primary
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = calculator.name.uppercase(),
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Black,
                    letterSpacing = 0.5.sp
                ),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun CalculatorCard(calculator: Calculator, onClick: (String) -> Unit) {
    // We need to handle the padding here if the grid doesn't have it, 
    // OR we update the grid to have padding and headers align with it.
    // Let's assume grid has padding (updated below).
    
    com.hsb.calchub.ui.components.NeoPopCard(
        modifier = Modifier
            .aspectRatio(1.2f)
            .clickable { onClick(calculator.route) },
        backgroundColor = MaterialTheme.colorScheme.surface,
        borderColor = MaterialTheme.colorScheme.outline
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = calculator.name.uppercase(),
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    letterSpacing = 0.5.sp
                ),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
