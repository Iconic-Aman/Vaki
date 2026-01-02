package com.example.to_do_list.network

import com.example.to_do_list.BuildConfig

object OpenRouterConfig {
    // List of keys. We start with the one from BuildConfig.
    // Users can add more here if they want rotation.
    private val apiKeys = mutableListOf<String>().apply {
        if (BuildConfig.OPENROUTER_API_KEY.isNotEmpty()) {
            add(BuildConfig.OPENROUTER_API_KEY)
        }
    }
    
    private var currentKeyIndex = 0

    fun getActiveKey(): String {
        if (apiKeys.isEmpty()) return ""
        return apiKeys[currentKeyIndex]
    }

    // Move to the next key when one fails
    fun rotateKey(): Boolean {
        if (apiKeys.isNotEmpty() && currentKeyIndex < apiKeys.size - 1) {
            currentKeyIndex++
            return true // Key rotated successfully
        }
        return false // No more keys left
    }
    
    fun hasKeys(): Boolean = apiKeys.isNotEmpty()
}
