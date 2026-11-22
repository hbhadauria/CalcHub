package com.hsb.calchub.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Construction
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hsb.calchub.ui.theme.CalcHubSkin
import com.hsb.calchub.ui.theme.LocalCalcHubTokens
import com.hsb.calchub.ui.theme.NeonGreen

/**
 * Multi-theme surface card preserving the [NeonCard] signature used across all 34 calculators.
 * Adapts automatically across Aurora, Ledger Paper, and Matrix skins without blur or glassmorphism.
 */
@Composable
fun NeonCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    borderColor: Color = Color.Unspecified,
    useGradientBorder: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    val tokens = LocalCalcHubTokens.current
    val shape = RoundedCornerShape(tokens.cardCornerRadius)

    val effectiveBorderColor = when {
        borderColor != Color.Unspecified -> borderColor
        useGradientBorder -> tokens.accentPrimary.copy(alpha = if (tokens.isDark) 0.50f else 0.40f)
        else -> tokens.cardBorder
    }

    val borderBrush = if (useGradientBorder) {
        Brush.linearGradient(
            colors = listOf(
                effectiveBorderColor,
                tokens.cardBorder
            )
        )
    } else {
        SolidColor(effectiveBorderColor)
    }

    val borderWidth = if (tokens.skin == CalcHubSkin.MATRIX) 1.25.dp else 1.dp

    if (onClick != null) {
        Surface(
            modifier = modifier.clip(shape),
            shape = shape,
            color = tokens.surfaceLowest,
            border = BorderStroke(borderWidth, borderBrush),
            onClick = onClick
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                content = content
            )
        }
    } else {
        Surface(
            modifier = modifier,
            shape = shape,
            color = tokens.surfaceLowest,
            border = BorderStroke(borderWidth, borderBrush)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                content = content
            )
        }
    }
}

/**
 * Themed numeric or text input field replacing the legacy neon-bordered field.
 */
@Composable
fun NeonInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    modifier: Modifier = Modifier
) {
    val tokens = LocalCalcHubTokens.current
    val shape = RoundedCornerShape(if (tokens.skin == CalcHubSkin.MATRIX) 10.dp else 16.dp)

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = tokens.inkSecondary,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                if (placeholder.isNotEmpty()) {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.bodyMedium,
                        color = tokens.inkMuted
                    )
                }
            },
            keyboardOptions = keyboardOptions,
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                fontFeatureSettings = "tnum",
                color = tokens.inkPrimary
            ),
            shape = shape,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = tokens.surfaceLow,
                unfocusedContainerColor = tokens.surfaceLow,
                focusedBorderColor = tokens.accentPrimary,
                unfocusedBorderColor = tokens.cardBorder,
                cursorColor = tokens.accentPrimary,
                focusedTextColor = tokens.inkPrimary,
                unfocusedTextColor = tokens.inkPrimary
            )
        )
    }
}

/**
 * Clean tonal search bar with leading magnifier, 48dp accessible clear action, and instant filtering.
 */
@Composable
fun NeonSearch(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search 40+ calculators (SIP, Tax, EMI...)"
) {
    val tokens = LocalCalcHubTokens.current
    val shape = RoundedCornerShape(if (tokens.skin == CalcHubSkin.MATRIX) 10.dp else tokens.pillCornerRadius)

    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.bodyMedium,
                color = tokens.inkMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = "Search",
                tint = tokens.accentPrimary
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Clear search",
                        tint = tokens.inkSecondary
                    )
                }
            }
        },
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyMedium.copy(
            color = tokens.inkPrimary
        ),
        shape = shape,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = tokens.surfaceLowest,
            unfocusedContainerColor = tokens.surfaceLowest,
            focusedBorderColor = tokens.accentPrimary,
            unfocusedBorderColor = tokens.cardBorder,
            cursorColor = tokens.accentPrimary,
            focusedTextColor = tokens.inkPrimary,
            unfocusedTextColor = tokens.inkPrimary
        )
    )
}

/**
 * Recommended bottom padding for scrollable lists so the final card clears the floating [NeonNavBar].
 */
@Composable
fun floatingNavBottomPadding(): Dp =
    WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 92.dp

/**
 * Floating overlay bottom navigation pill toolbar shared by [MainScreen] and legacy [NeonNavBar] callers.
 */
@Composable
fun NeonNavBar(
    selectedItem: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = LocalCalcHubTokens.current
    val haptic = LocalHapticFeedback.current
    val outerCorner = if (tokens.skin == CalcHubSkin.MATRIX) 12.dp else 28.dp
    val itemCorner = if (tokens.skin == CalcHubSkin.MATRIX) 8.dp else 22.dp

    val items: List<Pair<String, ImageVector>> = remember {
        listOf(
            "Home" to Icons.Rounded.Home,
            "Saved" to Icons.Rounded.Favorite,
            "Tools" to Icons.Rounded.Construction
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .widthIn(max = 320.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(outerCorner),
            color = tokens.surfaceToolbar,
            border = BorderStroke(1.dp, tokens.cardBorder),
            shadowElevation = 10.dp,
            tonalElevation = 0.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 5.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEachIndexed { index, (title, icon) ->
                    val isSelected = selectedItem == index
                    val bg by animateColorAsState(
                        targetValue = if (isSelected) tokens.accentContainer else Color.Transparent,
                        animationSpec = tween(180),
                        label = "navPillBg"
                    )
                    val fg by animateColorAsState(
                        targetValue = if (isSelected) tokens.onAccentContainer else tokens.inkSecondary,
                        animationSpec = tween(180),
                        label = "navPillFg"
                    )

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(itemCorner))
                            .semantics { this.selected = isSelected }
                            .clickable(role = Role.Tab) {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onItemSelected(index)
                            },
                        shape = RoundedCornerShape(itemCorner),
                        color = bg
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = fg,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                ),
                                color = fg,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Calculator screen header with safe status bar insets, back button, category-accent badge,
 * optional trailing actions, and expressive favorite toggle.
 */
@Composable
fun NeonHeader(
    title: String,
    subtitle: String,
    isFavorite: Boolean,
    onBackClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
    extraActions: @Composable (RowScope.() -> Unit)? = null
) {
    val tokens = LocalCalcHubTokens.current
    val haptic = LocalHapticFeedback.current
    val favScale by animateFloatAsState(
        targetValue = if (isFavorite) 1.12f else 1f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessMedium),
        label = "favPulse"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = tokens.surfaceLowest,
                border = BorderStroke(1.dp, tokens.cardBorder),
                onClick = onBackClick
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = tokens.inkPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = tokens.inkPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (subtitle.isNotBlank()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(tokens.chartEnd)
                        )
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.labelMedium,
                            color = tokens.inkSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            if (extraActions != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    content = extraActions
                )
            }

            Surface(
                modifier = Modifier
                    .size(44.dp)
                    .scale(favScale),
                shape = CircleShape,
                color = if (isFavorite) tokens.accentContainer else tokens.surfaceLowest,
                border = BorderStroke(
                    width = 1.dp,
                    color = if (isFavorite) tokens.accentPrimary else tokens.cardBorder
                ),
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onFavoriteClick()
                }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                        contentDescription = if (isFavorite) "Remove from Saved" else "Add to Saved",
                        tint = if (isFavorite) tokens.accentPrimary else tokens.inkSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Suppress("unused")
internal val UnusedNeonGreenReference: Color = NeonGreen
