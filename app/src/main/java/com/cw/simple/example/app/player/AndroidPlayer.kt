package com.cw.simple.example.app.player

import android.graphics.SurfaceTexture
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.SurfaceHolder
import com.cw.simple.example.app.App

class AndroidPlayer() : MediaPlayer {
    private var mSurfaceRenderView: SurfaceRenderView? = null
    private var mTextureRenderView: TextureRenderView? = null

    constructor(textureRenderView: TextureRenderView) : this() {
        mTextureRenderView = textureRenderView
    }

    constructor(surfaceRenderView: SurfaceRenderView) : this() {
        mSurfaceRenderView = surfaceRenderView
    }

    override val TAG: String = "AndroidPlayer"
    override var listener: MediaPlayerListener? = null
    private val WHAT_REPLAY = 1
    private lateinit var mediaPlayer: android.media.MediaPlayer
    private var mVideoWidth = 0
    private var mVideoHeight = 0
    private var mUrl: String = ""
    private var mHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            if (msg.what == WHAT_REPLAY) {
                play(mUrl)
            }
        }
    }

    override fun init() {
        mSurfaceRenderView?.attachToPlayer(this)
        mTextureRenderView?.attachToPlayer(this)
        mediaPlayer = android.media.MediaPlayer()
        mediaPlayer.setOnPreparedListener {
            it.start()
        }
        mediaPlayer.setOnInfoListener { mp, what, extra ->
            Log.d(TAG, "OnInfoListener: what = $what")
            true
        }
        mediaPlayer.setOnErrorListener { mp, what, extra ->
            Log.d(TAG, "setOnErrorListener: what = $what")
            true
        }
    }

    override fun setDisplay(holder: SurfaceHolder?) {
        mediaPlayer.setDisplay(holder)
    }

    override fun setDisplay(surface: SurfaceTexture?) {
        TODO("Not yet implemented")
    }

    override fun resetSurfaceViewSize() {
        mHandler.post {
            mSurfaceRenderView?.setScaleType(SurfaceRenderView.SCREEN_SCALE_CENTER_CROP)
            mSurfaceRenderView?.setVideoSize(mVideoWidth, mVideoHeight)
        }
    }

    override fun play(url: String) {
        mediaPlayer.apply {
            setDataSource(App.get(), Uri.parse(url))
            prepareAsync()
        }
    }

    override fun pause() {
        mediaPlayer.pause()
    }

    override fun stop() {
        mediaPlayer.stop()
        mediaPlayer.reset()
    }

    override fun release() {
        mediaPlayer.release()
    }
}