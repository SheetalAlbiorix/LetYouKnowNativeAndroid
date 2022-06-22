package com.letyouknow.view.splash

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.annotation.NonNull
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.installations.InstallationTokenResult
import com.google.gson.Gson
import com.letyouknow.R
import com.letyouknow.base.BaseActivity
import com.letyouknow.model.MarketConditionsData
import com.letyouknow.utils.Constant.Companion.ARG_DEAL_ID
import com.letyouknow.utils.Constant.Companion.ARG_IS_NOTIFICATION
import com.letyouknow.utils.Constant.Companion.ARG_TRANSACTION_CODE
import com.letyouknow.utils.Constant.Companion.ARG_UCD_DATA
import com.letyouknow.utils.Constant.Companion.HOT_PRICE
import com.letyouknow.utils.Constant.Companion.KEY_NOTIFICATION_TYPE
import com.letyouknow.utils.Constant.Companion.LCD_DEAL
import com.letyouknow.utils.Constant.Companion.LYK_DEAL
import com.letyouknow.utils.Constant.Companion.MARKET_CONDITION
import com.letyouknow.utils.Constant.Companion.PROMO_CODE
import com.letyouknow.utils.Constant.Companion.TransactionCode
import com.letyouknow.utils.Constant.Companion.UCD_DEAL
import com.letyouknow.view.dashboard.MainActivity
import com.letyouknow.view.login.LoginActivity
import org.jetbrains.anko.startActivity


class SplashActivity : BaseActivity() {

    /*10007
    30031
    98125
    21117 (may need to go 100 or 150 mi)
    {
    "Username": "e36328is98@gmail.com",
     "password": "WH&ry9MegCbw3FPG"
     }

     Albiorix12@gmail.com
     Albiorix@1234
    */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        if (pref?.isLogin()!!) {
            onNewIntent(intent)
        } else {
            init()
        }
    }

    private fun init() {
        Handler().postDelayed({
            if (pref?.isLogin()!!) {
                startActivity<MainActivity>()
            } else {
                startActivity<LoginActivity>()
            }
        }, 3000)
        getFBToken()
    }


    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val extras = intent.extras
        if (extras != null) {
            if (extras.containsKey(KEY_NOTIFICATION_TYPE)) {
                if (extras.get(KEY_NOTIFICATION_TYPE) == PROMO_CODE) {
                    startActivity<MainActivity>(ARG_IS_NOTIFICATION to true)
                } else if (extras.get(KEY_NOTIFICATION_TYPE) == HOT_PRICE) {
                    startActivity<MainActivity>(
                        ARG_IS_NOTIFICATION to true,
                        ARG_UCD_DATA to extras.get("data")
                    )
                } else if (extras.get(KEY_NOTIFICATION_TYPE) == LYK_DEAL || extras.get(
                        KEY_NOTIFICATION_TYPE
                    ) == LCD_DEAL || extras.get(KEY_NOTIFICATION_TYPE) == UCD_DEAL
                ) {
                    startActivity<MainActivity>(
                        ARG_IS_NOTIFICATION to true,
                        ARG_TRANSACTION_CODE to extras.get(TransactionCode)
                    )
                } else if (extras.get(KEY_NOTIFICATION_TYPE) == MARKET_CONDITION) {
                    val dataMarket = Gson().fromJson(
                        extras.get("data").toString(),
                        MarketConditionsData::class.java
                    )
                    startActivity<MainActivity>(
                        ARG_IS_NOTIFICATION to true,
                        ARG_DEAL_ID to dataMarket.DealId
                    )
                } else {
                    init()
                }
            }
        } else {
            init()
        }
    }

    override fun getViewActivity(): Activity {
        return this
    }

    override fun onNetworkStateChange(isConnect: Boolean) {
    }

    private fun getFBToken() {
        FirebaseInstallations.getInstance().getToken(false)
            .addOnCompleteListener(object : OnCompleteListener<InstallationTokenResult?> {
                override fun onComplete(@NonNull task: Task<InstallationTokenResult?>) {
                    if (!task.isSuccessful()) {
                        return
                    }
                    // Get new Instance ID token
                    val token: String = task.result?.token!!
                    Log.e("fbtoken", token)
                }
            })
    }
}
