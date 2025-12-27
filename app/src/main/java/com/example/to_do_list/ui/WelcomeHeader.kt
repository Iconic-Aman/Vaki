package com.example.to_do_list.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun WelcomeHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                "Hello, Aman!",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                "You have 4 focus on today",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
            )
        }
        Surface(
            modifier = Modifier.size(56.dp),
            shape = CircleShape,
            color = Color(0xFFE0F7FA)
        ) {
            Icon(
                Icons.Default.Person,
                contentDescription = "Profile",
                modifier = Modifier.padding(12.dp),
                tint = Color(0xFF00BCD4)
            )
        }
    }
}