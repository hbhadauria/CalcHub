package com.hsb.calchub.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hsb.calchub.di.LocalAppContainer
import com.hsb.calchub.domain.model.allCalculators
import com.hsb.calchub.ui.components.NeonCard
import com.hsb.calchub.ui.components.NeonSearch
import com.hsb.calchub.ui.components.label
import com.hsb.calchub.ui.theme.CategoryPalettes
import com.hsb.calchub.ui.theme.LocalCalcHubTokens

/**
 * Saved / Pinned calculators tab with search filtering and one-tap starter preset pinning.
 */
@Composable
fun FavoritesScreen(
    onCalculatorClick: (String) -> Unit,
    activeDetailRoute: String? = null
) {
    val appContainer = LocalAppContainer.current
    val favoritesRepository = appContainer.favoritesRepository

    val tokens = LocalCalcHubTokens.current
    val favoriteIds by favoritesRepository.favorites.collectAsStateWithLifecycle()
    var searchQuery by rememberSaveable { mutableStateOf("") }

    val favoriteCalculators = remember(favoriteIds, searchQuery) {
        allCalculators.filter { calc ->
            calc.route in favoriteIds && (
                searchQuery.isBlank() ||
                    calc.name.contains(searchQuery.trim(), ignoreCase = true) ||
                    calc.subtitle.contains(searchQuery.trim(), ignoreCase = true)
                )
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item(key = "fav_header") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Saved Calculators",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = tokens.inkPrimary
                    )
                    Text(
                        text = "Pinned locally on device for instant access",
                        style = MaterialTheme.typography.bodySmall,
                        color = tokens.inkSecondary
                    )
                }
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = tokens.surfaceContainer
                ) {
                    Text(
                        text = "${favoriteIds.size} pinned",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontFeatureSettings = "tnum"
                        ),
                        color = tokens.inkPrimary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        }

        if (favoriteIds.isNotEmpty()) {
            item(key = "fav_search") {
                NeonSearch(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    placeholder = "Search saved calculators..."
                )
            }
        }

        if (favoriteIds.isEmpty()) {
            item(key = "fav_empty") {
                NeonCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 18.dp, horizontal = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            modifier = Modifier.size(56.dp),
                            shape = CircleShape,
                            color = tokens.accentContainer
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Rounded.FavoriteBorder,
                                    contentDescription = null,
                                    tint = tokens.accentPrimary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                        Text(
                            text = "No favorites pinned yet",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = tokens.inkPrimary
                        )
                        Text(
                            text = "Tap the heart on any calculator card or pin the top 4 daily essentials right away.",
                            style = MaterialTheme.typography.bodySmall,
                            color = tokens.inkSecondary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Button(
                            onClick = {
                                listOf("sip", "emi", "income_tax", "ppf").forEach { route ->
                                    if (!favoritesRepository.isFavorite(route)) {
                                        favoritesRepository.toggleFavorite(route)
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = tokens.accentPrimary,
                                contentColor = tokens.onAccent
                            ),
                            shape = RoundedCornerShape(tokens.pillCornerRadius)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.AutoAwesome,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.size(6.dp))
                            Text(
                                text = "Pin Essentials (SIP, EMI, Tax, PPF)",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
        } else if (favoriteCalculators.isEmpty()) {
            item(key = "fav_no_match") {
                NeonCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "No pinned calculator matches \"$searchQuery\".",
                        style = MaterialTheme.typography.bodyMedium,
                        color = tokens.inkSecondary
                    )
                }
            }
        } else {
            items(
                items = favoriteCalculators,
                key = { it.route }
            ) { calc ->
                val catPalette = remember(calc.category) { CategoryPalettes.forCategory(calc.category) }
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(tokens.cardCornerRadius)),
                    shape = RoundedCornerShape(tokens.cardCornerRadius),
                    color = tokens.surfaceLowest,
                    border = BorderStroke(
                        width = if (calc.route == activeDetailRoute) 2.dp else 1.dp,
                        color = if (calc.route == activeDetailRoute) tokens.accentPrimary else tokens.cardBorder
                    ),
                    onClick = { onCalculatorClick(calc.route) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(catPalette.badgeGradStart, catPalette.badgeGradEnd)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = calc.icon,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = calc.name,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = tokens.inkPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${calc.category.label()} · ${calc.subtitle}",
                                style = MaterialTheme.typography.bodySmall,
                                color = tokens.inkSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        if (calc.sampleValue.isNotBlank()) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = tokens.surfaceLow
                            ) {
                                Text(
                                    text = calc.sampleValue,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontFeatureSettings = "tnum"
                                    ),
                                    color = tokens.inkPrimary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        IconButton(
                            onClick = { favoritesRepository.toggleFavorite(calc.route) },
                            modifier = Modifier.size(38.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Favorite,
                                contentDescription = "Unpin ${calc.name}",
                                tint = catPalette.gradEnd,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
