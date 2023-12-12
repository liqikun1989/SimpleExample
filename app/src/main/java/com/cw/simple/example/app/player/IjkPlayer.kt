package com.cw.simple.example.app.player

import android.graphics.SurfaceTexture
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import android.util.Log
import android.view.Surface
import android.view.SurfaceHolder
import tv.danmaku.ijk.media.player.IMediaPlayer
import tv.danmaku.ijk.media.player.IjkMediaPlayer


class IjkPlayer() : MediaPlayer {
    private var mSurfaceRenderView: SurfaceRenderView? = null
    private var mTextureRenderView: TextureRenderView? = null

    constructor(textureRenderView: TextureRenderView) : this() {
        mTextureRenderView = textureRenderView
    }

    constructor(surfaceRenderView: SurfaceRenderView) : this() {
        mSurfaceRenderView = surfaceRenderView
    }

    override val TAG = "IjkPlayer"
    override var listener: MediaPlayerListener? = null
    private val WHAT_REPLAY = 1
    private lateinit var mIjkMediaPlayer: IjkMediaPlayer
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
        mIjkMediaPlayer = IjkMediaPlayer()
        mIjkMediaPlayer.setOnPreparedListener { iMediaPlayer: IMediaPlayer ->
            Log.d(TAG, "----------> onPrepared")
        }
        mIjkMediaPlayer.setOnInfoListener { iMediaPlayer: IMediaPlayer?, i: Int, i1: Int ->
            Log.d(TAG, "----------> onInfo , i = $i, i1 = $i1")
            when (i) {
                IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START -> {
//                    if (mLoading != null && mLoading.getVisibility() !== View.GONE) {
//                        mLoading.setVisibility(View.GONE)
//                    }
                    listener?.onRenderedFirstFrame()
                }
                IMediaPlayer.MEDIA_INFO_BUFFERING_START -> {
//                    if (mLoading != null && mLoading.getVisibility() !== View.VISIBLE) {
//                    mLoading.setVisibility(View.VISIBLE)
//                    }
                }
                IMediaPlayer.MEDIA_INFO_BUFFERING_END -> {
//                    if (mLoading != null && mLoading.getVisibility() !== View.GONE) {
//                    mLoading.setVisibility(View.GONE)
//                    }
                }
            }
            false
        }
        mIjkMediaPlayer.setOnCompletionListener { iMediaPlayer: IMediaPlayer? ->
            Log.d(TAG, "----------> onCompletion")
        }
        mIjkMediaPlayer.setOnErrorListener { iMediaPlayer: IMediaPlayer?, i: Int, i1: Int ->
            Log.d(TAG, "----------> onError , i = $i, i1 = $i1")
            false
        }
        mIjkMediaPlayer.setOnVideoSizeChangedListener { iMediaPlayer: IMediaPlayer?, width: Int, height: Int, i2: Int, i3: Int ->
            Log.d(TAG, "----------> onVideoSizeChanged , width = $width, height = $height")
            if (mVideoWidth != width || mVideoHeight != height) {
                mVideoWidth = width
                mVideoHeight = height
                resetSurfaceViewSize()
            }
        }
    }

    override fun setDisplay(holder: SurfaceHolder?) {
//        mIjkMediaPlayer.setDisplay(holder)
    }

    override fun setDisplay(surface: SurfaceTexture?) {
//        mIjkMediaPlayer.setSurface(Surface(surface))
    }

    override fun resetSurfaceViewSize() {
        mHandler.post {
            mSurfaceRenderView?.setScaleType(SurfaceRenderView.SCREEN_SCALE_CENTER_CROP)
            mSurfaceRenderView?.setVideoSize(mVideoWidth, mVideoHeight)
        }
    }

    override fun play(url: String) {
        Log.d(TAG, "----------> play, url = $url")
        if (TextUtils.isEmpty(url)) {
//            ToastUtil.show(WordUtil.getString(R.string.live_play_error))
            return
        }
        mUrl = url
        try {
            setOptions()
            mIjkMediaPlayer.dataSource = url
            if (mSurfaceRenderView != null) mIjkMediaPlayer.setDisplay(mSurfaceRenderView?.holder)
            else mIjkMediaPlayer.setSurface(Surface(mTextureRenderView?.surfaceTexture))
            mIjkMediaPlayer.prepareAsync()
        } catch (e: Exception) {
            e.printStackTrace()
            tryRePlay(3000)
        }
    }

    override fun pause() {
        Log.d(TAG, "----------> pause")
        mIjkMediaPlayer.pause()
    }

    override fun stop() {
        Log.d(TAG, "----------> stop")
        mIjkMediaPlayer.stop()
        mIjkMediaPlayer.reset()
        mHandler.removeCallbacksAndMessages(null)
    }

    override fun release() {
        Log.d(TAG, "----------> release")
        mIjkMediaPlayer.stop()
        mIjkMediaPlayer.reset()
        mIjkMediaPlayer.release()
    }

    private fun setOptions() {
//        //设置软解硬解
//        // 0  使用软解
//        // 1  使用硬解
//        mIjkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 0)
////        mIjkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 1)
////        mIjkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", 1)
//        //设置播放前的探测时间 1,达到首屏秒开效果
//        mIjkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzeduration", 2000000)
//        mIjkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzemaxduration", 2000000)
//        //设置无packet缓存
//        mIjkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "packet-buffering", 0)
//        mIjkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "fflags", "nobuffer")
//        //如果是rtsp协议，可以优先用tcp(默认是用udp)
////        mIjkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "rtsp_transport", "tcp")
//        //播放前的探测Size，默认是1M, 改小一点会出画面更快
//        mIjkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "probesize", 4096)
//        //立刻写出处理完的Packet
//        mIjkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "flush_packets", 1)
//        //允许丢帧
//        mIjkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 60)
//        //优化进度跳转
////        mIjkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1)
//        //设置是否开启环路过滤: 0开启，画面质量高，解码开销大，48关闭，画面质量差点，解码开销小
//        mIjkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48)
//        //需要准备好后自动播放
//        //1 自动播放（默认值）
//        //0 准备好后暂停，等待播放命令
//        mIjkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 1)
//        //不限制拉流缓存大小
////        mIjkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "infbuf", 1)
//        //设置最大缓存数量
//        mIjkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "max-buffer-size", 1024)
//        mIjkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IjkMediaPlayer.SDL_FCC_YV12.toLong())
////        mIjkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max-fps", 0)
////        mIjkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "fps", 60)
////        mIjkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "min-frames", 3)
        mIjkMediaPlayer.apply {
            setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1)
            setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 1)
            setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", 1)
            setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "probesize", 1024 * 16)
            setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzeduration", 50000)
            setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 0)
            setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_frame", 0)
            setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max_cached_duration", 3000)
            setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "infbuf", 1)
            setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "packet-buffering", 0)
            setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "fflags", "nobuffer")
            setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 5)
        }
    }

    fun tryRePlay(delay: Int) {
        if (mHandler.hasMessages(WHAT_REPLAY)) {
            return
        }
        mHandler.sendEmptyMessageDelayed(WHAT_REPLAY, delay.toLong())
    }
}