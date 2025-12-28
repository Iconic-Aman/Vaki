package com.example.to_do_list

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.to_do_list.ui.*
import com.example.to_do_list.ui.theme.VakiTheme
import kotlinx.coroutines.launch
import java.util.*

class MainActivity : ComponentActivity() {
    private lateinit var vakiVoice: VakiVoiceManager
    private lateinit var vakiSpeechRecognizer: VakiSpeechRecognizer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vakiVoice = VakiVoiceManager(this)
        
        enableEdgeToEdge()
        setContent {
            VakiTheme {
                val viewModel: TaskViewModel = viewModel()
                var isVoiceExpanded by remember { mutableStateOf(false) }
                
                // Initialize Speech Recognizer
                vakiSpeechRecognizer = remember {
                    VakiSpeechRecognizer(
                        context = this,
                        onResult = { result ->
                            Log.d("VakiDebug", "Recognized: $result")
                            handleVoiceCommand(result, viewModel) {
                                isVoiceExpanded = false
                            }
                        },
                        onError = { error ->
                            Log.e("VakiDebug", "Speech Error Code: $error")
                            // Fix: Ensure the button shrinks on error
                            isVoiceExpanded = false
                        }
                    )
                }

                FocusFlowApp(
                    vakiVoice = vakiVoice,
                    vakiSpeechRecognizer = vakiSpeechRecognizer,
                    viewModel = viewModel,
                    isVoiceExpanded = isVoiceExpanded,
                    onVoiceExpandedChange = { isVoiceExpanded = it }
                )
            }
        }
    }

    private fun handleVoiceCommand(command: String, viewModel: TaskViewModel, onComplete: () -> Unit) {
        val lowerCommand = command.lowercase().trim()
        Log.d("VakiDebug", "Processing command: $lowerCommand")
        
        // Flexible parsing for "add" or "add task"
        val taskTitle = when {
            lowerCommand.startsWith("add task") -> lowerCommand.removePrefix("add task").trim()
            lowerCommand.startsWith("add") -> lowerCommand.removePrefix("add").trim()
            else -> null
        }

        if (taskTitle != null) {
            if (taskTitle.isNotEmpty()) {
                val capitalizedTitle = taskTitle.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                viewModel.addTask(capitalizedTitle, "Voice")
                vakiVoice.speak("Got it! I've added your task $taskTitle. Thank you!") {
                    onComplete()
                }
                Log.d("VakiDebug", "Action: Added task '$capitalizedTitle'")
            } else {
                vakiVoice.speak("What task would you like me to add?")
                Log.d("VakiDebug", "Action: Prompted for task title")
            }
        } else {
            vakiVoice.speak("I heard you say $command, but I'm not sure how to do that yet. Try saying Add followed by your task.") {
                onComplete()
            }
            Log.d("VakiDebug", "Action: Unknown command")
        }
    }

    override fun onDestroy() {
        vakiVoice.shutDown()
        vakiSpeechRecognizer.destroy()
        super.onDestroy()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusFlowApp(
    vakiVoice: VakiVoiceManager,
    vakiSpeechRecognizer: VakiSpeechRecognizer,
    viewModel: TaskViewModel,
    isVoiceExpanded: Boolean,
    onVoiceExpandedChange: (Boolean) -> Unit
) {
    val context = LocalContext.current
    var showSheet by remember { mutableStateOf(false) }
    val tasks = viewModel.tasks
    val completedCount = tasks.count { it.isCompleted }
    val totalCount = tasks.size
    
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Permission Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d("VakiDebug", "Microphone permission granted")
        } else {
            Log.e("VakiDebug", "Microphone permission denied")
        }
    }

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

                    VakiVoiceButton(
                        isExpanded = isVoiceExpanded,
                        onClick = { 
                            // Check permission first
                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                return@VakiVoiceButton
                            }

                            val nextState = !isVoiceExpanded
                            onVoiceExpandedChange(nextState)
                            if (nextState) {
                                Log.d("VakiDebug", "Starting Voice Session")
                                vakiVoice.speak("Hello Aman, I am listening. How can I help you today?") {
                                    // Start listening only AFTER Vaki finishes greeting
                                    vakiSpeechRecognizer.startListening()
                                    Log.d("VakiDebug", "Mic is now LIVE")
                                }
                            } else {
                                Log.d("VakiDebug", "Ending Voice Session")
                                // Stop everything immediately
                                vakiVoice.stop()
                                vakiSpeechRecognizer.stopListening()
                            }
                        }
                    )
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
                
                WelcomeHeader(taskCount = totalCount)
                
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
