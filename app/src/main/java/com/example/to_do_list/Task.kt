package com.example.to_do_list

data class Task(
    val id: Int,
    val title: String,
    val category: String,
    var isCompleted: Boolean = false
)
