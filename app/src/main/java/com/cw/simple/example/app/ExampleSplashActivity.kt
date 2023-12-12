package com.cw.simple.example.app

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.cw.simple.example.app.ui.ExampleMainActivity
import com.dylanc.longan.startActivity

class ExampleSplashActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }

    override fun onClick(p0: View?) {
        startActivity<ExampleMainActivity>()
        finish()
    }
}