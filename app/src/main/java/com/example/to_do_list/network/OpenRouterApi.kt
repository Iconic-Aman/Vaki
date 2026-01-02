package com.example.to_do_list.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface OpenRouterApi {
    @POST("chat/completions")
    suspend fun getCompletion(
        // Authorization header is handled by Interceptor usually, but we can pass it here or there.
        // We'll rely on the Interceptor for the token to support rotation easier, 
        // OR we can pass it if we want simple stateless logic.
        // Based on the guide, let's keep headers here for clarity or use interceptor.
        // Guide used Interceptor for Auth. Let's stick to that for "Advanced" setup.
        // So we only need body here, maybe Referer/Title if not in interceptor.
        // Let's put Referer/Title in Interceptor too for cleanliness?
        // Actually, let's keep it simple and consistent with the guide's "Final Retrofit Setup".
        @Body request: ChatRequest
    ): Response<ChatResponse>
}
