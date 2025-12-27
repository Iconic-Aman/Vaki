package com.example.to_do_list.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun VakiVoiceButton(
    isExpanded: Boolean,
    onClick: () -> Unit
) {
    val width by animateDpAsState(targetValue = if (isExpanded) 180.dp else 64.dp, label = "width")

    Surface(
        modifier = Modifier
            .height(64.dp)
            .width(width)
            .clickable { onClick() }
            .animateContentSize(),
        color = Color(0xFFFF9800), // Original orange color
        shape = CircleShape,
        shadowElevation = 6.dp
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = if (isExpanded) Arrangement.Start else Arrangement.Center
        ) {
            Box(
                modifier = Modifier.size(64.dp),
                contentAlignment = Alignment.Center
            ) {
                // Using Face icon as a fallback for Mic if extended icons aren't available
                Icon(
                    imageVector = Icons.Default.Face,
                    contentDescription = "Voice Assistant",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
            if (isExpanded) {
                Text(
                    text = "Vaki is listening...",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    modifier = Modifier.padding(end = 20.dp)
                )
            }
        }
    }
}
