package com.cw.simple.example.app

import android.app.Application
import com.cw.simple.example.app.log.KLog
import com.cw.simple.example.app.net.RetrofitServer

class App : Application() {
    companion object {
        private var mInstance: App? = null
        fun get(): App {
            return mInstance!!
        }
    }

    override fun onCreate() {
        super.onCreate()
        mInstance = this
        KLog.init(this, "BeiDowWatch", enableLog = BuildConfig.DEBUG, writeToFile = false, enableBorder = false)
        RetrofitServer.init(this, BuildConfig.BASE_URL/*, mutableListOf(TokenInterceptor(), RetInterceptor())*/)
    }
}