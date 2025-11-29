package com.hsb.calchub.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hsb.calchub.ui.theme.NeoBlack
import com.hsb.calchub.ui.theme.NeoWhite

@Composable
fun NeoPopButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    shadowColor: Color = NeoBlack, // Or NeoWhite if on dark background with light button
    borderColor: Color = NeoBlack,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val shadowOffset = 4.dp
    val pressOffset by animateDpAsState(if (isPressed) shadowOffset else 0.dp, label = "press")
    val contentOffset by animateDpAsState(if (isPressed) shadowOffset else 0.dp, label = "content")

    Box(
        modifier = modifier
            .height(56.dp) // Standard height
            .clickable(
                interactionSource = interactionSource,
                indication = null, // Custom indication handled by offset
                enabled = enabled,
                onClick = onClick
            )
    ) {
        // Shadow Layer (Static)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp) // Slightly smaller or same? Usually same size but offset.
                // Actually for hard shadow, we usually place a box behind.
                // Let's do: Shadow is at (4,4), Content is at (0,0).
                // When pressed: Content moves to (4,4).
                .offset(x = shadowOffset, y = shadowOffset)
                .background(shadowColor)
                .border(BorderStroke(1.dp, borderColor))
        )

        // Content Layer (Moves)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .offset(x = contentOffset, y = contentOffset)
                .background(containerColor)
                .border(BorderStroke(1.dp, borderColor))
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text.uppercase(),
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    letterSpacing = 1.sp,
                    color = contentColor
                )
            )
        }
    }
}

@Composable
fun NeoPopCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    borderColor: Color = MaterialTheme.colorScheme.outline,
    shadowColor: Color = NeoWhite, // White shadow on dark background for visibility
    content: @Composable () -> Unit
) {
    val shadowOffset = 4.dp
    
    Box(modifier = modifier.padding(bottom = shadowOffset, end = shadowOffset)) {
        // Shadow
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = shadowOffset, y = shadowOffset)
                .background(shadowColor)
                .border(BorderStroke(1.dp, borderColor))
        )
        
        // Content
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(backgroundColor)
                .border(BorderStroke(1.dp, borderColor))
                .padding(16.dp)
        ) {
            content()
        }
    }
}

@Composable
fun NeoPopInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String = "",
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    borderColor: Color = MaterialTheme.colorScheme.outline
) {
    Column(modifier = modifier) {
        if (label != null) {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
        
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            ),
            keyboardOptions = keyboardOptions,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .border(BorderStroke(1.dp, borderColor))
                        .padding(16.dp)
                ) {
                    if (value.isEmpty() && placeholder.isNotEmpty()) {
                        Text(
                            text = placeholder,
                            style = TextStyle(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                        )
                    }
                    innerTextField()
                }
            }
        )
    }
}
