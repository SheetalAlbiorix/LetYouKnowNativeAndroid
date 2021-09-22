package com.letyouknow.view.splash

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import com.letyouknow.R
import com.letyouknow.base.BaseActivity
import com.letyouknow.view.dashboard.MainActivity
import com.letyouknow.view.login.LoginActivity
import org.jetbrains.anko.startActivity

class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        init()
    }

    private fun init() {

        Handler().postDelayed({
            if (pref?.isLogin()!!) {
                startActivity<MainActivity>()
            } else {
                startActivity<LoginActivity>()
            }

        }, 3000)
    }

    override fun getViewActivity(): Activity {
        return this
    }

    override fun onNetworkStateChange(isConnect: Boolean) {
    }
}