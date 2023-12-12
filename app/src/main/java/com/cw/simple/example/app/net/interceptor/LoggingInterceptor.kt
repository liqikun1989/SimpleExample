package com.cw.simple.example.app.net.interceptor

import android.text.TextUtils
import com.cw.simple.example.app.log.KLog
import com.cw.simple.example.app.utils.StreamUtils
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Response
import okhttp3.ResponseBody
import okio.Buffer
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.EOFException
import java.io.InputStream
import java.net.URLDecoder
import java.nio.charset.Charset
import java.util.zip.GZIPInputStream

class LoggingInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val body = originalRequest.body
        val stringBuilder = StringBuilder("Request: \n")
        val url = URLDecoder.decode(originalRequest.url.toString(), "UTF-8")
        stringBuilder.append("URL: ").append(url).append("\n")
        stringBuilder.append("Method: ").append(originalRequest.method).append("\n")
        stringBuilder.append("Headers: \n")
        for ((index, header) in originalRequest.headers.withIndex()) {
            stringBuilder.append("    ").append("${header.first}: ${header.second}")
            if (index != originalRequest.headers.size - 1) {
                stringBuilder.append("\n")
            }
        }
        stringBuilder.append("\n")
        var charset = Charset.forName("UTF-8")
        if (body != null) {
            val contentTypeObj = body.contentType()
            val sendDataLen = "sendDataLen: "
            if (contentTypeObj != null) {
                val contentTypeStr = contentTypeObj.toString()
                if (!TextUtils.isEmpty(contentTypeStr) && contentTypeStr.startsWith("multipart")) {
                    stringBuilder.append(sendDataLen).append(body.contentLength()).append("\n")
                } else {
                    val buffer = Buffer()
                    body.writeTo(buffer)
                    val contentType = body.contentType()
                    if (contentType != null) {
                        charset = contentType.charset(charset)
                    }
                    if (isPlaintext(buffer)) {
                        var data: String? = buffer.readString(charset)
                        data = URLDecoder.decode(data, "UTF-8")
                        stringBuilder.append("send->").append(data).append("\n")
                    } else {
                        stringBuilder.append(sendDataLen).append(body.contentLength()).append("\n")
                    }
                }
            } else {
                stringBuilder.append(sendDataLen).append(body.contentLength()).append("\n")
            }
        } else {
            stringBuilder.append("send->null").append("\n")
        }
        stringBuilder.append("──────────────────────────────────────────────────────────\n")
        stringBuilder.append("Response: \n")
        val time = System.currentTimeMillis()
        var response = chain.proceed(originalRequest)
        stringBuilder.append("Time: ").append(System.currentTimeMillis() - time).append("ms\n")
        stringBuilder.append("HttpCode: ").append(response.code).append("\n")
        stringBuilder.append("isFromCache: ").append(response.networkResponse == null).append("\n")
        stringBuilder.append("Headers: \n")
        for ((index, header) in response.headers.withIndex()) {
            stringBuilder.append("    ").append("${header.first}: ${header.second}")
            if (index != response.headers.size - 1) {
                stringBuilder.append("\n")
            }
        }
        stringBuilder.append("\n")
        val responseBody = response.body
        if (responseBody != null) {
            val contentLength = responseBody.contentLength()
            val bodySize = if (contentLength != -1L) "$contentLength-byte" else "unknown-length"
            stringBuilder.append("Size: ").append(bodySize).append("\n")
            if (isText(responseBody.contentType())) {
                val source = responseBody.source()
                source.request(Long.MAX_VALUE)
                val buffer = source.buffer
                var responseStr = ""
                var headers = response.headers
                if ("gzip".equals(response.header("Content-Encoding"), ignoreCase = true)) {
                    val gzipInputStream = GZIPInputStream(buffer.clone().inputStream())
                    readDataFromStream(gzipInputStream)?.let {
                        responseStr = String(it)
                    }

                    val tmp = headers.newBuilder()
                    tmp.removeAll("Content-Encoding")
                    headers = tmp.build()
                } else {
                    responseStr = buffer.clone().readString(charset!!)
                }
                stringBuilder.append("data->").append(formatJson(responseStr))
                val builder = Response.Builder()
                    .request(response.request)
                    .protocol(response.protocol)
                    .code(response.code)
                    .handshake(response.handshake)
                    .headers(headers)
                    .message(response.message)
                    .receivedResponseAtMillis(response.receivedResponseAtMillis)
                    .sentRequestAtMillis(response.sentRequestAtMillis)
                    .body(ResponseBody.create(responseBody.contentType(), responseStr))
                response = builder.build()
            }
        }
        KLog.i(stringBuilder.toString(), enableBorder = true)
        return response
    }

    /***
     * 判断是否是文字
     * @param buffer 缓存
     * @return
     */
    private fun isPlaintext(buffer: Buffer): Boolean {
        return try {
            val prefix = Buffer()
            val byteCount = if (buffer.size < 64) buffer.size else 64
            buffer.copyTo(prefix, 0, byteCount)
            for (i in 0..15) {
                if (prefix.exhausted()) {
                    break
                }
                val codePoint = prefix.readUtf8CodePoint()
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false
                }
            }
            true
        } catch (e: EOFException) {
            false
        }
    }

    /***
     * 根据url判断是否是文本
     * @param mediaType contentType
     * @return
     */
    private fun isText(mediaType: MediaType?): Boolean {
        if (mediaType == null) {
            return false
        }
        val contentType = mediaType.toString()
        return contentType.startsWith("text/plain")
                || contentType.startsWith("application/json")
                || contentType.startsWith("text/html")
    }

    /***
     * 从流中读取数据
     * @param inputStream 读取流
     * @return 读取的数据
     */
    private fun readDataFromStream(inputStream: InputStream?): ByteArray? {
        if (inputStream == null) {
            return byteArrayOf()
        }
        try {
            ByteArrayOutputStream().use { bos ->
                var len = 0
                val buffer = ByteArray(1024)
                while (inputStream.read(buffer).also { len = it } > 0) {
                    bos.write(buffer, 0, len)
                }
                return bos.toByteArray()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return byteArrayOf()
        } finally {
            StreamUtils.closeStream(inputStream)
        }
    }

    private fun formatJson(jsonStr: String): String {
        try {
            val json = jsonStr.trim { it <= ' ' }
            if (json.startsWith("{")) {
                val jsonObject = JSONObject(json)
                return jsonObject.toString(2)
            }
            if (json.startsWith("[")) {
                val jsonArray = JSONArray(json)
                return jsonArray.toString(2)
            }
            return jsonStr
        } catch (e: JSONException) {
            return jsonStr
        }
    }
}