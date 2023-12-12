package com.cw.simple.example.app.data.model

class ServerResultData<T> {
    var code: Int? = 0
    var message: String? = ""
    var data: T? = null
}