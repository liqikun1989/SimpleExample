package com.cw.simple.example.app.ui

import android.os.Bundle
import android.view.View
import com.cw.simple.example.app.R
import com.cw.simple.example.app.base.BaseActivity
import com.cw.simple.example.app.base.createViewModel
import com.cw.simple.example.app.data.LoadState
import com.cw.simple.example.app.databinding.ActivityMainBinding

class ExampleMainActivity : BaseActivity(), View.OnClickListener {
    private val mViewModel: ExampleMainViewModel by createViewModel()

    override fun getLayoutId(): Int = R.layout.activity_main

    override fun init(params: Bundle?) {
//        setLayoutExtendStatusBar(true)
        val binding = mDataBinding as ActivityMainBinding
        mViewModel.loadState.observe(this) {
            when (it) {
                is LoadState.Success -> {
                }

                is LoadState.Fail -> {
                }

                is LoadState.Loading -> {
                }
            }
        }
        mViewModel.loadExampleData()
    }

    override fun onClick(v: View?) {
    }
}