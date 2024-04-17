package com.cw.simple.example.app.ui

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.cw.simple.example.app.base.BaseViewModel
import com.cw.simple.example.app.data.LoadState
import com.cw.simple.example.app.data.model.ExampleData
import com.cw.simple.example.app.data.repository.ExampleRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class ExampleMainViewModel(application: Application) : BaseViewModel(application) {
    var exampleData = MutableLiveData<ExampleData>()

    fun loadExampleData() {
        load(
            {

            },
            {

            }
        ) {
            exampleData.postValue(ExampleRepository.getInstance().loadExampleData("", ""))
        }
    }
}