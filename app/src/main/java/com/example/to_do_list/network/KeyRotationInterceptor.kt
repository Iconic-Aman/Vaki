package com.example.to_do_list.network

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class KeyRotationInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val key = OpenRouterConfig.getActiveKey()
        
        // 1. Add headers (Auth, Referer, Title)
        val requestWithHeaders = originalRequest.newBuilder()
            .header("Authorization", "Bearer $key")
            .header("HTTP-Referer", "https://github.com/Start-Up-Ed/Vaki") // Placeholder
            .header("X-Title", "Vaki Voice Assistant")
            .build()

        var response = chain.proceed(requestWithHeaders)

        // 2. Check if we hit a Rate Limit (429)
        if (response.code == 429) {
            synchronized(this) {
                // Try rotating to a new key
                if (OpenRouterConfig.rotateKey()) {
                    response.close() // Close the failed response
                    
                    // 3. Create a new request with the NEW key
                    val newKey = OpenRouterConfig.getActiveKey()
                    val newRequest = originalRequest.newBuilder()
                        .header("Authorization", "Bearer $newKey")
                        .header("HTTP-Referer", "https://github.com/Start-Up-Ed/Vaki")
                        .header("X-Title", "Vaki Voice Assistant")
                        .build()
                    
                    // 4. Retry the call
                    response = chain.proceed(newRequest)
                }
            }
        }
        return response
    }
}
