package com.cw.simple.example.app.data.repository

import com.cw.simple.example.app.data.model.ExampleData
import com.cw.simple.example.app.data.model.ServerResultData
import retrofit2.http.GET

interface ExampleWebService {

    @GET("rt-user/config")
    suspend fun exampleLoadData(): ServerResultData<ExampleData>
}