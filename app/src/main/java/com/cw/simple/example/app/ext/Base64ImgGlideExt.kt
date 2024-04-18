package com.cw.simple.example.app.ext

import android.util.Base64
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.bumptech.glide.signature.ObjectKey
import com.cw.simple.example.app.net.interceptor.LoggingInterceptor
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.nio.ByteBuffer


class Base64ImgGlideExt {

}

class MyBase64ModelLoader : ModelLoader<String, ByteBuffer> {
    override fun buildLoadData(model: String, width: Int, height: Int, options: Options): ModelLoader.LoadData<ByteBuffer> {
        return ModelLoader.LoadData(ObjectKey(model), Base64DataFetcher(model))
    }

    override fun handles(model: String): Boolean {
        return model.startsWith("https") && (model.endsWith("png") || model.endsWith("jpg"))
    }
}

class Base64DataFetcher(private val model: String) : DataFetcher<ByteBuffer> {
    private var job: Job? = null
    private var okHttpClient: OkHttpClient = OkHttpClient().newBuilder().apply {
        addInterceptor(GlideHeaderInterceptor())
        addInterceptor(LoggingInterceptor())
    }.build()

    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in ByteBuffer>) {
        job = CoroutineScope(Dispatchers.IO + CoroutineExceptionHandler { coroutineContext, throwable ->
            throwable.printStackTrace()
        }).launch {
            val request: Request = Request.Builder().url(model).build()
            okHttpClient.newCall(request).execute().apply {
                if (code != 200) {
                    return@apply
                }
                body?.apply {
                    val readString = Base64.encodeToString(bytes(), Base64.DEFAULT)
//                Log.i("LQK", "loadData: readString = $readString")
                    val data = Base64.decode(readString, Base64.DEFAULT)
                    val byteBuffer = ByteBuffer.wrap(data)
                    callback.onDataReady(byteBuffer)
                }
            }
        }
    }

    private fun getBase64SectionOfModel(base64Str: String): String {
        // See https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/Data_URIs.
        val startOfBase64Section = base64Str.indexOf(',')
        return base64Str.substring(startOfBase64Section + 1)
    }

    override fun cleanup() {

    }

    override fun cancel() {
        job?.cancel()
    }

    override fun getDataClass(): Class<ByteBuffer> = ByteBuffer::class.java

    override fun getDataSource(): DataSource = DataSource.LOCAL
}

class GlideHeaderInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
//        builder.addHeader("token", CacheUtil.getToken())
//        builder.addHeader("User-Agent", "android-xiaodu")
        return chain.proceed(builder.build())
    }
}

class Base64ModelLoaderFactory : ModelLoaderFactory<String, ByteBuffer> {
    override fun build(unused: MultiModelLoaderFactory): ModelLoader<String, ByteBuffer> {
        return MyBase64ModelLoader()
    }

    override fun teardown() {
        // Do nothing.
    }
}