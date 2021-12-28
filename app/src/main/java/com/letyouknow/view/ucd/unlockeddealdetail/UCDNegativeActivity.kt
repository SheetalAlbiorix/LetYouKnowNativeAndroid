package com.letyouknow.view.ucd.unlockeddealdetail

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
import com.letyouknow.databinding.ActivityUcdNegativeBinding
import com.letyouknow.model.FindUcdDealData
import com.letyouknow.model.PrefSearchDealData
import com.letyouknow.model.YearModelMakeData
import com.letyouknow.retrofit.ApiConstant
import com.letyouknow.retrofit.viewmodel.CheckVehicleStockViewModel
import com.letyouknow.utils.AppGlobal
import com.letyouknow.view.dashboard.MainActivity
import com.letyouknow.view.gallery360view.Gallery360TabActivity
import com.pionymessenger.utils.Constant
import kotlinx.android.synthetic.main.activity_ucd_negative.*
import kotlinx.android.synthetic.main.dialog_option_accessories.*
import kotlinx.android.synthetic.main.layout_toolbar_blue.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import org.jetbrains.anko.startActivity

class UCDNegativeActivity : BaseActivity(), View.OnClickListener {
    lateinit var binding: ActivityUcdNegativeBinding
    private lateinit var arImage: ArrayList<String>
    private lateinit var ucdData: FindUcdDealData
    private lateinit var yearModelMakeData: YearModelMakeData
    private var imageId = "0"
    private lateinit var checkVehicleStockViewModel: CheckVehicleStockViewModel
    private var isShowPer = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ucd_negative)
        init()
    }

    private fun init() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ucd_negative)
        binding.title = ""
        checkVehicleStockViewModel =
            ViewModelProvider(this).get(CheckVehicleStockViewModel::class.java)
        if (intent.hasExtra(
                Constant.ARG_YEAR_MAKE_MODEL
            ) && intent.hasExtra(
                Constant.ARG_IMAGE_URL
            )
        ) {
            isShowPer = intent.getBooleanExtra(Constant.ARG_IS_SHOW_PER, false)
            if (isShowPer) {
                tvMessage.text = "Market's hot! The car is no longer available."
            } else {
                tvMessage.text =
                    "Something went wrong, but don't worry your card hasn't been charged"
            }
            imageId = intent.getStringExtra(Constant.ARG_IMAGE_ID)!!
            arImage = Gson().fromJson(
                intent.getStringExtra(Constant.ARG_IMAGE_URL),
                object : TypeToken<ArrayList<String>?>() {}.type
            )
            if (arImage.size != 0) {
                AppGlobal.loadImageUrl(this, ivMain, arImage[0])
                AppGlobal.loadImageUrl(this, ivBgGallery, arImage[0])
                AppGlobal.loadImageUrl(this, ivBg360, arImage[0])
            }
            ucdData =
                Gson().fromJson(
                    intent.getStringExtra(Constant.ARG_UCD_DEAL),
                    FindUcdDealData::class.java
                )
            yearModelMakeData = Gson().fromJson(
                intent.getStringExtra(Constant.ARG_YEAR_MAKE_MODEL),
                YearModelMakeData::class.java
            )
            binding.ucdData = yearModelMakeData
            ivBack.visibility = View.GONE
            ivBack.setOnClickListener(this)
            ll360.setOnClickListener(this)
            llGallery.setOnClickListener(this)
            tvViewOptions.setOnClickListener(this)
            btnTryAgain.setOnClickListener(this)
            if (imageId == "0") {
                ll360.visibility = View.GONE
                llGallery.visibility = View.GONE
            }
        }
    }

    override fun getViewActivity(): Activity {
        return this
    }

    override fun onNetworkStateChange(isConnect: Boolean) {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
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
            R.id.ivBack -> {
                onBackPressed()
            }
            R.id.btnTryAgain -> {
                onBackPressed()
            }
        }
    }

    override fun onBackPressed() {
        callCheckVehicleStockAPI()

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
            ucdData.run {
                tvDialogTitle.text =
                    "$vehicleYear $vehicleMake $vehicleModel $vehicleTrim"
                tvDialogExteriorColor.text = vehicleExteriorColor
                tvDialogInteriorColor.text = vehicleInteriorColor
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

                var packages = ""
                for (i in 0 until vehiclePackages?.size!!) {
                    packages = if (i == 0) {
                        vehiclePackages[i].packageName!!
                    } else {
                        packages + ",\n" + vehiclePackages[i].packageName!!
                    }
                }
                if (packages.isEmpty())
                    tvDialogPackage.visibility = View.GONE

                tvDialogPackage.text = packages
                tvDialogOptions.text = accessories
                if (AppGlobal.isNotEmpty(miles)) {
                    tvDialogDisclosure.text =
                        getString(R.string.miles_approximate_odometer_reading, miles)
                    llDialogDisclosure.visibility = View.VISIBLE
                } else {
                    llDialogDisclosure.visibility = View.GONE
                }
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

    private fun callCheckVehicleStockAPI() {
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
            request[ApiConstant.Product] = 3
            request[ApiConstant.YearId1] = yearModelMakeData.vehicleYearID!!
            request[ApiConstant.MakeId1] = yearModelMakeData.vehicleMakeID!!
            request[ApiConstant.ModelID] = yearModelMakeData.vehicleModelID!!
            request[ApiConstant.TrimID] = yearModelMakeData.vehicleTrimID!!
            request[ApiConstant.ExteriorColorID] = yearModelMakeData.vehicleExtColorID!!
            request[ApiConstant.InteriorColorID] = yearModelMakeData.vehicleIntColorID!!
            request[ApiConstant.ZipCode1] = yearModelMakeData.zipCode!!
            request[ApiConstant.SearchRadius1] =
                if (yearModelMakeData.radius == "ALL") "6000" else yearModelMakeData.radius?.replace(
                    " mi",
                    ""
                )!!
            request[ApiConstant.AccessoryList] = accList
            request[ApiConstant.PackageList1] = pkgList
            Log.e("RequestStock", Gson().toJson(request))
            checkVehicleStockViewModel.checkVehicleStockCall(this, request)!!
                .observe(this, Observer { data ->
                    Constant.dismissLoader()
                    if (data) {
                        startActivity(
                            intentFor<MainActivity>(Constant.ARG_SEL_TAB to Constant.TYPE_SEARCH_DEAL).clearTask()
                                .newTask()
                        )
                    } else {
                        clearPrefSearchDealData()
                        startActivity(
                            intentFor<MainActivity>(Constant.ARG_SEL_TAB to Constant.TYPE_SEARCH_DEAL).clearTask()
                                .newTask()
                        )
                    }
                }
                )
        } else {
            Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    private fun clearPrefSearchDealData() {
        pref?.setSearchDealData(Gson().toJson(PrefSearchDealData()))
        pref?.setSearchDealTime("")
    }
}