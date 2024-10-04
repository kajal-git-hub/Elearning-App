package xyz.penpencil.competishun.utils

import android.util.Log
import com.apollographql.apollo3.api.http.HttpRequest
import com.apollographql.apollo3.api.http.HttpResponse
import com.apollographql.apollo3.network.http.HttpInterceptor
import com.apollographql.apollo3.network.http.HttpInterceptorChain
import javax.inject.Inject


class CustomLoggingInterceptor @Inject constructor() : HttpInterceptor {
    override suspend fun intercept(request: HttpRequest, chain: HttpInterceptorChain): HttpResponse {
        // Log request details
        Log.d("GraphQL Request", request.url)

        val response = chain.proceed(request)

        // Log response details
        Log.d("GraphQL Response", response.body.toString())

        return response
    }
}
