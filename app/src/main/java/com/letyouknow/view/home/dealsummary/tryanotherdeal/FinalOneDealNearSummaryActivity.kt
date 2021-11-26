package com.letyouknow.view.home.dealsummary.tryanotherdeal

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.reflect.TypeToken
import com.letyouknow.R
import com.letyouknow.base.BaseActivity
import com.letyouknow.databinding.ActivityFinalOneDealNearSummaryBinding
import com.letyouknow.model.FindLCDDeaData
import com.letyouknow.model.SubmitDealLCDData
import com.letyouknow.model.YearModelMakeData
import com.letyouknow.retrofit.ApiConstant
import com.letyouknow.retrofit.viewmodel.CheckVehicleStockViewModel
import com.letyouknow.utils.AppGlobal
import com.letyouknow.view.dashboard.MainActivity
import com.letyouknow.view.home.dealsummary.gallery360view.Gallery360TabActivity
import com.pionymessenger.utils.Constant
import com.pionymessenger.utils.Constant.Companion.ARG_IMAGE_ID
import com.pionymessenger.utils.Constant.Companion.ARG_LCD_DEAL_GUEST
import com.pionymessenger.utils.Constant.Companion.ARG_SUBMIT_DEAL
import kotlinx.android.synthetic.main.activity_final_one_deal_near_summary.*
import kotlinx.android.synthetic.main.dialog_option_accessories.*
import kotlinx.android.synthetic.main.layout_final_one_deal_near_summary.*
import kotlinx.android.synthetic.main.layout_toolbar_blue.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import org.jetbrains.anko.startActivity

class FinalOneDealNearSummaryActivity : BaseActivity(), View.OnClickListener {

    private lateinit var submitDealData: SubmitDealLCDData
    private lateinit var yearModelMakeData: YearModelMakeData
    private lateinit var binding: ActivityFinalOneDealNearSummaryBinding
    private var imageId = "0"
    private lateinit var arImage: ArrayList<String>
    private lateinit var checkVehicleStockViewModel: CheckVehicleStockViewModel
    private lateinit var dataLCDDeal: FindLCDDeaData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_final_one_deal_near_summary)
        init()
    }

    private fun init() {
        checkVehicleStockViewModel =
            ViewModelProvider(this).get(CheckVehicleStockViewModel::class.java)
        binding =
            DataBindingUtil.setContentView(this, R.layout.activity_final_one_deal_near_summary)
        if (intent.hasExtra(ARG_LCD_DEAL_GUEST) && intent.hasExtra(ARG_IMAGE_ID) && intent.hasExtra(
                ARG_SUBMIT_DEAL
            )
        ) {
            dataLCDDeal = Gson().fromJson(
                intent.getStringExtra(Constant.ARG_LCD_DEAL_GUEST),
                FindLCDDeaData::class.java
            )
            imageId = intent.getStringExtra(ARG_IMAGE_ID)!!
            arImage = Gson().fromJson(
                intent.getStringExtra(Constant.ARG_IMAGE_URL),
                object : TypeToken<ArrayList<String>?>() {}.type
            )
            if (arImage.size != 0) {
                AppGlobal.loadImageUrl(this, ivMain, arImage[0])
                AppGlobal.loadImageUrl(this, ivBgGallery, arImage[0])
                AppGlobal.loadImageUrl(this, ivBg360, arImage[0])
            }
            submitDealData = Gson().fromJson(
                intent.getStringExtra(Constant.ARG_SUBMIT_DEAL),
                SubmitDealLCDData::class.java
            )
            dataLCDDeal.firstName = pref?.getUserData()?.firstName
            binding.data = dataLCDDeal
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
                callIsSoldAPI()
            }
            R.id.btnWait -> {
                callIsSoldAPI()
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
            tvDialogTitle.text =
                dataLCDDeal.yearStr + " " + dataLCDDeal.makeStr + " " + dataLCDDeal.modelStr + " " + dataLCDDeal.trimStr
            tvDialogExteriorColor.text = dataLCDDeal.exteriorColorStr
            tvDialogInteriorColor.text = dataLCDDeal.interiorColorStr
            tvDialogPackage.text = dataLCDDeal.arPackage
            tvDialogOptions.text = dataLCDDeal.arAccessories
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

    private fun callIsSoldAPI() {
        if (Constant.isOnline(this)) {
            val pkgList = JsonArray()
            for (i in 0 until yearModelMakeData.arPackages?.size!!) {
                pkgList.add(yearModelMakeData.arPackages!![i].vehiclePackageID!!)
            }
            val accList = JsonArray()
            for (i in 0 until yearModelMakeData.arOptions!!.size) {
                accList.add(yearModelMakeData.arOptions!![i].dealerAccessoryID)
            }
            Constant.showLoader(this)
            val request = HashMap<String, Any>()
            request[ApiConstant.ProductType] = 2
            request[ApiConstant.YearId1] = yearModelMakeData.vehicleYearID!!
            request[ApiConstant.MakeId1] = yearModelMakeData.vehicleMakeID!!
            request[ApiConstant.ModelID] = yearModelMakeData.vehicleModelID!!
            request[ApiConstant.TrimID] = yearModelMakeData.vehicleTrimID!!
            request[ApiConstant.ExteriorColorID] = yearModelMakeData.vehicleExtColorID!!
            request[ApiConstant.InteriorColorID] = yearModelMakeData.vehicleIntColorID!!
            request[ApiConstant.ZipCode1] = yearModelMakeData.zipCode!!
            request[ApiConstant.SearchRadius1] = "6000"
            request[ApiConstant.AccessoryList] = accList
            request[ApiConstant.PackageList1] = pkgList
            Log.e("RequestStock", Gson().toJson(request))
            checkVehicleStockViewModel.checkVehicleStockCall(this, request)!!
                .observe(this, Observer { data ->
                    Constant.dismissLoader()
                    if (data) {
                        startActivity(
                            intentFor<MainActivity>(Constant.ARG_SEL_TAB to Constant.TYPE_ONE_DEAL_NEAR_YOU).clearTask()
                                .newTask()
                        )
                    } else {
                        finish()
                    }

                }
                )


        } else {
            Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

}