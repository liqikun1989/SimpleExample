package com.cw.simple.example.app.data.repository

import com.cw.simple.example.app.data.model.ExampleData
import com.cw.simple.example.app.net.RetrofitServer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ExampleRepository : BaseRepository() {
    companion object {
        private var instance: ExampleRepository? = null

        fun getInstance() = instance ?: synchronized(this) {
            instance ?: ExampleRepository().also { instance = it }
        }
    }

    private val webService: ExampleWebService = RetrofitServer.getRetrofit().create(ExampleWebService::class.java)

    suspend fun loadExampleData(param1: String, param2: String): ExampleData = withContext(Dispatchers.IO) {
        preprocessData(webService.exampleLoadData())
    } as ExampleData
}