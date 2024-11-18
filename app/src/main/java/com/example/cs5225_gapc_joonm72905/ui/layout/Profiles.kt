package com.example.cs5225_gapc_joonm72905.ui.layout

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter

@Composable
fun GreetingsSection(
    greetings: String,
    content1: String,
    content2: String,
    imgUrl: String,
    isOnline: Boolean
) {
    Text(
        text = greetings,
        fontSize = 24.sp,
        style = MaterialTheme.typography.headlineSmall.copy(
            fontWeight = FontWeight.Bold
        ),
        color = MaterialTheme.colorScheme.secondary,
        modifier = Modifier.padding(bottom = 16.dp)
    )
    ProfileSection(imgUrl, isOnline)
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = content1,
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurface
    )
    Text(
        text = content2,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        textAlign = TextAlign.Center
    )
}

/** Change to url string later */
@Composable
private fun ProfileSection(imgUrl: String, isOnline: Boolean) {
    Box(
        contentAlignment = Alignment.BottomEnd,
        modifier = Modifier.size(100.dp)
    ) {
        Image(
            painter = rememberAsyncImagePainter(imgUrl),
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        OnlineOfflineIndicator(isOnline = isOnline, 4.dp)
    }
}

