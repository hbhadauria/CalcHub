package com.hsb.calchub.ui.components

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BookmarkAdd
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hsb.calchub.data.repository.RecentCalculation
import com.hsb.calchub.data.repository.SavedScenario
import com.hsb.calchub.di.LocalAppContainer
import com.hsb.calchub.domain.model.CalculatorCategory
import com.hsb.calchub.domain.model.categoryForRoute
import com.hsb.calchub.domain.model.findCalculatorByRoute
import com.hsb.calchub.ui.theme.LocalCalcHubTokens
import com.hsb.calchub.ui.theme.ProvideCategoryAccent
import kotlinx.coroutines.launch

fun CalculatorCategory.label(): String = when (this) {
    CalculatorCategory.POPULAR -> "Popular"
    CalculatorCategory.INVESTMENT -> "Investment"
    CalculatorCategory.LOAN -> "Loan"
    CalculatorCategory.TAX -> "Tax"
    CalculatorCategory.RETIREMENT -> "Retirement"
    CalculatorCategory.TRADING -> "Trading"
    CalculatorCategory.OTHER -> "Other"
}

/**
 * Shared scaffold across all 34 calculator screens.
 * Automatically binds screen-scoped category accents, theme gradients, favorite state,
 * Continue-card recent tracking, and optional Vault scenario bookmarking.
 */
@Composable
fun CalculatorScaffold(
    title: String,
    onBackClick: () -> Unit,
    calculatorId: String,
    subtitle: String? = null,
    onSaveScenario: (() -> Unit)? = null,
    shareSummary: String? = null,
    content: @Composable (PaddingValues) -> Unit
) {
    val appContainer = LocalAppContainer.current
    val favoritesRepository = appContainer.favoritesRepository
    val userDataRepository = appContainer.userDataRepository

    val favorites by favoritesRepository.favorites.collectAsState()
    val isFavorite = favorites.contains(calculatorId)

    val meta = remember(calculatorId) { findCalculatorByRoute(calculatorId) }
    val category = remember(calculatorId) { categoryForRoute(calculatorId) }
    val resolvedSubtitle = subtitle ?: meta.subtitle.ifBlank { "${category.label()} Calculator" }
    val context = LocalContext.current
    val resolvedShareText = remember(shareSummary, meta.name, title, resolvedSubtitle, meta.formula) {
        shareSummary ?: buildString {
            append(meta.name.ifBlank { title })
            append(" — CalcHub\n")
            append(resolvedSubtitle)
            if (meta.formula.isNotBlank()) {
                append("\nFormula: ")
                append(meta.formula)
            }
            append("\nCalculated 100% offline with CalcHub")
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(calculatorId) {
        userDataRepository.updateLastUsed(
            RecentCalculation(
                route = calculatorId,
                calculatorName = meta.name.ifBlank { title },
                paramsSummary = resolvedSubtitle,
                formattedValue = meta.sampleValue.ifBlank { "Ready" },
                secondarySummary = meta.formula.ifBlank { "Live offline calculation" }
            )
        )
    }

    ProvideCategoryAccent(category = category) {
        val tokens = LocalCalcHubTokens.current

        val saveAction: () -> Unit = onSaveScenario ?: {
            userDataRepository.saveScenario(
                SavedScenario(
                    id = "${calculatorId}_${System.currentTimeMillis()}",
                    route = calculatorId,
                    title = meta.name.ifBlank { title },
                    summary = resolvedSubtitle,
                    resultFormatted = meta.sampleValue.ifBlank { "Saved" }
                )
            )
            scope.launch {
                snackbarHostState.showSnackbar("Saved ${meta.name.ifBlank { title }} to Vault")
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .calcHubScreenBackdrop(tokens)
        ) {
            Scaffold(
                containerColor = Color.Transparent,
                snackbarHost = { SnackbarHost(snackbarHostState) },
                topBar = {
                    NeonHeader(
                        title = title,
                        subtitle = resolvedSubtitle,
                        isFavorite = isFavorite,
                        onBackClick = onBackClick,
                        onFavoriteClick = { favoritesRepository.toggleFavorite(calculatorId) },
                        extraActions = {
                            Surface(
                                modifier = Modifier.size(44.dp),
                                shape = CircleShape,
                                color = tokens.surfaceLowest,
                                border = BorderStroke(1.dp, tokens.cardBorder),
                                onClick = {
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_SUBJECT, "$title — CalcHub")
                                        putExtra(Intent.EXTRA_TEXT, resolvedShareText)
                                    }
                                    val chooser = Intent.createChooser(shareIntent, "Share calculation").apply {
                                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    }
                                    runCatching { context.startActivity(chooser) }
                                }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Rounded.Share,
                                        contentDescription = "Share calculation summary",
                                        tint = tokens.inkPrimary,
                                        modifier = Modifier.size(19.dp)
                                    )
                                }
                            }
                            Surface(
                                modifier = Modifier.size(44.dp),
                                shape = CircleShape,
                                color = tokens.surfaceLowest,
                                border = BorderStroke(1.dp, tokens.cardBorder),
                                onClick = saveAction
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Rounded.BookmarkAdd,
                                        contentDescription = "Save scenario to Vault",
                                        tint = tokens.accentPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    )
                },
                content = content
            )
        }
    }
}

/**
 * Scrollable screen container wrapping [CalculatorScaffold] with standard horizontal padding,
 * vertical spacing, and trailing bottom clearance.
 */
@Composable
fun CalculatorScrollScaffold(
    title: String,
    onBackClick: () -> Unit,
    calculatorId: String,
    subtitle: String? = null,
    onSaveScenario: (() -> Unit)? = null,
    shareSummary: String? = null,
    verticalSpacing: Dp = 12.dp,
    bottomSpacerHeight: Dp = 16.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    CalculatorScaffold(
        title = title,
        onBackClick = onBackClick,
        calculatorId = calculatorId,
        subtitle = subtitle,
        onSaveScenario = onSaveScenario,
        shareSummary = shareSummary
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(verticalSpacing)
        ) {
            content()
            Spacer(modifier = Modifier.height(bottomSpacerHeight))
        }
    }
}
