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
        
        // Tested & Confirmed Available Models (Jan 2026)
        val fallbackModels = listOf(
            "google/gemma-3-4b-it:free",              // Small & New (4B)
            "meta-llama/llama-3.2-3b-instruct:free",  // Reliable (3B)
            "mistralai/mistral-7b-instruct:free"      // Trusted Fallback (7B)
        )

        // Context variables
        val sdf = java.text.SimpleDateFormat("EEEE, dd MMMM yyyy, h:mm a", java.util.Locale.getDefault())
        val currentTime = sdf.format(java.util.Date())

        // Strict prompt to ensure output matches VakiBrain's expected intents if possible
        val prompt = """
            You are a task manager voice assistant.
            
            Context:
            - Current Time: $currentTime
            - User Location: India (IST)
            - User Name: Aman
            
            The user said: "$voskText". 
            
            Analyze the intent strictly. you will reply the user based on their question , if they ask about task then you'll follow TASK_RULES below 
            
            TASK Rules:
            1. If they want to ADD a task, reply ONLY with: i have added task [exact task content]
            2. If they want to DELETE a task, reply ONLY with: i have deleted task [task keyword]
            3. If they want to COUNT tasks, reply ONLY with:  you have COUNT tasks
            4. If they want to LIST tasks, reply ONLY with: i have LIST tasks
            5. If the user asks about time, weather, general knowledge, or random chat: reply with: answer [response], thank you.
            6. If you are unable to answer or don't know: reply with: answer Sorry, I don't know that.
            7. If the user says "No", "Stop", "That's all", "Done": reply ONLY with: STOP
            
            Examples:   
            - "Buy milk" -> i have added task Buy milk
            - "Remove milk" -> i have deleted task milk
            - "How many tasks?" -> you have total COUNT tasks
            - "What do I have to do?" -> i have LIST tasks
            - "That's all" -> STOP
            - "No thanks" -> STOP
            - "What is the time?" -> answer It is currently 5 PM, thank you.
            - "Who is the president?" -> answer The president is [Name], thank you.
            - "Hello" -> answer Hello there! How can I help?
            
            Reply with ONE alias from above. Do not explain.
        """.trimIndent()

        val request = ChatRequest(
            models = fallbackModels,
            messages = listOf(Message("user", prompt))
        )

        return try {
            Log.d("VakiDebug", "LLM: Sending request to OpenRouter...")
            val response = api.getCompletion(request = request)
            
            if (response.isSuccessful) {
                val responseBody = response.body()
                val content = responseBody?.choices?.firstOrNull()?.message?.content?.trim()
                val modelUsed = responseBody?.model ?: "Unknown Model"
                Log.d("VakiDebug", "LLM Success: $content (Model: $modelUsed)")
                content
            } else {
                val code = response.code()
                val errorBody = response.errorBody()?.string() ?: "No error body"
                if (code == 429) {
                    Log.w("VakiDebug", "LLM Rate Limit Reached (429). Models attempted: $fallbackModels. Falling back to VakiBrain.")
                } else {
                    Log.e("VakiDebug", "LLM API Error: $code - $errorBody. Models attempted: $fallbackModels")
                }
                null // Trigger fallback
            }
        } catch (e: Exception) {
            // Suppress network errors for offline mode
            if (e is java.net.UnknownHostException || e is java.io.IOException) {
                Log.d("VakiDebug", "LLM Network unavailable (Offline): Falling back to VakiBrain.")
            } else {
                Log.e("VakiDebug", "LLM Exception: ${e.javaClass.simpleName} - ${e.localizedMessage}")
                e.printStackTrace()
            }
            null // Trigger fallback
        }
    }
}
