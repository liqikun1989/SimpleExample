package com.cw.simple.example.app.player

import android.content.Context
import android.graphics.SurfaceTexture
import android.util.AttributeSet
import android.util.Log
import android.view.TextureView


class TextureRenderView(context: Context, attrs: AttributeSet?) : TextureView(context, attrs), TextureView.SurfaceTextureListener {
    companion object {
        val SCREEN_SCALE_DEFAULT = 0
        val SCREEN_SCALE_16_9 = 1
        val SCREEN_SCALE_4_3 = 2
        val SCREEN_SCALE_MATCH_PARENT = 3
        val SCREEN_SCALE_ORIGINAL = 4
        val SCREEN_SCALE_CENTER_CROP = 5
    }
    private var mMediaPlayer: MediaPlayer? = null
    private var mVideoWidth = 0
    private var mVideoHeight = 0
    private var mCurrentScreenScale = SCREEN_SCALE_DEFAULT

    init {
        surfaceTextureListener = this
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        Log.d("TextureRenderView", "onSurfaceTextureAvailable: ")
        mMediaPlayer?.setDisplay(surface)
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
        Log.d("TextureRenderView", "onSurfaceTextureSizeChanged: ")
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        TODO("Not yet implemented")
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {

    }

    fun attachToPlayer(player: MediaPlayer) {
        mMediaPlayer = player
    }

    fun setVideoSize(videoWidth: Int, videoHeight: Int) {
        if (videoWidth > 0 && videoHeight > 0) {
            mVideoWidth = videoWidth
            mVideoHeight = videoHeight
            requestLayout()
        }
    }

    fun setScaleType(screenScale: Int) {
        mCurrentScreenScale = screenScale
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var width: Int = MeasureSpec.getSize(widthMeasureSpec)
        var height: Int = MeasureSpec.getSize(heightMeasureSpec)

        if (mVideoHeight == 0 || mVideoWidth == 0) {
            setMeasuredDimension(width, height)
        }

        //如果设置了比例
        when (mCurrentScreenScale) {
            SCREEN_SCALE_DEFAULT -> if (mVideoWidth * height < width * mVideoHeight) {
                width = height * mVideoWidth / mVideoHeight
            } else if (mVideoWidth * height > width * mVideoHeight) {
                height = width * mVideoHeight / mVideoWidth
            }
            SCREEN_SCALE_ORIGINAL -> {
                width = mVideoWidth
                height = mVideoHeight
            }
            SCREEN_SCALE_16_9 -> if (height > width / 16 * 9) {
                height = width / 16 * 9
            } else {
                width = height / 9 * 16
            }
            SCREEN_SCALE_4_3 -> if (height > width / 4 * 3) {
                height = width / 4 * 3
            } else {
                width = height / 3 * 4
            }
            SCREEN_SCALE_MATCH_PARENT -> {
                width = widthMeasureSpec
                height = heightMeasureSpec
            }
            SCREEN_SCALE_CENTER_CROP -> if (mVideoWidth * height > width * mVideoHeight) {
                width = height * mVideoWidth / mVideoHeight
            } else {
                height = width * mVideoHeight / mVideoWidth
            }
            else -> if (mVideoWidth * height < width * mVideoHeight) {
                width = height * mVideoWidth / mVideoHeight
            } else if (mVideoWidth * height > width * mVideoHeight) {
                height = width * mVideoHeight / mVideoWidth
            }
        }
        setMeasuredDimension(width, height)
    }
}