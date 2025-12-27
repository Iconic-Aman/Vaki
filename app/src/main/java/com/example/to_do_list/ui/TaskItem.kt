package com.example.to_do_list.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.to_do_list.Task

@Composable
fun TaskItem(task: Task, onToggle: () -> Unit, onDelete: () -> Unit) {
    val backgroundColor by animateColorAsState(
        if (task.isCompleted) Color(0xFFF1F8E9) else Color.White, label = "bgColor"
    )
    val textColor = if (task.isCompleted) Color.Gray else Color.Black

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Category Tag
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp),
                shape = RoundedCornerShape(6.dp),
                color = getCategoryColor(task.category).copy(alpha = 0.2f)
            ) {
                Text(
                    text = task.category,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = getCategoryColor(task.category),
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Custom Checkbox
                Surface(
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onToggle() }
                        .clip(CircleShape),
                    shape = CircleShape,
                    color = if (task.isCompleted) Color(0xFF4CAF50) else Color.Transparent,
                    border = if (!task.isCompleted) androidx.compose.foundation.BorderStroke(2.dp, Color.Gray) else null
                ) {
                    if (task.isCompleted) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium,
                            color = textColor,
                            textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null
                        )
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.LightGray
                    )
                }
            }
        }
    }
}

fun getCategoryColor(category: String): Color {
    return when(category.lowercase()) {
        "work" -> Color(0xFF2196F3)
        "personal" -> Color(0xFFE91E63)
        "design" -> Color(0xFF9C27B0)
        else -> Color(0xFFFF9800)
    }
}