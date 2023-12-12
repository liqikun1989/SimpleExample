package com.cw.simple.example.app.utils

import java.io.Closeable

object StreamUtils {
    @JvmStatic
    fun closeStream(stream: Closeable?) {
        if (stream != null) {
            try {
                stream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}