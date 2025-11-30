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
            columns = GridCells.Fixed(3), // Changed to 3 columns
            contentPadding = PaddingValues(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            if (isSearching) {
                // Show all filtered results when searching
                items(filteredCalculators) { calculator ->
                    CalculatorCard(calculator, onCalculatorClick)
                }
            } else {
                // Show all calculators in grid (no sections)
                items(filteredCalculators) { calculator ->
                    CalculatorCard(calculator, onCalculatorClick)
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
    NeonCard(
        modifier = Modifier
            .width(160.dp)
            .aspectRatio(0.7f), // Vertical aspect ratio
        onClick = { onClick(calculator.route) },
        borderColor = NeonGreen
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = calculator.icon,
                contentDescription = null,
                tint = NeonGreen,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = calculator.name,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                ),
                textAlign = TextAlign.Center,
                color = NeonText
            )
        }
    }
}

@Composable
fun CalculatorCard(calculator: Calculator, onClick: (String) -> Unit) {
    NeonCard(
        modifier = Modifier
            .aspectRatio(0.85f), // Slightly taller for better proportions
        onClick = { onClick(calculator.route) },
        borderColor = NeonPink,
        useGradientBorder = true // Use gradient border
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = calculator.icon,
                contentDescription = null,
                tint = if (calculator.isPopular) NeonPink else NeonGreen,
                modifier = Modifier.size(48.dp) // Larger icon
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = calculator.name,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center,
                color = NeonText,
                maxLines = 2
            )
        }
    }
}
