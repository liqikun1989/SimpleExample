package com.cw.simple.example.app.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cw.simple.example.app.data.LoadState
import com.cw.simple.example.app.data.TokenInvalidException
import com.cw.simple.example.app.event.EventBusBean
import com.dylanc.longan.toast
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import retrofit2.HttpException
import java.net.SocketTimeoutException

abstract class BaseViewModel(application: Application) : AndroidViewModel(application) {
    val loadState = MutableLiveData<LoadState>()

    fun load(
        onError: (e: Throwable) -> Unit = {},
        onComplete: () -> Unit = {},
        postLoading: Boolean = false,
        block: suspend CoroutineScope.() -> Unit,
    ) {
        viewModelScope.launch(CoroutineExceptionHandler { _, e ->
            e.printStackTrace()
            loadState.value = LoadState.Fail(e.message ?: "")
            onError(e)
            when (e) {
                is TokenInvalidException -> {
                    EventBus.getDefault().post(EventBusBean.TokenInvalidEvent())
                }

                is SocketTimeoutException -> {
                    toast("请求服务器超时")
                }

                is HttpException -> {
                    toast("服务器异常")
                }

                else -> toast("${e.message}")
            }
        }) {
            if (postLoading) {
                loadState.value = LoadState.Loading()
            }
            try {
                block.invoke(this)
            } finally {
                loadState.value = LoadState.Success()
                onComplete()
            }
        }
    }
}

fun BaseViewModel.toast(message: CharSequence?) {
    com.dylanc.longan.application.toast(message)
}

inline fun <reified T> genericType() = object : TypeToken<T>() {}.type