package com.example.to_do_list.service

import android.util.Log
import com.example.to_do_list.network.ChatRequest
import com.example.to_do_list.network.KeyRotationInterceptor
import com.example.to_do_list.network.Message
import com.example.to_do_list.network.OpenRouterApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TaskAIService {

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(KeyRotationInterceptor())
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY 
        })
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://openrouter.ai/api/v1/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(OpenRouterApi::class.java)

    suspend fun processVoskTask(voskText: String): String? {
        Log.d("TaskAIService", "Processing with LLM: $voskText")
        
        // precise free models
        val fallbackModels = listOf(
            "google/gemini-2.0-flash-exp:free",
            "meta-llama/llama-3.2-3b-instruct:free",
            "mistralai/mistral-7b-instruct:free"
        )

        // Strict prompt to ensure output matches VakiBrain's expected intents if possible, 
        // OR we can just return raw "ADD <task>" and let the caller handle it.
        // The plan says: "If they want to add a task, reply with: ADD [task name]."
        val prompt = """
            You are a task manager voice assistant. The user said: "$voskText". 
            
            Analyze the intent:
            1. If they want to ADD a task, reply ONLY with: ADD [exact task content]
            2. If they want to DELETE a task, reply ONLY with: DELETE [task keyword]
            3. If they want to COUNT tasks, reply ONLY with: COUNT
            4. If they want to LIST tasks, reply ONLY with: LIST
            5. If it's none of the above, reply with: UNKNOWN
            
            Do not add any other text, punctuation, or chat.
        """.trimIndent()

        val request = ChatRequest(
            models = fallbackModels,
            messages = listOf(Message("user", prompt))
        )

        return try {
            val response = api.getCompletion(request = request)
            
            if (response.isSuccessful) {
                val content = response.body()?.choices?.firstOrNull()?.message?.content?.trim()
                Log.d("TaskAIService", "LLM Response: $content")
                
                // If LLM explicitly says UNKNOWN, we might want to return null to let local regex try 
                // (though regex is likely simpler, so LLM should be smarter). 
                // Actually, if LLM fails to understand, VakiBrain (regex) definitely won't understand complex stuff.
                // But VakiBrain is the fallback for *Technical* failures mostly.
                // However, user said "fallback... if we're offline".
                // So if LLM returns "UNKNOWN", it means it worked but didn't match. 
                // We should probably return "UNKNOWN" string or null?
                // VakiBrain returns VakiIntent.Unknown.
                // Let's return the content string.
                content
            } else {
                Log.e("TaskAIService", "API Error: ${response.code()}")
                null // Trigger fallback
            }
        } catch (e: Exception) {
            Log.e("TaskAIService", "Network/Exception: ${e.localizedMessage}")
            null // Trigger fallback
        }
    }
}
