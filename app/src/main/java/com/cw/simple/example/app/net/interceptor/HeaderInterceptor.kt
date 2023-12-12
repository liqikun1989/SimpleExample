package com.cw.simple.example.app.net.interceptor

import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val builder = original.newBuilder().apply {
//            addHeader()
        }
        val request = builder.build()
        return chain.proceed(request)
    }
}