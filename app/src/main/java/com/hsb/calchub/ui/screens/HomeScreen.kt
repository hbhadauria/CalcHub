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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.ReceiptLong
import androidx.compose.material.icons.automirrored.rounded.ShowChart
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material.icons.rounded.Elderly
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hsb.calchub.data.repository.RecentCalculation
import com.hsb.calchub.di.LocalAppContainer
import com.hsb.calchub.domain.model.Calculator
import com.hsb.calchub.domain.model.CalculatorCategory
import com.hsb.calchub.domain.model.allCalculators
import com.hsb.calchub.domain.model.categoryForRoute
import com.hsb.calchub.ui.components.MiniGrowthSparkline
import com.hsb.calchub.ui.components.MorphingPolygonBadge
import com.hsb.calchub.ui.components.NeonCard
import com.hsb.calchub.ui.components.NeonSearch
import com.hsb.calchub.ui.components.OdometerText
import com.hsb.calchub.ui.components.label
import com.hsb.calchub.ui.components.shapeTypeForCategory
import com.hsb.calchub.ui.theme.CalcHubSkin
import com.hsb.calchub.ui.theme.CategoryAccentPalette
import com.hsb.calchub.ui.theme.CategoryPalettes
import com.hsb.calchub.ui.theme.LocalCalcHubTokens

private data class CategoryOrbItem(
    val category: CalculatorCategory?,
    val label: String,
    val icon: ImageVector,
    val palette: CategoryAccentPalette
)

private val HOME_ORBS = listOf(
    CategoryOrbItem(null, "All", Icons.Rounded.Apps, CategoryPalettes.All),
    CategoryOrbItem(CalculatorCategory.INVESTMENT, "Invest", Icons.AutoMirrored.Rounded.TrendingUp, CategoryPalettes.Investment),
    CategoryOrbItem(CalculatorCategory.LOAN, "Loan", Icons.Rounded.AccountBalance, CategoryPalettes.Loan),
    CategoryOrbItem(CalculatorCategory.TAX, "Tax", Icons.AutoMirrored.Rounded.ReceiptLong, CategoryPalettes.Tax),
    CategoryOrbItem(CalculatorCategory.RETIREMENT, "Retire", Icons.Rounded.Elderly, CategoryPalettes.Retirement),
    CategoryOrbItem(CalculatorCategory.TRADING, "Trade", Icons.AutoMirrored.Rounded.ShowChart, CategoryPalettes.Trading),
    CategoryOrbItem(CalculatorCategory.OTHER, "Other", Icons.Rounded.Calculate, CategoryPalettes.Other)
)

/**
 * Discovery hub featuring the live Continue Hero banner, morphing category polygon orbs,
 * instant 34-tool search, and skin-adaptive calculator grid/ledger rows.
 */
@Composable
fun HomeScreen(
    onCalculatorClick: (String) -> Unit,
    activeDetailRoute: String? = null
) {
    val appContainer = LocalAppContainer.current
    val favoritesRepository = appContainer.favoritesRepository

    val tokens = LocalCalcHubTokens.current
    val favorites by favoritesRepository.favorites.collectAsStateWithLifecycle()
    val lastUsed by appContainer.userDataRepository.lastUsed.collectAsStateWithLifecycle()

    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf<CalculatorCategory?>(null) }

    val countsByCategory = remember {
        allCalculators.groupingBy { it.category }.eachCount()
    }

    val filteredCalculators = remember(searchQuery, selectedCategory) {
        allCalculators.filter { calc ->
            val matchesCat = selectedCategory == null || calc.category == selectedCategory
            val q = searchQuery.trim()
            val matchesQuery = q.isEmpty() ||
                calc.name.contains(q, ignoreCase = true) ||
                calc.subtitle.contains(q, ignoreCase = true) ||
                calc.category.label().contains(q, ignoreCase = true) ||
                calc.formula.contains(q, ignoreCase = true) ||
                calc.route.contains(q, ignoreCase = true)
            matchesCat && matchesQuery
        }
    }

    val useTwoColumnAuroraGrid = tokens.skin == CalcHubSkin.AURORA && searchQuery.isBlank()
    val chunkedPairs = remember(filteredCalculators, useTwoColumnAuroraGrid) {
        if (useTwoColumnAuroraGrid) filteredCalculators.chunked(2) else emptyList()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        if (searchQuery.isBlank() && selectedCategory == null) {
            item(key = "continue_hero") {
                ContinueHeroBanner(
                    recent = lastUsed,
                    onClick = { onCalculatorClick(lastUsed.route) }
                )
            }
        }

        item(key = "search_bar") {
            NeonSearch(
                query = searchQuery,
                onQueryChange = { searchQuery = it }
            )
        }

        item(key = "category_orbs") {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(vertical = 2.dp)
            ) {
                items(HOME_ORBS, key = { it.label }) { orb ->
                    val count = if (orb.category == null) {
                        allCalculators.size
                    } else {
                        countsByCategory[orb.category] ?: 0
                    }
                    MorphingPolygonBadge(
                        label = orb.label,
                        icon = orb.icon,
                        selected = selectedCategory == orb.category,
                        shapeType = shapeTypeForCategory(orb.category),
                        accentPalette = orb.palette,
                        count = count,
                        onClick = {
                            selectedCategory = if (selectedCategory == orb.category) null else orb.category
                        }
                    )
                }
            }
        }

        item(key = "section_header") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedCategory?.label() ?: if (searchQuery.isBlank()) "All Calculators" else "Matching Results",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = tokens.inkPrimary
                )
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = tokens.surfaceContainer
                ) {
                    val toolsCountLabel = if (filteredCalculators.size > 30) "30+" else filteredCalculators.size.toString()
                    Text(
                        text = "$toolsCountLabel tools",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontFeatureSettings = "tnum"
                        ),
                        color = tokens.inkSecondary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }

        if (filteredCalculators.isEmpty()) {
            item(key = "empty_search") {
                NeonCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "No matching calculators",
                            style = MaterialTheme.typography.titleMedium,
                            color = tokens.inkPrimary
                        )
                        Text(
                            text = "Try \"SIP\", \"Tax\", \"EMI\", or clear filters.",
                            style = MaterialTheme.typography.bodySmall,
                            color = tokens.inkSecondary
                        )
                    }
                }
            }
        } else if (useTwoColumnAuroraGrid) {
            items(
                items = chunkedPairs,
                key = { pair -> pair.joinToString("_") { it.route } }
            ) { pair ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    pair.forEach { calc ->
                        AuroraCalculatorGridTile(
                            calculator = calc,
                            isFavorite = favorites.contains(calc.route),
                            isSelectedPane = calc.route == activeDetailRoute,
                            onToggleFavorite = { favoritesRepository.toggleFavorite(calc.route) },
                            onClick = { onCalculatorClick(calc.route) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (pair.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        } else {
            items(
                items = filteredCalculators,
                key = { it.route }
            ) { calc ->
                DenseCalculatorRowCard(
                    calculator = calc,
                    isFavorite = favorites.contains(calc.route),
                    isSelectedPane = calc.route == activeDetailRoute,
                    onToggleFavorite = { favoritesRepository.toggleFavorite(calc.route) },
                    onClick = { onCalculatorClick(calc.route) }
                )
            }
        }
    }
}

@Composable
private fun ContinueHeroBanner(
    recent: RecentCalculation,
    onClick: () -> Unit
) {
    val tokens = LocalCalcHubTokens.current
    val cat = remember(recent.route) { categoryForRoute(recent.route) }
    val palette = remember(cat) { CategoryPalettes.forCategory(cat) }

    val washBrush = if (tokens.skin == CalcHubSkin.AURORA && tokens.useGradientFill) {
        Brush.linearGradient(
            listOf(
                if (tokens.isDark) palette.darkWashStart else palette.lightWashStart,
                if (tokens.isDark) palette.darkWashEnd else palette.lightWashEnd
            )
        )
    } else {
        SolidColor(tokens.surfaceLowest)
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(tokens.heroCornerRadius)),
        shape = RoundedCornerShape(tokens.heroCornerRadius),
        color = Color.Transparent,
        border = BorderStroke(1.dp, tokens.cardBorder),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(washBrush)
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(palette.gradStart)
                        )
                        Text(
                            text = "CONTINUE · ${cat.label().uppercase()}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = tokens.inkSecondary
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = tokens.accentPrimary
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Resume",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = tokens.onAccent
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                                contentDescription = null,
                                tint = tokens.onAccent,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = recent.calculatorName,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = tokens.inkPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = recent.paramsSummary,
                            style = MaterialTheme.typography.bodySmall,
                            color = tokens.inkSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OdometerText(
                            text = recent.formattedValue,
                            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold),
                            color = tokens.inkPrimary,
                            highlightLedger = true,
                            useDotMatrixInMatrixSkin = true
                        )
                        Text(
                            text = recent.secondarySummary,
                            style = MaterialTheme.typography.labelSmall,
                            color = tokens.gainInk,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    MiniGrowthSparkline(
                        modifier = Modifier
                            .width(86.dp)
                            .height(48.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AuroraCalculatorGridTile(
    calculator: Calculator,
    isFavorite: Boolean,
    isSelectedPane: Boolean,
    onToggleFavorite: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = LocalCalcHubTokens.current
    val catPalette = remember(calculator.category) { CategoryPalettes.forCategory(calculator.category) }
    val borderColor = if (isSelectedPane) catPalette.gradStart else tokens.cardBorder

    Surface(
        modifier = modifier.clip(RoundedCornerShape(tokens.cardCornerRadius)),
        shape = RoundedCornerShape(tokens.cardCornerRadius),
        color = tokens.surfaceLowest,
        border = BorderStroke(if (isSelectedPane) 2.dp else 1.dp, borderColor),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(catPalette.badgeGradStart, catPalette.badgeGradEnd)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = calculator.icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(21.dp)
                    )
                }

                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                        contentDescription = if (isFavorite) "Remove ${calculator.name} from favorites" else "Save ${calculator.name} to favorites",
                        tint = if (isFavorite) catPalette.gradEnd else tokens.inkMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Column {
                Text(
                    text = calculator.name,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = tokens.inkPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = calculator.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.inkSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (calculator.sampleValue.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = tokens.surfaceLow
                ) {
                    Text(
                        text = calculator.sampleValue,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontFeatureSettings = "tnum"
                        ),
                        color = tokens.inkSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun DenseCalculatorRowCard(
    calculator: Calculator,
    isFavorite: Boolean,
    isSelectedPane: Boolean,
    onToggleFavorite: () -> Unit,
    onClick: () -> Unit
) {
    val tokens = LocalCalcHubTokens.current
    val catPalette = remember(calculator.category) { CategoryPalettes.forCategory(calculator.category) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(tokens.cardCornerRadius)),
        shape = RoundedCornerShape(tokens.cardCornerRadius),
        color = tokens.surfaceLowest,
        border = BorderStroke(
            width = if (isSelectedPane) 2.dp else 1.dp,
            color = if (isSelectedPane) tokens.accentPrimary else tokens.cardBorder
        ),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(catPalette.badgeGradStart, catPalette.badgeGradEnd)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = calculator.icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = calculator.name,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = tokens.inkPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = calculator.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.inkSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (calculator.sampleValue.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = tokens.surfaceLow
                ) {
                    Text(
                        text = calculator.sampleValue,
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
                onClick = onToggleFavorite,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                    contentDescription = if (isFavorite) "Remove ${calculator.name}" else "Save ${calculator.name}",
                    tint = if (isFavorite) tokens.accentPrimary else tokens.inkMuted,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
