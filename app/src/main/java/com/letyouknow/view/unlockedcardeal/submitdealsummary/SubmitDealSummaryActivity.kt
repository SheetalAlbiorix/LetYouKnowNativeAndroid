package com.letyouknow.view.unlockedcardeal.submitdealsummary

import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
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
            binding.userName = pref?.getUserData()?.firstName + " " + pref?.getUserData()?.lastName
            binding.data = submitDealData

            var add2 = submitDealData.successResult?.transactionInfo?.buyerAddress2
            if (!TextUtils.isEmpty(add2) && add2 != "null" && add2 != "NULL") {
                add2 += "\n"
            }
            var buyerInfo = ""
            buyerInfo =
                pref?.getUserData()?.firstName + " " + pref?.getUserData()?.lastName + "\n" +
                        submitDealData.successResult?.transactionInfo?.buyerAddress1 + "\n" +
                        add2?.trim() +
                        submitDealData.successResult?.transactionInfo?.buyerCity + "," +
                        submitDealData.successResult?.transactionInfo?.buyerState + " " +
                        submitDealData.successResult?.transactionInfo?.buyerZipcode + "\n" +
                        submitDealData.successResult?.transactionInfo?.buyerPhone + "\n" +
                        submitDealData.successResult?.transactionInfo?.buyerEmail

            tvBuyerInfo.text = buyerInfo
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