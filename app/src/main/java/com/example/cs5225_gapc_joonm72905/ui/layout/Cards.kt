package com.example.cs5225_gapc_joonm72905.ui.layout

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter

@Composable
fun Cards(
    imgUrl: String,
    contentTitle: String,
    contentDesc: String,
    checked: Boolean,
    onChange: (Boolean) -> Unit,
    hasToggle: Boolean,
    isOnline: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CardImage(imgUrl, isOnline)
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                CardContent(contentTitle, contentDesc)
            }
            Spacer(modifier = Modifier.width(16.dp))
            if (hasToggle) {
                ToggleButtons(onChange, checked)
            }
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
private fun CardImage(imgUrl: String, isOnline: Boolean) {
    Box(
        contentAlignment = Alignment.BottomEnd,
        modifier = Modifier.size(60.dp)
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = imgUrl),
            contentDescription = "Profile Picture in Card",
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        OnlineOfflineIndicator(isOnline = isOnline, 4.dp)
    }
}

@Composable
private fun CardContent(contentTitle: String, contentDesc: String) {
    Text(
        text = contentTitle,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.secondary,
        modifier = Modifier.padding(bottom = 8.dp)
    )
    Text(
        text = contentDesc,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface
    )
}


