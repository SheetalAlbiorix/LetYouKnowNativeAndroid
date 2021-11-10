package com.letyouknow.view.submitprice.summary

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.letyouknow.R
import com.letyouknow.base.BaseActivity
import com.letyouknow.databinding.ActivityFinalSubmitPriceDealSummaryBinding
import com.letyouknow.model.SubmitDealLCDData
import com.letyouknow.model.YearModelMakeData
import com.letyouknow.view.dashboard.MainActivity
import com.letyouknow.view.home.dealsummary.gallery360view.Gallery360TabActivity
import com.pionymessenger.utils.Constant
import com.pionymessenger.utils.Constant.Companion.ARG_IMAGE_ID
import com.pionymessenger.utils.Constant.Companion.ARG_SUBMIT_DEAL
import com.pionymessenger.utils.Constant.Companion.ARG_YEAR_MAKE_MODEL
import kotlinx.android.synthetic.main.activity_final_submit_price_deal_summary.*
import kotlinx.android.synthetic.main.dialog_option_accessories.*
import kotlinx.android.synthetic.main.layout_final_submit_price_deal_summary.*
import kotlinx.android.synthetic.main.layout_toolbar_blue.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import org.jetbrains.anko.startActivity

class FinalSubmitDealSummaryActivity : BaseActivity(), View.OnClickListener {

    private lateinit var submitDealData: SubmitDealLCDData
    private lateinit var yearModelMakeData: YearModelMakeData
    private lateinit var binding: ActivityFinalSubmitPriceDealSummaryBinding
    private var imageId = "0"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_final_submit_price_deal_summary)
        init()
    }

    private fun init() {
        binding =
            DataBindingUtil.setContentView(this, R.layout.activity_final_submit_price_deal_summary)
        if (intent.hasExtra(ARG_YEAR_MAKE_MODEL) && intent.hasExtra(ARG_IMAGE_ID) && intent.hasExtra(
                ARG_SUBMIT_DEAL
            )
        ) {
            yearModelMakeData = Gson().fromJson(
                intent.getStringExtra(ARG_YEAR_MAKE_MODEL),
                YearModelMakeData::class.java
            )
            imageId = intent.getStringExtra(ARG_IMAGE_ID)!!
            submitDealData = Gson().fromJson(
                intent.getStringExtra(Constant.ARG_SUBMIT_DEAL),
                SubmitDealLCDData::class.java
            )
            yearModelMakeData.firstName = pref?.getUserData()?.firstName
            binding.data = yearModelMakeData
            binding.dealData = submitDealData
        }
        llGallery.setOnClickListener(this)
        ll360.setOnClickListener(this)
        tvViewOptions.setOnClickListener(this)
        btnModify.setOnClickListener(this)
        btnWait.setOnClickListener(this)
        tvLightingCar.setOnClickListener(this)
        tvStep2.setOnClickListener(this)
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
            R.id.tvLightingCar -> {
                onBackPressed()
            }
            R.id.btnFindYourCar -> {
                onBackPressed()
            }
            R.id.btnModify -> {
                finish()
            }
            R.id.btnWait -> {
                finish()
            }
            R.id.tvViewOptions -> {
                popupOption()
            }
            R.id.llGallery -> {
                startActivity<Gallery360TabActivity>(
                    Constant.ARG_TYPE_VIEW to 0,
                    Constant.ARG_IMAGE_ID to imageId
                )
            }
            R.id.ll360 -> {
                startActivity<Gallery360TabActivity>(
                    Constant.ARG_TYPE_VIEW to 2,
                    Constant.ARG_IMAGE_ID to imageId
                )
            }
        }
    }

    override fun onBackPressed() {
        startActivity(
            intentFor<MainActivity>().clearTask().newTask()
        )
    }

    private fun popupOption() {
        val dialog = Dialog(this, R.style.FullScreenDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setContentView(R.layout.dialog_option_accessories)
        dialog.run {
            ivDialogClose.setOnClickListener {
                dismiss()
            }
            yearModelMakeData.run {
                tvDialogTitle.text =
                    vehicleYearStr + " " + vehicleMakeStr + " " + vehicleModelStr + " " + vehicleTrimStr
                tvDialogExteriorColor.text = vehicleExtColorStr
                tvDialogInteriorColor.text = vehicleIntColorStr
                var accessoriesStr = ""
                var isFirstAcce = true
                val arAccId: ArrayList<String> = ArrayList()
                for (i in 0 until arOptions?.size!!) {
                    if (arOptions!![i].isSelect!!) {
                        arAccId.add(arOptions!![i].dealerAccessoryID!!)
                        if (isFirstAcce) {
                            isFirstAcce = false
                            accessoriesStr = arOptions!![i].accessory!!
                        } else
                            accessoriesStr += ",\n" + arOptions!![i].accessory!!
                    }
                }
                var packageStr = ""
                var isFirstPackage = true

                for (i in 0 until arPackages?.size!!) {
                    if (arPackages!![i].isSelect!!) {
                        if (isFirstPackage) {
                            isFirstPackage = false
                            packageStr = arPackages!![i].packageName!!
                        } else {
                            packageStr = packageStr + ",\n" + arPackages!![i].packageName!!
                        }
                    }
                }
                tvDialogPackage.text = packageStr
                tvDialogOptions.text = accessoriesStr
            }
        }
        setLayoutParam(dialog)
        dialog.show()
    }

    private fun setLayoutParam(dialog: Dialog) {
        val layoutParams: WindowManager.LayoutParams = dialog.window?.attributes!!
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        dialog.window?.attributes = layoutParams
    }

}