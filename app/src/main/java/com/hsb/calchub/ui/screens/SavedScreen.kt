package com.hsb.calchub.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hsb.calchub.data.repository.FavoritesRepository
import com.hsb.calchub.domain.model.allCalculators
import com.hsb.calchub.ui.components.NeonCard
import com.hsb.calchub.ui.theme.NeonGreen
import com.hsb.calchub.ui.theme.NeonText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedScreen(onCalculatorClick: (String) -> Unit) {
    val context = LocalContext.current
    val favoritesRepository = remember { FavoritesRepository.getInstance(context) }
    val favorites by favoritesRepository.favorites.collectAsState()

    val savedCalculators = allCalculators.filter { favorites.contains(it.route) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "SAVED",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                color = NeonGreen
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (savedCalculators.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No saved calculators yet.",
                    color = NeonText.copy(alpha = 0.5f)
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(savedCalculators) { calculator ->
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
                                    tint = NeonGreen,
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
}
