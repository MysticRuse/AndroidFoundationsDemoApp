package com.sample.android.composebasics.networkingdatapersistence.okhttp

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

interface TokenProvider {
    fun getToken(): String?
    fun refreshToken(): String?
}

class AuthInterceptor(
    private val tokenProvider: TokenProvider
) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = tokenProvider.getToken()

        // 1. Add Authorization header to every request
        val requestWithToken = if (token != null) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }

        var response = chain.proceed(requestWithToken)

        // 2. Handle 401 Unauthorized
        if (response.code == 401) {
            synchronized(this) {
                // Double check token to avoid multiple refreshes
                val currentToken = tokenProvider.getToken()
                
                // Refresh token (only once)
                val newToken = tokenProvider.refreshToken()
                
                if (newToken != null && newToken != currentToken) {
                    // Close the 401 response before retrying
                    response.close()

                    // 3. Retry the request with the new token
                    val newRequest = originalRequest.newBuilder()
                        .header("Authorization", "Bearer $newToken")
                        .build()
                    
                    response = chain.proceed(newRequest)
                }
            }
        }

        return response
    }
}
