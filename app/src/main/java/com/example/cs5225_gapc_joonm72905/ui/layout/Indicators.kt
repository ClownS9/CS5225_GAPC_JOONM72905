package com.example.cs5225_gapc_joonm72905.ui.layout

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun OnlineOfflineIndicator(isOnline: Boolean, paddingValue: Dp) {
    Icon(
        imageVector = if (isOnline) Icons.Filled.Circle else Icons.Filled.Circle,
        contentDescription = if (isOnline) "Online" else "Offline",
        tint = if (isOnline) MaterialTheme.colorScheme.inversePrimary else MaterialTheme.colorScheme.inverseSurface,
        modifier = Modifier
            .size(24.dp)
            .padding(paddingValue)
    )
}