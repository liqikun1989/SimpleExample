package com.cw.simple.example.app.net

import android.content.Context
import com.alibaba.fastjson.support.retrofit.Retrofit2ConverterFactory
import com.cw.simple.example.app.net.interceptor.LoggingInterceptor
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class RetrofitServer {
    companion object {
        const val TAG = "Retrofit"

        private lateinit var retrofit: Retrofit
        private lateinit var okHttpClient: OkHttpClient
        private lateinit var cacheControl: CacheControl
        private var interceptors: MutableList<Interceptor> = mutableListOf()

        fun init(context: Context, baseUrl: String, interceptors: MutableList<Interceptor> = mutableListOf()) {
            this.interceptors = interceptors
            retrofit = if (Companion::retrofit.isInitialized) {
                retrofit
            } else {
                Retrofit.Builder().baseUrl(baseUrl)
                    .addConverterFactory(Retrofit2ConverterFactory())
                    .client(getUnsafeOkHttpClient(context))
                    .build()
            }
        }

        fun getRetrofit(): Retrofit {
            return retrofit
        }

        fun getCacheControl(): CacheControl {
            cacheControl = if (Companion::cacheControl.isInitialized) {
                cacheControl
            } else {
                CacheControl.Builder().maxAge(5, TimeUnit.MINUTES).build()
            }
            return cacheControl
        }

        private fun getUnsafeOkHttpClient(context: Context): OkHttpClient {
            if (Companion::okHttpClient.isInitialized) {
                return okHttpClient
            } else {
                // Create a trust manager that does not validate certificate chains
                val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                    override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
                    }

                    override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
                    }

                    override fun getAcceptedIssuers() = arrayOf<X509Certificate>()
                })

                // Install the all-trusting trust manager
                val sslContext = SSLContext.getInstance("SSL")
                sslContext.init(null, trustAllCerts, java.security.SecureRandom())
                // Create an ssl socket factory with our all-trusting manager
                val sslSocketFactory = sslContext.socketFactory
                okHttpClient = OkHttpClient.Builder().apply {
                    sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                    hostnameVerifier(HostnameVerifier { _, _ -> true })
                    interceptors().addAll(interceptors)
                    addInterceptor(LoggingInterceptor())
                    cache(Cache(context.applicationContext.getDir("okhttpCache", Context.MODE_PRIVATE), 5 * 1024 * 1024))
                }.build()

                return okHttpClient
            }
        }
    }
}