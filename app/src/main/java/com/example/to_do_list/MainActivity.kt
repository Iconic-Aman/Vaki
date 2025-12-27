package com.example.to_do_list

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.to_do_list.ui.theme.VakiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VakiTheme {
                FocusFlowApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusFlowApp(viewModel: TaskViewModel = viewModel()) {
    var showSheet by remember { mutableStateOf(false) }
    val tasks = viewModel.tasks
    val completedCount = tasks.count { it.isCompleted }
    val totalCount = tasks.size

    Scaffold(
        floatingActionButton = {
            // Floating Pill Buttons and FAB
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Formatting "Pill" buttons request as small FloatingActionButtons or extended ones
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    ExtendedFloatingActionButton(
                        onClick = { /* Edit Task Logic - Placeholder */ },
                        containerColor = Color.White,
                        contentColor = Color.Black,
                        modifier = Modifier.height(40.dp)
                    ) {
                        Text("Edit Task", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    ExtendedFloatingActionButton(
                        onClick = { showSheet = true },
                        containerColor = Color.White,
                        contentColor = Color.Black,
                        modifier = Modifier.height(40.dp)
                    ) {
                        Text("New Task", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                FloatingActionButton(
                    onClick = { showSheet = true },
                    containerColor = Color(0xFFFF9800), // Orange FAB
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Task", modifier = Modifier.size(32.dp))
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE0F7FA), // Soft Cyan
                        Color.White,
                        Color(0xFFF3E5F5)  // Light Purple
                    )
                ))
                .padding(padding)
                .padding(16.dp)
        ) {
            // Top Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Lock, // Or Home
                    contentDescription = "Lock",
                    modifier = Modifier.align(Alignment.CenterStart),
                    tint = Color.Gray
                )
                Text(
                    "Vaki",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 24.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            WelcomeHeader()
            
            Spacer(modifier = Modifier.height(24.dp))
            
            ProgressCard(completed = completedCount, total = totalCount)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                "Today's Tasks",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.DarkGray
                ),
                modifier = Modifier.padding(start = 8.dp, bottom = 12.dp)
            )
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(tasks, key = { it.id }) { task ->
                    TaskItem(
                        task = task,
                        onToggle = { viewModel.toggleTaskCompletion(task) },
                        onDelete = { viewModel.removeTask(task) }
                    )
                }
            }
        }
        
        if (showSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSheet = false },
                containerColor = Color.White
            ) {
                AddTaskContent(
                    onAddTask = { title, category ->
                        viewModel.addTask(title, category)
                        showSheet = false
                    }
                )
            }
        }
    }
}

@Composable
fun WelcomeHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                "Hello, User!",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                "You have 4 focus on today", // Hardcoded or dynamic could be implemented
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
            // Ideally an Image goes here
        }
    }
}

@Composable
fun ProgressCard(completed: Int, total: Int) {
    val progress = if (total > 0) completed.toFloat() / total else 0f
    
    // Glassmorphism effect
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = Color(0x4000BCD4)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Daily Progress",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50) // Green
                    )
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape),
                color = Color(0xFF4CAF50), // Green
                trackColor = Color(0xFFE0E0E0),
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                 Text(
                    "$completed Completed | ${total - completed} Remaining",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = Color.Gray,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }
}

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
                            textDecoration = if (task.isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = task.category,
                        style = MaterialTheme.typography.labelMedium.copy(color = Color.Gray.copy(alpha=0.0f)) // Hide original text category to not duplicate
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

@Composable
fun AddTaskContent(onAddTask: (String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .navigationBarsPadding() // Handle edge-to-edge
            .imePadding() // Handle keyboard
    ) {
        Text(
            "Add New Task",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Task Title") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = category,
            onValueChange = { category = it },
            label = { Text("Category (e.g., Work, Personal, Design)") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (title.isNotBlank()) {
                    onAddTask(title, category.ifBlank { "General" })
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Create Task", fontSize = 16.sp)
        }
    }
}
