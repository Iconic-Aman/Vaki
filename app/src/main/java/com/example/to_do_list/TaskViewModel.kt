package com.example.to_do_list

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

class TaskViewModel : ViewModel() {
    private val _tasks = mutableStateListOf<Task>()
    val tasks: List<Task> get() = _tasks

    init {
        // Add sample tasks
        _tasks.add(Task(1, "Plan project", "Work", true))
        _tasks.add(Task(2, "Design UI", "Work", false))
        _tasks.add(Task(3, "Implement logic", "Coding", false))
    }

    fun addTask(title: String, category: String) {
        val newId = if (_tasks.isEmpty()) 1 else _tasks.maxOf { it.id } + 1
        _tasks.add(Task(newId, title, category))
    }

    fun toggleTaskCompletion(task: Task) {
        val index = _tasks.indexOfFirst { it.id == task.id }
        if (index != -1) {
            val currentTask = _tasks[index]
            _tasks[index] = currentTask.copy(isCompleted = !currentTask.isCompleted)
        }
    }

    fun removeTask(task: Task) {
        _tasks.remove(task)
    }
}
