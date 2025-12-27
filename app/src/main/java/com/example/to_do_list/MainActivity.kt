package com.example.to_do_list

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.to_do_list.ui.*
import com.example.to_do_list.ui.theme.VakiTheme
import kotlinx.coroutines.launch

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
    
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            VakiDrawerContent()
        },
        gesturesEnabled = true
    ) {
        Scaffold(
            floatingActionButton = {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        ExtendedFloatingActionButton(
                            onClick = { /* Edit Task Logic */ },
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
                        containerColor = Color(0xFFFF9800),
                        contentColor = Color.White,
                        shape = CircleShape,
                        modifier = Modifier.size(64.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Task", modifier = Modifier.size(32.dp))
                    }
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFE0F7FA),
                            Color.White,
                            Color(0xFFF3E5F5)
                        )
                    ))
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                // Top Bar with Menu Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    IconButton(
                        onClick = { 
                            scope.launch { 
                                if (drawerState.isClosed) drawerState.open() else drawerState.close()
                            } 
                        },
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = Color.Black
                        )
                    }
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
}
