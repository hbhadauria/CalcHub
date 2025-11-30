package com.hsb.calchub.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hsb.calchub.ui.theme.NeonGreen
import com.hsb.calchub.ui.theme.NeonPink
import com.hsb.calchub.ui.theme.NeonSurface
import com.hsb.calchub.ui.theme.NeonText

@Composable
fun NeonCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    borderColor: Color = NeonGreen,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(16.dp)
    val borderBrush = Brush.verticalGradient(
        colors = listOf(borderColor, borderColor.copy(alpha = 0.2f))
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(8.dp, shape, ambientColor = borderColor, spotColor = borderColor)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        shape = shape,
        color = NeonSurface.copy(alpha = 0.9f), // Glassmorphism base
        border = BorderStroke(1.dp, borderBrush)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
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
                    .background(NeonSurface, RoundedCornerShape(24.dp))
                    .border(1.dp, NeonPink.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = NeonText.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box {
                    if (query.isEmpty()) {
                        Text("Search Calculators", color = NeonText.copy(alpha = 0.5f))
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
        contentColor = NeonGreen
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = selectedItem == 0,
            onClick = { onItemSelected(0) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = NeonGreen,
                selectedTextColor = NeonGreen,
                indicatorColor = NeonGreen.copy(alpha = 0.2f),
                unselectedIconColor = NeonText.copy(alpha = 0.5f),
                unselectedTextColor = NeonText.copy(alpha = 0.5f)
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Favorite, contentDescription = "Saved") },
            label = { Text("Saved") },
            selected = selectedItem == 1,
            onClick = { onItemSelected(1) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = NeonPink,
                selectedTextColor = NeonPink,
                indicatorColor = NeonPink.copy(alpha = 0.2f),
                unselectedIconColor = NeonText.copy(alpha = 0.5f),
                unselectedTextColor = NeonText.copy(alpha = 0.5f)
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Settings, contentDescription = "Tools") },
            label = { Text("Tools") },
            selected = selectedItem == 2,
            onClick = { onItemSelected(2) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = NeonGreen,
                selectedTextColor = NeonGreen,
                indicatorColor = NeonGreen.copy(alpha = 0.2f),
                unselectedIconColor = NeonText.copy(alpha = 0.5f),
                unselectedTextColor = NeonText.copy(alpha = 0.5f)
            )
        )
    }
}
