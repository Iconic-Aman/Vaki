package com.example.to_do_list.network

import com.google.gson.annotations.SerializedName

// Request Data Models
data class ChatRequest(
    val models: List<String>,       // List for automatic fallback (OpenRouter specific)
    val messages: List<Message>,
    val route: String = "fallback"  // Tells OpenRouter to try models in order
)

data class Message(
    val role: String, 
    val content: String
)

// Response Data Models
data class ChatResponse(
    val id: String,
    val model: String, // The model that actually handled the request
    val choices: List<Choice>
)

data class Choice(
    val message: Message
)
