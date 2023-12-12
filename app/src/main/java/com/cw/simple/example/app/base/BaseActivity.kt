package com.cw.simple.example.app.base

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cw.simple.example.app.R
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

inline fun <reified VM : ViewModel> BaseActivity.createViewModel() = lazy {
    ViewModelProvider(this)[VM::class.java]
}

abstract class BaseActivity : AppCompatActivity() {
    protected val TAG = this.javaClass.simpleName
    protected var mDataBinding: ViewDataBinding? = null
    protected var isOnResume = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createDataBinding()
        setStatusBarNavigationBarColor(R.color.white, true)
        setTitleBackClick()
        init(intent.extras)
        EventBus.getDefault().register(this)
    }

    override fun onResume() {
        super.onResume()
        isOnResume = true
    }

    override fun onPause() {
        super.onPause()
        isOnResume = false
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun attachBaseContext(context: Context) {
        super.attachBaseContext(context)
    }

    private fun createDataBinding() {
        mDataBinding = DataBindingUtil.setContentView<ViewDataBinding?>(this, getLayoutId())?.also {
            it.lifecycleOwner = this@BaseActivity
        }
    }

    protected abstract fun getLayoutId(): Int

    protected abstract fun init(params: Bundle?)

    protected fun setTitleBackClick() {
        val back = findViewById<View>(R.id.iv_title_layout_back)
        back?.setOnClickListener { v: View? -> finish() }
    }

    /**
     * 设置状态栏和导航栏的背景颜色和文字图标的颜色
     * colorResourcesId: 颜色资源ID
     * isLightColor: 文字颜色, true: 文字黑色, false: 文字白色
     */
    protected fun setStatusBarNavigationBarColor(colorResourcesId: Int, isLightColor: Boolean) {
        setStatusBarColor(colorResourcesId, isLightColor)
        setNavigationBarColor(colorResourcesId, isLightColor)
    }

    protected fun setStatusBarColor(colorResourcesId: Int, isLightColor: Boolean) {
        val window = window.also {
            //添加Flag把状态栏设为可绘制模式
            it.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            //设置状态栏颜色
            it.statusBarColor = getColor(colorResourcesId)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            //android R (android 11, API 30) 使用下面的新api
            //传入0则是清理状态,恢复高亮
            val state = if (isLightColor) WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS else 0
            window?.insetsController?.setSystemBarsAppearance(state, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)
        } else {
            //低于android R 使用兼容模式
            WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = isLightColor
        }
    }

    protected fun setNavigationBarColor(color: Int, isLightColor: Boolean) {
        val window = window.also {
            it.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            it.navigationBarColor = getColor(color)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val state = if (isLightColor) WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS else 0
            window?.insetsController?.setSystemBarsAppearance(state, WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS)
        } else {
            WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightNavigationBars = isLightColor
        }
    }

    /**
     * 设置状态栏沉浸式
     */
    protected fun setLayoutExtendStatusBar(isLightColor: Boolean) {
        WindowCompat.setDecorFitsSystemWindows(window, false)// 关键代码
        val window = window.also {
            it.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            it.statusBarColor = Color.TRANSPARENT
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val state = if (isLightColor) WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS else 0
            window?.insetsController?.setSystemBarsAppearance(state, WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS)
        } else {
            WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightNavigationBars = isLightColor
        }
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { v, insets ->
            val statusBars = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            if (statusBars.top > 0) {
                findViewById<FrameLayout>(android.R.id.content).apply {
                    setPadding(0, statusBars.top, 0, 0)
                }
                ViewCompat.setOnApplyWindowInsetsListener(window.decorView, null)
            }
            insets
        }
    }

    protected fun AppCompatActivity.getStatusBarsHeight(): Int {
        val windowInsetsCompat = ViewCompat.getRootWindowInsets(findViewById(android.R.id.content)) ?: return 0
        return windowInsetsCompat.getInsets(WindowInsetsCompat.Type.statusBars()).top
    }

    protected fun setFullScreen() {
        val window = window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window?.insetsController?.apply {
                hide(WindowInsets.Type.systemBars())
                systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            WindowCompat.getInsetsController(window, window.decorView).apply {
                hide(WindowInsetsCompat.Type.systemBars())
                systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }
    }

    protected fun exitFullScreen() {
        val window = window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window?.insetsController?.apply {
                show(WindowInsets.Type.systemBars())
            }
        } else {
            WindowCompat.getInsetsController(window, window.decorView).apply {
                show(WindowInsetsCompat.Type.systemBars())
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun emptyMethod(any: Any) {
    }
}