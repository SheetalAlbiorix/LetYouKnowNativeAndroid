package com.letyouknow.view.submitprice.summary

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
import com.letyouknow.databinding.ActivityFinalSubmitPriceDealSummaryBinding
import com.letyouknow.model.*
import com.letyouknow.retrofit.ApiConstant
import com.letyouknow.retrofit.viewmodel.IsSoldViewModel
import com.letyouknow.utils.AppGlobal
import com.letyouknow.view.dashboard.MainActivity
import com.letyouknow.view.home.dealsummary.DealSummaryActivity
import com.letyouknow.view.home.dealsummary.gallery360view.Gallery360TabActivity
import com.pionymessenger.utils.Constant
import com.pionymessenger.utils.Constant.Companion.ARG_IMAGE_ID
import com.pionymessenger.utils.Constant.Companion.ARG_IS_LCD
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
    private lateinit var dataPendingDeal: SubmitPendingUcdData
    private lateinit var binding: ActivityFinalSubmitPriceDealSummaryBinding
    private var imageId = "0"
    private var arImage: ArrayList<String> = ArrayList()

    private lateinit var isSoldViewModel: IsSoldViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_final_submit_price_deal_summary)
        init()
    }

    private fun init() {
        isSoldViewModel =
            ViewModelProvider(this).get(IsSoldViewModel::class.java)
        binding =
            DataBindingUtil.setContentView(this, R.layout.activity_final_submit_price_deal_summary)
        if (intent.hasExtra(ARG_YEAR_MAKE_MODEL) && intent.hasExtra(ARG_IMAGE_ID) && intent.hasExtra(
                ARG_SUBMIT_DEAL
            ) && intent.hasExtra(Constant.ARG_UCD_DEAL_PENDING)
        ) {
            yearModelMakeData = Gson().fromJson(
                intent.getStringExtra(ARG_YEAR_MAKE_MODEL),
                YearModelMakeData::class.java
            )

            dataPendingDeal = Gson().fromJson(
                intent.getStringExtra(Constant.ARG_UCD_DEAL_PENDING),
                SubmitPendingUcdData::class.java
            )

            imageId = intent.getStringExtra(ARG_IMAGE_ID)!!
            arImage = Gson().fromJson(
                intent.getStringExtra(Constant.ARG_IMAGE_URL),
                object : TypeToken<ArrayList<String>>() {}.type
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
            if (submitDealData.negativeResult?.secondLabel == "1") {
                pref?.setRadius(submitDealData.negativeResult?.minimalDistance!!)
            }
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
                setLCDPrefData()
//                callIsSoldAPI(true)
            }
            R.id.tvStep2 -> {
                callIsSoldAPI(false)
            }
            R.id.btnModify -> {
                onBackPressed()
            }
            R.id.btnWait -> {
                onBackPressed()
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
        pref?.setSubmitPriceData(Gson().toJson(PrefSubmitPriceData()))
        pref?.setSubmitPriceTime("")
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

    private fun callIsSoldAPI(isLCD: Boolean) {
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
            request[ApiConstant.Product] = 1
            request[ApiConstant.YearId1] = yearModelMakeData.vehicleYearID!!
            request[ApiConstant.MakeId1] = yearModelMakeData.vehicleMakeID!!
            request[ApiConstant.ModelID] = yearModelMakeData.vehicleModelID!!
            request[ApiConstant.TrimID] = yearModelMakeData.vehicleTrimID!!
            request[ApiConstant.ExteriorColorID] = yearModelMakeData.vehicleExtColorID!!
            request[ApiConstant.InteriorColorID] = yearModelMakeData.vehicleIntColorID!!
            request[ApiConstant.ZipCode1] = yearModelMakeData.zipCode!!
            request[ApiConstant.SearchRadius1] =
                if (yearModelMakeData.radius!! == "ALL") "6000" else yearModelMakeData.radius!!.replace(
                    " mi",
                    ""
                )
            request[ApiConstant.AccessoryList] = accList
            request[ApiConstant.PackageList1] = pkgList
            Log.e("RequestStock", Gson().toJson(request))
            isSoldViewModel.isSoldCall(this, request)!!
                .observe(this, Observer { data ->
                    Constant.dismissLoader()
                    if (data) {
                        pref?.setSubmitPriceData(Gson().toJson(PrefSubmitPriceData()))
                        pref?.setSubmitPriceTime("")
                        startActivity(
                            intentFor<MainActivity>(Constant.ARG_SEL_TAB to Constant.TYPE_SUBMIT_PRICE).clearTask()
                                .newTask()
                        )
                    } else {
                        if (isLCD) {
                            setLCDPrefData()
                        } else {
                            finish()
                        }
                    }

                }
                )
        } else {
            Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setLCDPrefData() {
        val lcdData = PrefOneDealNearYouData()
        lcdData.zipCode = yearModelMakeData.zipCode
        lcdData.yearId = yearModelMakeData.vehicleYearID
        lcdData.makeId = yearModelMakeData.vehicleMakeID
        lcdData.modelId = yearModelMakeData.vehicleModelID
        lcdData.trimId = yearModelMakeData.vehicleTrimID
        lcdData.extColorId = yearModelMakeData.vehicleExtColorID
        lcdData.intColorId = yearModelMakeData.vehicleIntColorID
        lcdData.yearStr = yearModelMakeData.vehicleYearStr
        lcdData.makeStr = yearModelMakeData.vehicleMakeStr
        lcdData.modelStr = yearModelMakeData.vehicleModelStr
        lcdData.trimStr = yearModelMakeData.vehicleTrimStr
        lcdData.extColorStr = yearModelMakeData.vehicleExtColorStr
        lcdData.intColorStr = yearModelMakeData.vehicleIntColorStr
        lcdData.packagesData = yearModelMakeData.arPackages
        lcdData.optionsData = yearModelMakeData.arOptions
        pref?.setOneDealNearYouData(Gson().toJson(lcdData))


        var accessoriesStr = ""
        var isFirstAcce = true
        val arAccId: ArrayList<String> = ArrayList()
        for (i in 0 until yearModelMakeData.arOptions?.size!!) {

            arAccId.add(yearModelMakeData.arOptions!![i].dealerAccessoryID!!)
            if (isFirstAcce) {
                isFirstAcce = false
                accessoriesStr = yearModelMakeData.arOptions!![i].accessory!!
            } else
                accessoriesStr += ",\n" + yearModelMakeData.arOptions!![i].accessory!!

        }
        var packageStr = ""
        var isFirstPackage = true
        val arPackageId: ArrayList<String> = ArrayList()

        for (i in 0 until yearModelMakeData.arPackages?.size!!) {
            arPackageId.add(yearModelMakeData.arPackages!![i].vehiclePackageID!!)
            if (isFirstPackage) {
                isFirstPackage = false
                packageStr = yearModelMakeData.arPackages!![i].packageName!!
            } else {
                packageStr =
                    packageStr + ",\n" + yearModelMakeData.arPackages!![i].packageName!!
            }

        }
        val data = FindLCDDeaData()
        data.productId = "2"
        data.zipCode = yearModelMakeData.zipCode
        data.yearId = yearModelMakeData.vehicleYearID
        data.makeId = yearModelMakeData.vehicleMakeID
        data.modelId = yearModelMakeData.vehicleModelID
        data.trimId = yearModelMakeData.vehicleTrimID
        data.exteriorColorId = yearModelMakeData.vehicleExtColorID
        data.interiorColorId = yearModelMakeData.vehicleIntColorID
        data.yearStr = yearModelMakeData.vehicleYearStr
        data.makeStr = yearModelMakeData.vehicleMakeStr
        data.modelStr = yearModelMakeData.vehicleModelStr
        data.trimStr = yearModelMakeData.vehicleTrimStr
        data.exteriorColorStr = yearModelMakeData.vehicleExtColorStr
        data.interiorColorStr = yearModelMakeData.vehicleIntColorStr
        data.price = yearModelMakeData.price
        data.arPackage = packageStr
        data.arAccessories = accessoriesStr
        data.arAccessoriesId = arAccId
        data.arPackageId = arPackageId
        data.dealID = dataPendingDeal.dealID
        data.guestID = dataPendingDeal.guestID

        startActivity(
            intentFor<DealSummaryActivity>(
                Constant.ARG_LCD_DEAL_GUEST to Gson().toJson(data),
                ARG_IS_LCD to true
            ).clearTask()
                .newTask()
        )
    }

}