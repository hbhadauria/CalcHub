package com.hsb.calchub.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hsb.calchub.domain.model.Calculator
import com.hsb.calchub.domain.model.CalculatorCategory
import com.hsb.calchub.domain.model.allCalculators
import com.hsb.calchub.ui.components.NeonCard
import com.hsb.calchub.ui.components.NeonSearch
import com.hsb.calchub.ui.components.NeoPopGlossyCard
import com.hsb.calchub.ui.theme.NeonGreen
import com.hsb.calchub.ui.theme.NeonPink
import com.hsb.calchub.ui.theme.NeonText
import androidx.compose.ui.text.style.TextAlign

@Composable
fun HomeScreen(onCalculatorClick: (String) -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    
    // Filter calculators based on search
    val filteredCalculators = if (searchQuery.isEmpty()) {
        allCalculators
    } else {
        allCalculators.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    val popularCalculators = filteredCalculators.filter { it.isPopular }
    
    // If searching, show all results in one grid. If not, show sections.
    val isSearching = searchQuery.isNotEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text(
                text = "Calc",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = NeonGreen
                )
            )
            Text(
                text = "Hub",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = NeonPink
                )
            )
            Icon(
                imageVector = Icons.Default.Calculate,
                contentDescription = null,
                tint = NeonText,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(32.dp)
            )
        }

        // Search Bar
        NeonSearch(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            modifier = Modifier.padding(bottom = 24.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            if (!isSearching) {
                // Popular Section
                if (popularCalculators.isNotEmpty()) {
                    item(span = { GridItemSpan(2) }) {
                        Text(
                            text = "POPULAR",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = NeonGreen,
                                letterSpacing = 1.sp
                            ),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    items(popularCalculators) { calculator ->
                        PopularCalculatorCard(calculator, onCalculatorClick)
                    }
                }
                
                // Categories
                val categories = CalculatorCategory.values()
                categories.forEach { category ->
                    val calculators = allCalculators.filter { it.category == category && !it.isPopular }
                    if (calculators.isNotEmpty()) {
                        item(span = { GridItemSpan(2) }) {
                            Text(
                                text = category.name,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = NeonPink,
                                    letterSpacing = 1.sp
                                ),
                                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                            )
                        }
                        items(calculators) { calculator ->
                            CalculatorCard(calculator, onCalculatorClick)
                        }
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
            fontWeight = FontWeight.Bold,
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
    NeoPopGlossyCard(
        modifier = Modifier
            .width(160.dp)
            .aspectRatio(1.4f)
            .clickable { onClick(calculator.route) },
        backgroundColor = MaterialTheme.colorScheme.primaryContainer,
        borderColor = MaterialTheme.colorScheme.primary
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = calculator.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
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
    NeoPopGlossyCard(
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
            Icon(
                imageVector = calculator.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
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
