package com.cw.simple.example.app.utils

import android.graphics.Insets
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowInsets
import android.view.WindowManager
import android.view.WindowMetrics
import com.cw.simple.example.app.App


class ScreenUtils private constructor() {
    private var mScreenWidth = 0
    private var mScreenHeight = 0
    private var mScreenRealHeight = 0
    companion object {
        private var instance: ScreenUtils? = null
            get() {
                if (field == null) {
                    field = ScreenUtils()
                }
                return field
            }

        fun get(): ScreenUtils {
            return instance!!
        }
    }

    init {
        val windowManager = App.get().getSystemService(WindowManager::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics: WindowMetrics = windowManager.currentWindowMetrics
            val insets: Insets =
                windowMetrics.windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            mScreenWidth = windowMetrics.bounds.width()
            mScreenHeight = windowMetrics.bounds.height() - insets.top - insets.bottom
            mScreenRealHeight = windowMetrics.bounds.height()
        } else {
            val displayMetrics = DisplayMetrics()
            val defaultDisplay = windowManager.defaultDisplay
            defaultDisplay.getMetrics(displayMetrics)
            mScreenWidth = displayMetrics.widthPixels
            mScreenHeight = displayMetrics.heightPixels
            defaultDisplay.getRealMetrics(displayMetrics)
            mScreenRealHeight = displayMetrics.heightPixels
        }
    }

    fun getScreenWidth(): Int {
        return mScreenWidth
    }

    fun getScreenHeight(): Int {
        return mScreenHeight
    }
}