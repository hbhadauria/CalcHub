package com.hsb.calchub.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hsb.calchub.ui.theme.NeonGreen
import com.hsb.calchub.ui.theme.NeonPink
import com.hsb.calchub.ui.theme.NeonSurface
import com.hsb.calchub.ui.theme.NeonText

/**
 * A specialized card with a neon glow effect.
 *
 * @param modifier Modifier for styling.
 * @param onClick Optional callback for click events.
 * @param borderColor The color of the border and glow (e.g., NeonGreen, NeonPink).
 * @param useGradientBorder If true, applies a vertical gradient to the border.
 * @param content The content composable.
 */
@Composable
fun NeonCard(modifier: Modifier = Modifier,
             onClick: (() -> Unit)? = null,
             borderColor: Color = NeonGreen,
             useGradientBorder: Boolean = false,
             content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(16.dp)
    val borderBrush = if (useGradientBorder) {
        Brush.verticalGradient(
            colors = listOf(borderColor.copy(alpha = 0.8f), borderColor.copy(alpha = 0.1f)),
            startY = 0f,
            endY = 300f
        )
    } else {
        SolidColor(borderColor.copy(alpha = 0.5f))
    }

    // The main container that defines the card's shape, border, and behavior.
    Box(
        modifier = modifier
            // *** THE FINAL FIX ***
            // Replace the failing .shadow() with our new, correct .coloredGlow()
            .coloredGlow(
                color = borderColor,
                shape = shape,
                blurRadius = 30f // Adjust blur amount as you like
            )
            .background(Color.Transparent, shape) // Background remains transparent
            .border(BorderStroke(1.5.dp, borderBrush), shape)
            .clip(shape) // Clip everything inside to the card's shape
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = androidx.compose.foundation.LocalIndication.current,
                        onClick = onClick
                    )
                } else Modifier
            ),
        contentAlignment = Alignment.Center // Center content by default
    ) {
        // Inner Glow (Top) - drawn first, so it's in the background
        Canvas(modifier = Modifier.matchParentSize()) { // Fills the Box
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(borderColor.copy(alpha = 0.05f), Color.Transparent),
                    startY = 0f,
                    endY = size.height / 2 // Confine glow to top half
                )
            )
        }

        // The content Column is now the second child, drawn on top of the glow.
        Column(
            modifier = Modifier
                .fillMaxSize() // Make the column fill the Box
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            content() // Invoke the passed-in content lambda
        }
    }
}

fun Modifier.coloredGlow(color: Color, shape: androidx.compose.ui.graphics.Shape, blurRadius: Float = 30f, strokeWidth: Float = 6f) = this.drawBehind {
    // This drawIntoCanvas is the key. It gives us a native canvas.
    drawIntoCanvas { canvas ->
        val paint = androidx.compose.ui.graphics.Paint().apply {
            this.color = color
        }
        val frameworkPaint = paint.asFrameworkPaint() // This is the native paint object

        // Set the blur effect on the native paint object.
        if (blurRadius > 0) {
            frameworkPaint.maskFilter = (android.graphics.BlurMaskFilter(blurRadius, android.graphics.BlurMaskFilter.Blur.NORMAL))
        }

        // *** THE CRUCIAL FIX IS HERE ***
        // By setting the style to STROKE, we only draw the outline, which creates a hollow glow.
        frameworkPaint.style = android.graphics.Paint.Style.STROKE
        frameworkPaint.strokeWidth = strokeWidth


        // Draw the shape outline with the blurred paint.
        canvas.drawOutline(
            outline = shape.createOutline(size, layoutDirection, this),
            paint = paint
        )
    }
}

/**
 * A text input field styled with Neon aesthetics.
 *
 * @param value Current text value.
 * @param onValueChange Callback for text changes.
 * @param label Label to display above input.
 * @param placeholder Placeholder text when empty.
 * @param keyboardOptions Keyboard configuration.
 * @param modifier Modifier for styling.
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
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = NeonText.copy(alpha = 0.7f),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(
                color = NeonText,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            ),
            keyboardOptions = keyboardOptions,
            cursorBrush = SolidColor(NeonGreen),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(NeonSurface, RoundedCornerShape(12.dp))
                        .border(1.dp, NeonGreen.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    if (value.isEmpty() && placeholder.isNotEmpty()) {
                        Text(
                            text = placeholder,
                            color = NeonText.copy(alpha = 0.3f),
                            fontSize = 18.sp
                        )
                    }
                    innerTextField()
                }
            }
        )
    }
}

@Composable
fun NeonSearch(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BasicTextField(
        value = query,
        onValueChange = onQueryChange,
        textStyle = TextStyle(color = NeonText, fontSize = 16.sp),
        cursorBrush = SolidColor(NeonPink),
        decorationBox = { innerTextField ->
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .background(NeonSurface.copy(alpha = 0.3f), RoundedCornerShape(16.dp)) // More transparent, less rounded
                    .border(1.dp, NeonText.copy(alpha = 0.2f), RoundedCornerShape(16.dp)) // Subtle border
                    .padding(horizontal = 16.dp, vertical = 16.dp), // More vertical padding
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = NeonText.copy(alpha = 0.5f),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Box {
                    if (query.isEmpty()) {
                        Text("Search Calculators", color = NeonText.copy(alpha = 0.5f), fontSize = 16.sp)
                    }
                    innerTextField()
                }
            }
        }
    )
}

@Composable
fun NeonNavBar(
    selectedItem: Int,
    onItemSelected: (Int) -> Unit
) {
    NavigationBar(
        containerColor = NeonSurface.copy(alpha = 0.9f),
        // *** THE DEFINITIVE FIX ***
        // Apply the aesthetic modifiers AND explicitly clear default inset handling.
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets(0, 0, 0, 0)) // 1. PREVENT DOUBLE PADDING
            .clip(RoundedCornerShape(32.dp))
            .border(1.dp, NeonText.copy(alpha = 0.1f), RoundedCornerShape(32.dp))
    ) {
        // ... (The rest of the function remains the same) ...
        val navItems = listOf("Home", "Favorites", "Tools")
        val navIcons = listOf(Icons.Outlined.Home, Icons.Outlined.FavoriteBorder, Icons.Outlined.Build)
        val navColors = listOf(NeonGreen, NeonPink, NeonGreen)

        navItems.forEachIndexed { index, title ->
            val isSelected = selectedItem == index

            NavigationBarItem(
                selected = isSelected,
                onClick = { onItemSelected(index) },
                icon = {
                    Icon(
                        imageVector = navIcons[index],
                        contentDescription = title
                    )
                },
                label = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = navColors[index].copy(alpha = 0.15f),
                    selectedIconColor = navColors[index],
                    selectedTextColor = navColors[index],
                    unselectedIconColor = NeonText.copy(alpha = 0.7f),
                    unselectedTextColor = NeonText.copy(alpha = 0.7f)
                )
            )
        }
    }
}

/**
 * The standard header for the application, featuring the "CalcHub" text.
 * Provides Back and Favorite actions.
 *
 * @param title The main title (CalcHub).
 * @param subtitle The subtitle (Screen Name).
 * @param isFavorite Whether the current screen is marked as favorite.
 * @param onBackClick Callback for back button.
 * @param onFavoriteClick Callback for favorite toggle.
 */
@Composable
fun NeonHeader(
    title: String,
    subtitle: String,
    isFavorite: Boolean,
    onBackClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding() // Fix status bar overlap
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: Menu/Back Button
        Box(
            modifier = Modifier
                .size(48.dp)
                .coloredGlow(NeonGreen, RoundedCornerShape(12.dp), blurRadius = 15f)
                .background(NeonSurface.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .border(1.dp, NeonGreen.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = androidx.compose.foundation.LocalIndication.current,
                    onClick = onBackClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = NeonGreen
            )
        }

        // Center: Title and Subtitle
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Calc",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = NeonGreen,
                        shadow = androidx.compose.ui.graphics.Shadow(
                            color = NeonGreen,
                            blurRadius = 20f
                        )
                    )
                )
                Text(
                    text = "Hub",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = NeonPink,
                        shadow = androidx.compose.ui.graphics.Shadow(
                            color = NeonPink,
                            blurRadius = 20f
                        )
                    )
                )
            }
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = NeonText.copy(alpha = 0.7f)
            )
        }

        // Right: Favorite Button
        Box(
            modifier = Modifier
                .size(48.dp)
                .coloredGlow(if (isFavorite) NeonPink else NeonGreen, RoundedCornerShape(12.dp), blurRadius = 15f)
                .background(NeonSurface.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .border(1.dp, if (isFavorite) NeonPink.copy(alpha = 0.5f) else NeonGreen.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = androidx.compose.foundation.LocalIndication.current,
                    onClick = onFavoriteClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "Favorite",
                tint = if (isFavorite) NeonPink else NeonGreen
            )
        }
    }
}
