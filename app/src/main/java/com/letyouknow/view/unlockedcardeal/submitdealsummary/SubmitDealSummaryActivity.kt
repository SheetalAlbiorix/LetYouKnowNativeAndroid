package com.letyouknow.view.unlockedcardeal.submitdealsummary

import android.app.Activity
import android.os.Bundle
import com.google.gson.Gson
import com.letyouknow.R
import com.letyouknow.base.BaseActivity
import com.letyouknow.model.SubmitDealLCDData
import com.pionymessenger.utils.Constant.Companion.ARG_SUBMIT_DEAL

class SubmitDealSummaryActivity : BaseActivity() {

    private lateinit var submitDealData: SubmitDealLCDData
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_submit_deal_summary)
        init()
    }

    private fun init() {
        if (intent.hasExtra(ARG_SUBMIT_DEAL)) {
            submitDealData = Gson().fromJson(
                intent.getStringExtra(ARG_SUBMIT_DEAL),
                SubmitDealLCDData::class.java
            )
        }
    }

    override fun getViewActivity(): Activity? {
        return this
    }

    override fun onNetworkStateChange(isConnect: Boolean) {
    }
}