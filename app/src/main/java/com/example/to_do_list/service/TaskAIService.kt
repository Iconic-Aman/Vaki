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
        val key = com.example.to_do_list.network.OpenRouterConfig.getActiveKey()
        Log.d("VakiDebug", "LLM Processing: '$voskText' | KeyPresent: ${key.isNotEmpty()} (Len: ${key.length})")
        
        // precise free models
        val fallbackModels = listOf(
            "google/gemini-2.0-flash-exp:free",
            "meta-llama/llama-3.2-3b-instruct:free",
            "mistralai/mistral-7b-instruct:free"
        )

        // Strict prompt to ensure output matches VakiBrain's expected intents if possible
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
            Log.d("VakiDebug", "LLM: Sending request to OpenRouter...")
            val response = api.getCompletion(request = request)
            
            if (response.isSuccessful) {
                val content = response.body()?.choices?.firstOrNull()?.message?.content?.trim()
                Log.d("VakiDebug", "LLM Response: $content")
                content
            } else {
                val errorBody = response.errorBody()?.string() ?: "No error body"
                Log.e("VakiDebug", "LLM API Error: ${response.code()} - $errorBody")
                null // Trigger fallback
            }
        } catch (e: Exception) {
            Log.e("VakiDebug", "LLM Exception: ${e.javaClass.simpleName} - ${e.localizedMessage}")
            e.printStackTrace()
            null // Trigger fallback
        }
    }
}
