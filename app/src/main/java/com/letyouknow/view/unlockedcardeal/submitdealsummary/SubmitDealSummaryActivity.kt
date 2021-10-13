package com.letyouknow.view.unlockedcardeal.submitdealsummary

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.letyouknow.R
import com.letyouknow.base.BaseActivity
import com.letyouknow.databinding.ActivitySubmitDealSummaryBinding
import com.letyouknow.model.SubmitDealLCDData
import com.letyouknow.view.dashboard.MainActivity
import com.pionymessenger.utils.Constant.Companion.ARG_SUBMIT_DEAL
import kotlinx.android.synthetic.main.activity_submit_deal_summary.*
import kotlinx.android.synthetic.main.layout_toolbar_blue.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask

class SubmitDealSummaryActivity : BaseActivity(), View.OnClickListener {

    private lateinit var submitDealData: SubmitDealLCDData
    private lateinit var binding: ActivitySubmitDealSummaryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_submit_deal_summary)
        init()
    }

    private fun init() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_submit_deal_summary)
        if (intent.hasExtra(ARG_SUBMIT_DEAL)) {
            submitDealData = Gson().fromJson(
                intent.getStringExtra(ARG_SUBMIT_DEAL),
                SubmitDealLCDData::class.java
            )
            binding.data = submitDealData
        }
        btnFindYourCar.setOnClickListener(this)
        ivBack.visibility = View.GONE
        tvTitleTool.visibility = View.GONE
    }

    override fun getViewActivity(): Activity? {
        return this
    }

    override fun onNetworkStateChange(isConnect: Boolean) {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnFindYourCar -> {
                onBackPressed()
            }
        }
    }

    override fun onBackPressed() {
        startActivity(
            intentFor<MainActivity>().clearTask().newTask()
        )
    }
}