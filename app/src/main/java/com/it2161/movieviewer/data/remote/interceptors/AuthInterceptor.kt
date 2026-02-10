package com.it2161.movieviewer.data.remote.interceptors

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer 7556508b4ea1b0b755fdde00504919b7")
            .addHeader("accept", "application/json")
            .build()
        return chain.proceed(request)
    }
}
