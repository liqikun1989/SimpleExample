package com.cw.simple.example.app.player

import android.graphics.SurfaceTexture
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import android.util.Log
import android.view.Surface
import android.view.SurfaceHolder
import com.cw.simple.example.app.App
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.video.VideoSize


class Exoplayer() : MediaPlayer {
    private var mSurfaceRenderView: SurfaceRenderView? = null
    private var mTextureRenderView: TextureRenderView? = null

    constructor(textureRenderView: TextureRenderView) : this() {
        mTextureRenderView = textureRenderView
    }

    constructor(surfaceRenderView: SurfaceRenderView) : this() {
        mSurfaceRenderView = surfaceRenderView
    }

    override val TAG: String = "Exoplayer"
    override var listener: MediaPlayerListener? = null
    private val WHAT_REPLAY = 1
    private lateinit var mExoPlayer: ExoPlayer
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
        mExoPlayer = ExoPlayer.Builder(App.get()).build()
        mExoPlayer.playWhenReady = true
        mExoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                Log.d(TAG, "----------> onPlaybackStateChanged, playbackState = $playbackState")
                when (playbackState) {
                    Player.STATE_IDLE -> {}
                    Player.STATE_BUFFERING -> {}
                    Player.STATE_READY -> {}
                    // 播放器已完成媒体播放。
                    Player.STATE_ENDED -> {
                        tryRePlay(3000)
                    }
                }
            }

            override fun onVideoSizeChanged(videoSize: VideoSize) {
                Log.d(TAG, "----------> onVideoSizeChanged, videoSize = " + videoSize.width + ", " + videoSize.height)
                if (mVideoWidth != videoSize.width || mVideoHeight != videoSize.height) {
                    mVideoWidth = videoSize.width
                    mVideoHeight = videoSize.height
                    resetSurfaceViewSize()
                }
            }

            override fun onRenderedFirstFrame() {
                Log.d(TAG, "----------> onRenderedFirstFrame")
                listener?.onRenderedFirstFrame()
            }

            override fun onPlayerError(error: PlaybackException) {
                Log.d(TAG, "----------> onPlayerError , error = " + error.errorCodeName + ", " + error.message)
                tryRePlay(3000)
            }
        })
    }

    override fun setDisplay(holder: SurfaceHolder?) {
        mExoPlayer.setVideoSurfaceHolder(holder)
    }

    override fun setDisplay(surface: SurfaceTexture?) {
        mExoPlayer.setVideoSurface(Surface(surface))
    }

    override fun resetSurfaceViewSize() {
        mHandler.post {
            mSurfaceRenderView?.setScaleType(SurfaceRenderView.SCREEN_SCALE_CENTER_CROP)
            mSurfaceRenderView?.setVideoSize(mVideoWidth, mVideoHeight)
        }
    }

    override fun play(url: String) {
        if (TextUtils.isEmpty(url)) {
            return
        }
        mUrl = url
        mExoPlayer.setMediaItem(MediaItem.fromUri(url))
        Log.d(TAG, "----------> play, url = $url")
        mExoPlayer.prepare()
    }

    override fun pause() {
        Log.d(TAG, "----------> pause")
        mExoPlayer.pause()
    }

    override fun stop() {
        Log.d(TAG, "----------> stop")
        mExoPlayer.stop()
    }

    override fun release() {
        Log.d(TAG, "----------> release")
        mExoPlayer.release()
    }

    fun tryRePlay(delay: Int) {
        if (mHandler.hasMessages(WHAT_REPLAY)) {
            return
        }
        mHandler.sendEmptyMessageDelayed(WHAT_REPLAY, delay.toLong())
    }
}