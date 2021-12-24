package com.letyouknow.view.ucd.submitdealsummary

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
import com.letyouknow.utils.AppGlobal
import com.letyouknow.view.dashboard.MainActivity
import com.pionymessenger.utils.Constant
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
            if (add2 != "null" && add2 != "NULL" && !TextUtils.isEmpty(add2?.trim())) {
                add2 += "\n"
            }

            var buyerName = ""

            if (TextUtils.isEmpty(submitDealData.successResult?.transactionInfo?.buyerName)) {
                buyerName = ""
            } else {
                val arName = submitDealData.successResult?.transactionInfo?.buyerName?.split(" ")
                if (arName?.size!! > 0) {
                    buyerName = when (arName.size) {
                        3 -> {
                            val isMiddle = arName[1][0].let {
                                Constant.middleNameValidator(
                                    it.toString()
                                )
                            }

                            val middleName = if (isMiddle) {
                                (arName[1][0] + " ")
                            } else {
                                ""
                            }

                            arName[0] + " " + middleName + arName[2] + "\n"
                        }
                        2 -> {
                            arName[0] + " " + arName[1] + "\n"
                        }
                        else -> {
                            arName[0] + "\n"
                        }
                    }

                }
            }
            var buyerInfo = ""
            buyerInfo =
                buyerName +
                        submitDealData.successResult?.transactionInfo?.buyerAddress1 + "\n" +
                        add2 +
                        submitDealData.successResult?.transactionInfo?.buyerCity + ", " +
                        submitDealData.successResult?.transactionInfo?.buyerState + " " +
                        submitDealData.successResult?.transactionInfo?.buyerZipcode + "\n" +
                        submitDealData.successResult?.transactionInfo?.buyerPhone + "\n" +
                        submitDealData.successResult?.transactionInfo?.buyerEmail

            tvBuyerInfo.text = buyerInfo
        }
        btnFindYourCar.setOnClickListener(this)
        tvCallNumber.setOnClickListener(this)
        ivBack.visibility = View.GONE
        tvTitleTool.visibility = View.GONE
    }

    override fun getViewActivity(): Activity {
        return this
    }

    override fun onNetworkStateChange(isConnect: Boolean) {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnFindYourCar -> {
                onBackPressed()
            }
            R.id.tvCallNumber -> {
                AppGlobal.callDialerOpen(this, tvCallNumber.text.toString().trim())
            }
        }
    }

    override fun onBackPressed() {
        startActivity(
            intentFor<MainActivity>().clearTask().newTask()
        )
    }
}