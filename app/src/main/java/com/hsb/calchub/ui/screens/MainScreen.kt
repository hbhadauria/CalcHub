package com.hsb.calchub.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.hsb.calchub.ui.components.NeonNavBar
import com.hsb.calchub.ui.theme.NeonBackground

@Composable
fun MainScreen(onCalculatorClick: (String) -> Unit) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NeonNavBar(
                selectedItem = selectedTab,
                onItemSelected = { selectedTab = it }
            )
        },
        containerColor = NeonBackground
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                0 -> HomeScreen(onCalculatorClick = onCalculatorClick)
                1 -> SavedScreen(onCalculatorClick = onCalculatorClick)
                2 -> ToolsScreen(onCalculatorClick = onCalculatorClick)
            }
        }
    }
}
