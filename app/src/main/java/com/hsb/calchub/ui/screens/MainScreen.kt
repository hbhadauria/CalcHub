package com.hsb.calchub.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hsb.calchub.di.LocalAppContainer
import com.hsb.calchub.ui.components.AboutCalcHubDialog
import com.hsb.calchub.ui.components.AppearanceThemeSheet
import com.hsb.calchub.ui.components.NeonNavBar
import com.hsb.calchub.ui.components.OfflinePrivacyDialog
import com.hsb.calchub.ui.components.calcHubScreenBackdrop
import com.hsb.calchub.ui.theme.LocalCalcHubTokens

/**
 * Primary 3-tab hub hosting Home Discovery, Saved Favorites, and Tools Lab,
 * plus live Appearance & Offline Privacy controls.
 */
@Composable
fun MainScreen(
    onCalculatorClick: (String) -> Unit,
    selectedTab: Int? = null,
    onTabSelected: ((Int) -> Unit)? = null,
    activeDetailRoute: String? = null
) {
    val appContainer = LocalAppContainer.current
    val tokens = LocalCalcHubTokens.current

    var localTab by rememberSaveable { mutableIntStateOf(0) }
    val activeTab = selectedTab ?: localTab
    val setTab: (Int) -> Unit = { idx ->
        localTab = idx
        onTabSelected?.invoke(idx)
    }

    val themePrefs by appContainer.themeRepository.preferences.collectAsStateWithLifecycle()

    var showThemeSheet by rememberSaveable { mutableStateOf(false) }
    var showPrivacyDialog by rememberSaveable { mutableStateOf(false) }
    var showAboutDialog by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .calcHubScreenBackdrop(tokens)
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                HubTopHeader(
                    onOpenPrivacy = { showPrivacyDialog = true },
                    onOpenThemeSheet = { showThemeSheet = true }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = innerPadding.calculateTopPadding())
            ) {
                Crossfade(
                    targetState = activeTab,
                    animationSpec = tween(180),
                    label = "hubTabFade"
                ) { tab ->
                    when (tab) {
                        0 -> HomeScreen(
                            onCalculatorClick = onCalculatorClick,
                            activeDetailRoute = activeDetailRoute
                        )
                        1 -> FavoritesScreen(
                            onCalculatorClick = onCalculatorClick,
                            activeDetailRoute = activeDetailRoute
                        )
                        else -> ToolsScreen(
                            onCalculatorClick = onCalculatorClick
                        )
                    }
                }
            }
        }

        NeonNavBar(
            selectedItem = activeTab,
            onItemSelected = setTab,
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        if (showThemeSheet) {
            AppearanceThemeSheet(
                preferences = themePrefs,
                onSelectSkin = { skin -> appContainer.themeRepository.setSkin(skin) },
                onSelectPolarity = { polarity -> appContainer.themeRepository.setPolarity(polarity) },
                onSelectGradientMode = { mode -> appContainer.themeRepository.setGradientMode(mode) },
                onOpenPrivacyInfo = {
                    showThemeSheet = false
                    showPrivacyDialog = true
                },
                onOpenAboutInfo = {
                    showThemeSheet = false
                    showAboutDialog = true
                },
                onDismiss = { showThemeSheet = false }
            )
        }

        if (showPrivacyDialog) {
            OfflinePrivacyDialog(onDismiss = { showPrivacyDialog = false })
        }

        if (showAboutDialog) {
            AboutCalcHubDialog(onDismiss = { showAboutDialog = false })
        }
    }
}

@Composable
private fun HubTopHeader(
    onOpenPrivacy: () -> Unit,
    onOpenThemeSheet: () -> Unit
) {
    val tokens = LocalCalcHubTokens.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(12.dp),
                color = tokens.accentPrimary
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "₹",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                        color = tokens.onAccent
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "CalcHub",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = tokens.inkPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "40+ Financial Calculators",
                    style = MaterialTheme.typography.labelSmall,
                    color = tokens.inkSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(999.dp),
                color = tokens.gainBg,
                border = BorderStroke(1.dp, tokens.gainInk.copy(alpha = 0.30f)),
                onClick = onOpenPrivacy
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.VerifiedUser,
                        contentDescription = "Offline privacy status",
                        tint = tokens.gainInk,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Offline",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = tokens.gainInk
                    )
                }
            }

            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = tokens.surfaceLowest,
                border = BorderStroke(1.dp, tokens.cardBorder),
                onClick = onOpenThemeSheet
            ) {
                Box(
                    modifier = Modifier.background(Color.Transparent),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Palette,
                        contentDescription = "Customize appearance and theme",
                        tint = tokens.accentPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
