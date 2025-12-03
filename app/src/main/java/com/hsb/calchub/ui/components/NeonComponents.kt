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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
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
        containerColor = NeonSurface,
        contentColor = NeonGreen,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(32.dp))
            .border(1.dp, NeonGreen.copy(alpha = 0.3f), RoundedCornerShape(32.dp))
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = selectedItem == 0,
            onClick = { onItemSelected(0) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = NeonGreen,
                selectedTextColor = NeonGreen,
                indicatorColor = NeonGreen.copy(alpha = 0.1f),
                unselectedIconColor = NeonText.copy(alpha = 0.5f),
                unselectedTextColor = NeonText.copy(alpha = 0.5f)
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.FavoriteBorder, contentDescription = "Saved") },
            label = { Text("Saved") },
            selected = selectedItem == 1,
            onClick = { onItemSelected(1) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = NeonPink,
                selectedTextColor = NeonPink,
                indicatorColor = NeonPink.copy(alpha = 0.1f),
                unselectedIconColor = NeonText.copy(alpha = 0.5f),
                unselectedTextColor = NeonText.copy(alpha = 0.5f)
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.Build, contentDescription = "Tools") },
            label = { Text("Tools") },
            selected = selectedItem == 2,
            onClick = { onItemSelected(2) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = NeonGreen,
                selectedTextColor = NeonGreen,
                indicatorColor = NeonGreen.copy(alpha = 0.1f),
                unselectedIconColor = NeonText.copy(alpha = 0.5f),
                unselectedTextColor = NeonText.copy(alpha = 0.5f)
            )
        )
    }
}
