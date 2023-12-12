package com.cw.simple.example.app.data.repository

import com.cw.simple.example.app.data.model.ServerResultData

open class BaseRepository {
    protected val TAG = this.javaClass.simpleName

    fun <T> preprocessData(responseBody: ServerResultData<T>): T? {
        return if (responseBody.code == 200) responseBody.data else throw Throwable(responseBody.message)
    }
}