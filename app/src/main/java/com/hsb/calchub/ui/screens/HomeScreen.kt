package com.hsb.calchub.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hsb.calchub.domain.model.Calculator
import com.hsb.calchub.domain.model.allCalculators
import com.hsb.calchub.ui.components.NeonCard
import com.hsb.calchub.ui.components.NeonSearch
import com.hsb.calchub.ui.theme.NeonGreen
import com.hsb.calchub.ui.theme.NeonPink
import com.hsb.calchub.ui.theme.NeonText

@Composable
fun HomeScreen(onCalculatorClick: (String) -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    
    // Filter calculators based on search
    val filteredCalculators = if (searchQuery.isEmpty()) {
        allCalculators
    } else {
        allCalculators.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    // Background with gradient
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF050505), // Top - Deep Black
                        Color(0xFF120024), // Middle - Deep Purple/Blue hint
                        Color(0xFF050505)  // Bottom - Deep Black
                    )
                )
            )
    ) {
        // Subtle background glow effects
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            
            // Top center glow (Green/Cyan)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(NeonGreen.copy(alpha = 0.15f), Color.Transparent),
                    center = Offset(width / 2, height * 0.1f),
                    radius = width * 0.8f
                )
            )
            
            // Bottom corners glow (Pink/Purple)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(NeonPink.copy(alpha = 0.1f), Color.Transparent),
                    center = Offset(0f, height),
                    radius = width * 0.6f
                )
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(NeonPink.copy(alpha = 0.1f), Color.Transparent),
                    center = Offset(width, height),
                    radius = width * 0.6f
                )
            )
        }

        // Main Content (Grid) - Now behind the floating elements
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(
                top = 230.dp, // Increased to avoid overlap with header + status bar
                bottom = 120.dp, // Space for floating nav bar
                start = 16.dp,
                end = 16.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredCalculators) { calculator ->
                CalculatorCard(calculator, onCalculatorClick)
            }
        }

        // Floating Header & Search
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF050505).copy(alpha = 0.95f),
                            Color(0xFF050505).copy(alpha = 0.8f),
                            Color.Transparent
                        )
                    )
                )
                .padding(horizontal = 16.dp)
                .statusBarsPadding()
                .padding(top = 16.dp, bottom = 24.dp) // Add some bottom padding for the gradient fade
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text(
                    text = "Calc",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = NeonGreen,
                        shadow = Shadow(
                            color = NeonGreen,
                            blurRadius = 20f
                        )
                    )
                )
                Text(
                    text = "Hub",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = NeonPink,
                        shadow = Shadow(
                            color = NeonPink,
                            blurRadius = 20f
                        )
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.Calculate,
                    contentDescription = null,
                    tint = NeonText,
                    modifier = Modifier.size(32.dp)
                )
            }

            // Search Bar
            NeonSearch(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
    }
}

@Composable
fun CalculatorCard(calculator: Calculator, onClick: (String) -> Unit) {
    NeonCard(
        modifier = Modifier.aspectRatio(0.65f),
        onClick = { onClick(calculator.route) },
        borderColor = if (calculator.isPopular) NeonGreen else NeonPink,
        useGradientBorder = true
    ) {
        // This Column's arrangement is now in full control.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 12.dp, horizontal = 4.dp), // Adjust padding
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center // This will now work as expected
        ) {
            // The Box now wraps its content instead of expanding.
            Box(
                contentAlignment = Alignment.Center,
                // *** CRITICAL CHANGE: Modifier.weight(1f) is REMOVED ***
                // The size of this box will now be determined by its children.
            ) {
                // The Canvas defines the glow area.
                Canvas(modifier = Modifier.size(56.dp)) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                (if (calculator.isPopular) NeonGreen else NeonPink).copy(alpha = 0.3f),
                                Color.Transparent
                            )
                        )
                    )
                }
                // The Icon is drawn on top of the Canvas.
                Icon(
                    imageVector = calculator.icon,
                    contentDescription = calculator.name,
                    tint = if (calculator.isPopular) NeonGreen else NeonPink,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = calculator.name,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center,
                color = NeonText,
                maxLines = 2,
                minLines = 2
            )
        }
    }
}
