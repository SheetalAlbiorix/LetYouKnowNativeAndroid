package com.letyouknow.view.lyk.summary

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
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
import com.letyouknow.databinding.ActivityLykNegativeBinding
import com.letyouknow.model.*
import com.letyouknow.retrofit.ApiConstant
import com.letyouknow.retrofit.viewmodel.CheckVehicleStockViewModel
import com.letyouknow.retrofit.viewmodel.IsSoldViewModel
import com.letyouknow.retrofit.viewmodel.RefreshTokenViewModel
import com.letyouknow.utils.AppGlobal
import com.letyouknow.utils.Constant
import com.letyouknow.utils.Constant.Companion.ARG_IMAGE_ID
import com.letyouknow.utils.Constant.Companion.ARG_IS_FROM_LYK
import com.letyouknow.utils.Constant.Companion.ARG_IS_LCD
import com.letyouknow.utils.Constant.Companion.ARG_IS_LYK_SHOW
import com.letyouknow.utils.Constant.Companion.ARG_IS_UCD
import com.letyouknow.utils.Constant.Companion.ARG_SUBMIT_DEAL
import com.letyouknow.utils.Constant.Companion.ARG_YEAR_MAKE_MODEL
import com.letyouknow.view.dashboard.MainActivity
import com.letyouknow.view.gallery360view.Gallery360TabActivity
import com.letyouknow.view.lcd.summary.LCDDealSummaryStep1Activity
import com.letyouknow.view.ucd.Items_LinearRVAdapter
import com.letyouknow.view.ucd.unlockeddealdetail.UCDDealSummaryStep2Activity
import kotlinx.android.synthetic.main.activity_lyk_negative.*
import kotlinx.android.synthetic.main.dialog_option_accessories.*
import kotlinx.android.synthetic.main.dialog_option_accessories.ivDialogClose
import kotlinx.android.synthetic.main.dialog_option_accessories.tvDialogDisclosure
import kotlinx.android.synthetic.main.dialog_option_accessories.tvDialogExteriorColor
import kotlinx.android.synthetic.main.dialog_option_accessories.tvDialogOptions
import kotlinx.android.synthetic.main.dialog_option_accessories_unlocked.*
import kotlinx.android.synthetic.main.layout_lyk_negative.*
import kotlinx.android.synthetic.main.layout_lyk_negative.tvPrice
import kotlinx.android.synthetic.main.layout_toolbar_blue.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import org.jetbrains.anko.startActivity
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class LYKNegativeActivity : BaseActivity(), View.OnClickListener {

    private lateinit var submitDealData: SubmitDealLCDData
    private lateinit var yearModelMakeData: YearModelMakeData
    private lateinit var dataPendingDeal: SubmitPendingUcdData
    private lateinit var binding: ActivityLykNegativeBinding
    private var imageId = "0"
    private var arImage: ArrayList<String> = ArrayList()
    private lateinit var tokenModel: RefreshTokenViewModel

    private lateinit var checkVehicleStockViewModel: CheckVehicleStockViewModel

    private lateinit var isSoldViewModel: IsSoldViewModel

    private lateinit var adapterUCD: Items_LinearRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lyk_negative)
        init()
    }

    private fun init() {
        isSoldViewModel =
            ViewModelProvider(this).get(IsSoldViewModel::class.java)
        tokenModel = ViewModelProvider(this)[RefreshTokenViewModel::class.java]
        checkVehicleStockViewModel =
            ViewModelProvider(this)[CheckVehicleStockViewModel::class.java]
        binding =
            DataBindingUtil.setContentView(this, R.layout.activity_lyk_negative)

        if (intent.hasExtra(ARG_YEAR_MAKE_MODEL) && intent.hasExtra(ARG_IMAGE_ID) && intent.hasExtra(
                ARG_SUBMIT_DEAL
            ) && intent.hasExtra(Constant.ARG_UCD_DEAL_PENDING) && intent.hasExtra(
                Constant.ARG_MSRP_RANGE
            )
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
                intent.getStringExtra(ARG_SUBMIT_DEAL),
                SubmitDealLCDData::class.java
            )

            yearModelMakeData.firstName = pref?.getUserData()?.firstName

            binding.radius = yearModelMakeData.radius?.replace(" mi", "")
            binding.data = yearModelMakeData
            binding.dealData = submitDealData
            if (imageId == "0") {
                ll360.visibility = View.GONE
                llGallery.visibility = View.GONE
            }

            if (!TextUtils.isEmpty(intent.getStringExtra(Constant.ARG_MSRP_RANGE))) {
                tvPriceMSRP.text = intent.getStringExtra(Constant.ARG_MSRP_RANGE)
            } else {
                tvPriceMSRP.visibility = View.GONE
            }
            ucdList()
        }
        llGallery.setOnClickListener(this)
        ll360.setOnClickListener(this)
        tvViewOptionsLYK.setOnClickListener(this)
        btnModify.setOnClickListener(this)
        btnWait.setOnClickListener(this)
        tvLightingCar.setOnClickListener(this)
        tvStep2.setOnClickListener(this)
        ivBack.visibility = View.GONE
        tvTitleTool.visibility = View.GONE
    }

    private fun ucdList() {
        if (::submitDealData.isInitialized && !submitDealData.negativeResult?.ucdDeals.isNullOrEmpty()) {
            val arUcd: ArrayList<FindUcdDealData?> = ArrayList()
            arUcd.addAll(submitDealData.negativeResult?.ucdDeals!!)
            adapterUCD = Items_LinearRVAdapter(arUcd, this, false)
            adapterUCD.notifyDataSetChanged()
            rvUnlockedCar.adapter = adapterUCD
        }
    }

    override fun getViewActivity(): Activity {
        return this
    }

    override fun onNetworkStateChange(isConnect: Boolean) {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvLightingCar -> {
                callIsSoldAPI()
            }
            R.id.tvStep2 -> {
                callCheckVehicleStockAPI(true)
            }
            R.id.btnModify -> {
                callCheckVehicleStockAPI(false)
            }
            R.id.btnWait -> {
                callCheckVehicleStockAPI(false)
            }
            R.id.tvViewOptionsLYK -> {
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
            R.id.tvSelectDeal -> {
                val pos = v.tag as Int
                callCheckVehicleStockUCDAPI(adapterUCD.getItem(pos))
            }
            R.id.tvViewOptions -> {
                val pos = v.tag as Int
                popupOptionUCD(adapterUCD.getItem(pos))
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
                    arAccId.add(arOptions!![i].dealerAccessoryID!!)
                    if (isFirstAcce) {
                        isFirstAcce = false
                        accessoriesStr = arOptions!![i].accessory!!
                    } else
                        accessoriesStr += ",\n" + arOptions!![i].accessory!!
                }
                var packageStr = ""
                var isFirstPackage = true

                for (i in 0 until arPackages?.size!!) {
                    if (isFirstPackage) {
                        isFirstPackage = false
                        packageStr = arPackages!![i].packageName!!
                    } else {
                        packageStr = packageStr + ",\n" + arPackages!![i].packageName!!
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

    private fun callRefreshTokenApi(isLCD: Boolean, isRadius: Boolean) {
        if (Constant.isOnline(this)) {
            if (!Constant.isInitProgress()) {
                Constant.showLoader(this)
            } else if (Constant.isInitProgress() && !Constant.progress.isShowing) {
                Constant.showLoader(this)
            }
            val request = java.util.HashMap<String, Any>()
            request[ApiConstant.AuthToken] = pref?.getUserData()?.authToken!!
            request[ApiConstant.RefreshToken] = pref?.getUserData()?.refreshToken!!

            tokenModel.refresh(this, request)!!.observe(this, { data ->
                val userData = pref?.getUserData()
                userData?.authToken = data.auth_token!!
                userData?.refreshToken = data.refresh_token!!
                pref?.setUserData(Gson().toJson(userData))
            }
            )
        } else {
            Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    private fun callIsSoldAPI() {
        if (Constant.isOnline(this)) {
            isSoldViewModel.isSoldCall(this, submitDealData.negativeResult?.vehicleInventoryID!!)!!
                .observe(this, Observer { data ->
                    Constant.dismissLoader()
                    if (data) {
                        clearOneDealNearData()
                        startActivity(
                            intentFor<MainActivity>(
                                Constant.ARG_SEL_TAB to Constant.TYPE_ONE_DEAL_NEAR_YOU,
                                ARG_IS_LCD to true
                            ).clearTask()
                                .newTask()
                        )

                    } else {
                        setLCDPrefData()
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
        data.price = submitDealData.negativeResult?.lcdPrice
        data.msrp = yearModelMakeData.msrp
        data.arPackage = packageStr
        data.arAccessories = accessoriesStr
        data.arAccessoriesId = arAccId
        data.arPackageId = arPackageId
        data.dealID = submitDealData.negativeResult?.dealID
        data.guestID = dataPendingDeal.guestID
        data.vehicleInventoryID = submitDealData.negativeResult?.vehicleInventoryID

        startActivity(
            intentFor<LCDDealSummaryStep1Activity>(
                Constant.ARG_LCD_DEAL_GUEST to Gson().toJson(data),
                ARG_IS_LCD to true
            ).clearTask()
                .newTask()
        )
    }

    private fun clearOneDealNearData() {
        pref?.setOneDealNearYou("")
        pref?.setOneDealNearYouData(Gson().toJson(PrefOneDealNearYouData()))
    }

    private fun callCheckVehicleStockAPI(isRadius: Boolean) {
        if (Constant.isOnline(this)) {
            if (!Constant.isInitProgress()) {
                Constant.showLoader(this)
            } else if (Constant.isInitProgress() && !Constant.progress.isShowing) {
                Constant.showLoader(this)
            }
            val pkgList = JsonArray()
            for (i in 0 until yearModelMakeData.arPackages?.size!!) {
                pkgList.add(yearModelMakeData.arPackages!![i].vehiclePackageID)
            }
            val accList = JsonArray()
            for (i in 0 until yearModelMakeData.arOptions?.size!!) {
                accList.add(yearModelMakeData.arOptions!![i].dealerAccessoryID)
            }

            val request = HashMap<String, Any>()
            request[ApiConstant.Product] = 1
            request[ApiConstant.YearId1] = yearModelMakeData.vehicleYearID!!
            request[ApiConstant.MakeId1] = yearModelMakeData.vehicleMakeID!!
            request[ApiConstant.ModelID] = yearModelMakeData.vehicleModelID!!
            request[ApiConstant.TrimID] = yearModelMakeData.vehicleTrimID!!
            request[ApiConstant.ExteriorColorID] = yearModelMakeData.vehicleExtColorID!!
            request[ApiConstant.InteriorColorID] = yearModelMakeData.vehicleIntColorID!!
            if (isRadius) {
                request[ApiConstant.ZipCode1] = yearModelMakeData.zipCode!!
                request[ApiConstant.SearchRadius1] =
                    submitDealData.negativeResult?.minimalDistance!!
            }
            request[ApiConstant.AccessoryList] = accList
            request[ApiConstant.PackageList1] = pkgList
            //Log.e("RequestStock", Gson().toJson(request))
            checkVehicleStockViewModel.checkVehicleStockCall(this, request)!!
                .observe(this, Observer { data ->
                    Constant.dismissLoader()
                    if (data) {
                        if (isRadius) {
                            if (submitDealData.negativeResult?.secondLabel == "1") {
                                val prefSubmitPriceData = pref?.getSubmitPriceData()
                                prefSubmitPriceData?.radius =
                                    submitDealData.negativeResult?.minimalDistance!! + " mi"
                                pref?.setSubmitPriceData(Gson().toJson(prefSubmitPriceData))
                                val df = SimpleDateFormat("yyyy MM d, HH:mm:ss a")
                                val date = df.format(Calendar.getInstance().time)
                                pref?.setSubmitPriceTime(date)
                                pref?.setRadius(submitDealData.negativeResult?.minimalDistance!!)
                            }
                        }
                        finish()
                    } else {
                        if (isRadius) {
                            pref?.setSubmitPriceData(Gson().toJson(PrefSubmitPriceData()))
                            pref?.setSubmitPriceTime("")
                            startActivity(
                                intentFor<MainActivity>(ARG_IS_LYK_SHOW to true).clearTask()
                                    .newTask()
                            )
                        } else {
                            onBackPressed()
                        }
                    }
                }
                )
        } else {
            Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    private fun callCheckVehicleStockUCDAPI(data: FindUcdDealData) {
        if (Constant.isOnline(this)) {
            if (!Constant.isInitProgress()) {
                Constant.showLoader(this)
            } else if (Constant.isInitProgress() && !Constant.progress.isShowing) {
                Constant.showLoader(this)
            }
            val pkgList = JsonArray()
            for (i in 0 until data.vehiclePackages?.size!!) {
                pkgList.add(data.vehiclePackages!![i].vehiclePackageID)
            }
            val accList = JsonArray()
            for (i in 0 until data.vehicleAccessories?.size!!) {
                accList.add(data.vehicleAccessories!![i].dealerAccessoryID)
            }

            val request = HashMap<String, Any>()
            request[ApiConstant.Product] = 3
            request[ApiConstant.YearId1] = data.yearId!!
            request[ApiConstant.MakeId1] = data.makeId!!
            request[ApiConstant.ModelID] = data.modelId!!
            request[ApiConstant.TrimID] = data.trimId!!
            request[ApiConstant.ExteriorColorID] = data.exteriorColorId!!
            request[ApiConstant.InteriorColorID] = data.interiorColorId!!

            request[ApiConstant.ZipCode1] = data.zipCode!!
            request[ApiConstant.SearchRadius1] =
                data.searchRadius!!

            request[ApiConstant.AccessoryList] = accList
            request[ApiConstant.PackageList1] = pkgList
            //Log.e("RequestStock", Gson().toJson(request))
            checkVehicleStockViewModel.checkVehicleStockCall(this, request)!!
                .observe(this, Observer { dataStock ->
                    Constant.dismissLoader()
                    if (dataStock) {
                        setUCDStock(data)
                    } else {
                        startActivity(
                            intentFor<MainActivity>(
                                Constant.ARG_SEL_TAB to Constant.TYPE_SEARCH_DEAL,
                                ARG_IS_UCD to true
                            ).clearTask()
                                .newTask()
                        )
                    }
                }
                )
        } else {
            Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setUCDStock(data: FindUcdDealData) {
        yearModelMakeData.vehicleExtColorID = data.exteriorColorId
        yearModelMakeData.vehicleExtColorID = data.interiorColorId
        setPrefUcdDealData(data)
        startActivity<UCDDealSummaryStep2Activity>(
            Constant.ARG_UCD_DEAL to Gson().toJson(data),
            ARG_YEAR_MAKE_MODEL to Gson().toJson(yearModelMakeData),
            ARG_IMAGE_ID to imageId,
            ARG_IS_FROM_LYK to true
        )
    }

    private fun setPrefUcdDealData(data: FindUcdDealData) {
        val prefSearchDealData = PrefSearchDealData()
        prefSearchDealData.yearId = data.yearId
        prefSearchDealData.yearStr = data.vehicleYear
        prefSearchDealData.makeId = data.makeId
        prefSearchDealData.makeStr = data.vehicleMake
        prefSearchDealData.modelId = data.modelId
        prefSearchDealData.modelStr = data.vehicleModel
        prefSearchDealData.trimId = data.trimId
        prefSearchDealData.trimStr = data.vehicleTrim
        prefSearchDealData.extColorId = data.exteriorColorId
        prefSearchDealData.extColorStr = data.vehicleExteriorColor
        prefSearchDealData.intColorId = data.vehicleInventoryID
        prefSearchDealData.intColorStr = data.vehicleInteriorColor
        prefSearchDealData.zipCode = data.zipCode
        prefSearchDealData.searchRadius = yearModelMakeData.radius
        pref?.setSearchDealData(Gson().toJson(prefSearchDealData))
    }

    private fun popupOptionUCD(data: FindUcdDealData) {
        val dialog = Dialog(this, R.style.FullScreenDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setContentView(R.layout.dialog_option_accessories_unlocked)
        dialog.run {
            ivDialogClose.setOnClickListener {
                dismiss()
            }
            data.run {
                tvDialogVehicle.text = "$vehicleYear $vehicleMake $vehicleModel $vehicleTrim"
                tvDialogExteriorColor.text = vehicleExteriorColor
                tvDialogInterior.text = vehicleInteriorColor
                var packages = ""
                for (i in 0 until vehiclePackages?.size!!) {
                    packages = if (i == 0) {
                        vehiclePackages[i].packageName!!
                    } else {
                        packages + ",\n" + vehiclePackages[i].packageName!!
                    }
                }
                if (packages.isEmpty())
                    tvDialogPackages.visibility = View.GONE
                tvDialogPackages.text = packages

                var accessories = ""
                for (i in 0 until vehicleAccessories?.size!!) {
                    accessories = if (i == 0) {
                        vehicleAccessories[i].accessory!!
                    } else {
                        accessories + ",\n" + vehicleAccessories[i].accessory!!
                    }
                }
                if (accessories.isEmpty())
                    tvDialogOptions.visibility = View.GONE
                tvDialogOptions.text = accessories
                tvPrice.text = "Price: " + NumberFormat.getCurrencyInstance(Locale.US).format(price)

                if (AppGlobal.isNotEmpty(miles) || AppGlobal.isNotEmpty(condition)) {
                    if (AppGlobal.isNotEmpty(miles))
                        tvDialogDisclosure.text =
                            getString(R.string.miles_approximate_odometer_reading, miles)
                    if (AppGlobal.isNotEmpty(condition)) {
                        if (AppGlobal.isEmpty(miles)) {
                            tvDialogDisclosure.text = condition
                        } else {
                            tvDialogDisclosure.text =
                                tvDialogDisclosure.text.toString().trim() + ", " + condition
                        }
                    }
                    tvDialogDisclosure.visibility = View.VISIBLE
                    tvTitleDisclosure.visibility = View.VISIBLE
                } else {
                    tvDialogDisclosure.visibility = View.GONE
                    tvTitleDisclosure.visibility = View.GONE
                }
            }

        }
        setLayoutParam(dialog)
        dialog.show()
    }
}