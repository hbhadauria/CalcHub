package com.hsb.calchub.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hsb.calchub.data.repository.FavoritesRepository
import com.hsb.calchub.domain.model.allCalculators
import com.hsb.calchub.ui.components.NeonCard
import com.hsb.calchub.ui.components.NeonSearch
import com.hsb.calchub.ui.theme.NeonGreen
import com.hsb.calchub.ui.theme.NeonPink
import com.hsb.calchub.ui.theme.NeonText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(onCalculatorClick: (String) -> Unit) {
    val context = LocalContext.current
    val favoritesRepository = remember { FavoritesRepository.getInstance(context) }
    val favorites by favoritesRepository.favorites.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    val favoriteCalculators = allCalculators.filter { favorites.contains(it.route) }
    
    // Filter favorite calculators based on search
    val filteredCalculators = if (searchQuery.isEmpty()) {
        favoriteCalculators
    } else {
        favoriteCalculators.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    // Background with gradient
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF050505),
                        Color(0xFF120024),
                        Color(0xFF050505)
                    )
                )
            )
    ) {
        // Subtle background glow effects
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(NeonPink.copy(alpha = 0.15f), Color.Transparent),
                    center = Offset(width / 2, height * 0.1f),
                    radius = width * 0.8f
                )
            )
            
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(NeonGreen.copy(alpha = 0.1f), Color.Transparent),
                    center = Offset(0f, height),
                    radius = width * 0.6f
                )
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(NeonGreen.copy(alpha = 0.1f), Color.Transparent),
                    center = Offset(width, height),
                    radius = width * 0.6f
                )
            )
        }

        // Main Content (List)
        if (favoriteCalculators.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 180.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No favorite calculators yet.",
                    color = NeonText.copy(alpha = 0.5f),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(
                    top = 180.dp,
                    bottom = 120.dp,
                    start = 16.dp,
                    end = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                if (filteredCalculators.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No calculators match your search.",
                                color = NeonText.copy(alpha = 0.5f),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                } else {
                    items(filteredCalculators) { calculator ->
                        NeonCard(
                            onClick = { onCalculatorClick(calculator.route) }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                calculator.icon?.let { icon ->
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = NeonPink,
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                }
                                Text(
                                    text = calculator.name,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = NeonText
                                    )
                                )
                            }
                        }
                    }
                }
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
                .padding(top = 16.dp, bottom = 24.dp)
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
                    text = "Favorites",
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
                    imageVector = Icons.Default.Favorite,
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
