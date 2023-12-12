package com.cw.simple.example.app.player

import android.graphics.SurfaceTexture
import android.view.SurfaceHolder

interface MediaPlayer {
    val TAG: String
    var listener : MediaPlayerListener?

    fun init()

    fun setDisplay(holder: SurfaceHolder?)

    fun setDisplay(surface: SurfaceTexture?)

    fun resetSurfaceViewSize()

    fun play(url : String)

    fun pause()

    fun stop()

    fun release()
}