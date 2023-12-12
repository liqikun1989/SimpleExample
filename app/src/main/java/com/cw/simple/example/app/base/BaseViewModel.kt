package com.cw.simple.example.app.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cw.simple.example.app.data.LoadState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

abstract class BaseViewModel(application: Application) : AndroidViewModel(application) {
    val loadState = MutableLiveData<LoadState>()

    fun load(
        block: suspend CoroutineScope.() -> Unit,
        onError: (e: Throwable) -> Unit = {},
        onComplete: () -> Unit = {}
    ) {
        viewModelScope.launch(CoroutineExceptionHandler { _, e ->
            e.printStackTrace()
            loadState.value = LoadState.Fail(e.message ?: "")
            // TODO: 此处判断错误类型
            onError(e)
        }) {
            try {
                loadState.value = LoadState.Loading()
                block.invoke(this)
            } finally {
                loadState.value = LoadState.Success()
                onComplete()
            }
        }
    }
}