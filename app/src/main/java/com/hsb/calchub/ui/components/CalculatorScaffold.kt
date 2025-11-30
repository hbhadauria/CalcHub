package com.hsb.calchub.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import com.hsb.calchub.data.repository.FavoritesRepository
import com.hsb.calchub.ui.theme.NeonBackground
import com.hsb.calchub.ui.theme.NeonGreen
import com.hsb.calchub.ui.theme.NeonPink
import com.hsb.calchub.ui.theme.NeonText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScaffold(
    title: String,
    onBackClick: () -> Unit,
    calculatorId: String,
    content: @Composable (PaddingValues) -> Unit
) {
    val context = LocalContext.current
    val favoritesRepository = remember { FavoritesRepository.getInstance(context) }
    val favorites by favoritesRepository.favorites.collectAsState()
    val isFavorite = favorites.contains(calculatorId)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = NeonText
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = NeonText
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { favoritesRepository.toggleFavorite(calculatorId) }) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Save",
                            tint = if (isFavorite) NeonPink else NeonText
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = NeonBackground,
                    titleContentColor = NeonText
                )
            )
        },
        containerColor = NeonBackground,
        content = content
    )
}
