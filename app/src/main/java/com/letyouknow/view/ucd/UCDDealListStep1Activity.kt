package com.letyouknow.view.ucd

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
import com.google.gson.reflect.TypeToken
import com.letyouknow.R
import com.letyouknow.base.BaseActivity
import com.letyouknow.databinding.ActivityUcdDealListStep1Binding
import com.letyouknow.model.FindUcdDealData
import com.letyouknow.model.YearModelMakeData
import com.letyouknow.retrofit.ApiConstant
import com.letyouknow.retrofit.viewmodel.ImageIdViewModel
import com.letyouknow.retrofit.viewmodel.ImageUrlViewModel
import com.letyouknow.retrofit.viewmodel.RefreshTokenViewModel
import com.letyouknow.utils.AppGlobal
import com.letyouknow.view.ucd.unlockeddealdetail.UCDDealSummaryStep2Activity
import com.pionymessenger.utils.Constant
import com.pionymessenger.utils.Constant.Companion.ARG_IMAGE_ID
import com.pionymessenger.utils.Constant.Companion.ARG_RADIUS
import com.pionymessenger.utils.Constant.Companion.ARG_UCD_DEAL
import com.pionymessenger.utils.Constant.Companion.ARG_YEAR_MAKE_MODEL
import com.pionymessenger.utils.Constant.Companion.ARG_ZIPCODE
import kotlinx.android.synthetic.main.activity_ucd_deal_list_step1.*
import kotlinx.android.synthetic.main.dialog_option_accessories_unlocked.*
import kotlinx.android.synthetic.main.layout_toolbar_blue.*
import kotlinx.android.synthetic.main.layout_toolbar_blue.toolbar
import org.jetbrains.anko.startActivity
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class UCDDealListStep1Activity : BaseActivity(), View.OnClickListener {
    private var arUnlocked: ArrayList<FindUcdDealData> = ArrayList()
    private var yearModelMakeData = YearModelMakeData()
    private lateinit var adapterUnlockedCarDeal: UnlockedCarDealAdapter
    private var searchRadius = ""
    private var zipCode = ""
    private var imageId = ""

    private lateinit var binding: ActivityUcdDealListStep1Binding

    private lateinit var tokenModel: RefreshTokenViewModel
    private lateinit var imageIdViewModel: ImageIdViewModel
    private lateinit var imageUrlViewModel: ImageUrlViewModel
    private var arImageUrl: ArrayList<String> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ucd_deal_list_step1)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ucd_deal_list_step1)
        init()
    }

    private fun init() {
        imageIdViewModel = ViewModelProvider(this)[ImageIdViewModel::class.java]
        imageUrlViewModel = ViewModelProvider(this)[ImageUrlViewModel::class.java]
        tokenModel = ViewModelProvider(this)[RefreshTokenViewModel::class.java]
        if (intent.hasExtra(ARG_UCD_DEAL) && intent.hasExtra(ARG_YEAR_MAKE_MODEL) && intent.hasExtra(
                ARG_RADIUS
            ) && intent.hasExtra(ARG_ZIPCODE)
        ) {
            arUnlocked = Gson().fromJson(
                intent.getStringExtra(ARG_UCD_DEAL),
                object : TypeToken<ArrayList<FindUcdDealData>?>() {}.type
            )
            if (arUnlocked.isNullOrEmpty()) {
                tvNotFound.visibility = View.VISIBLE
            }
            yearModelMakeData = Gson().fromJson(
                intent.getStringExtra(ARG_YEAR_MAKE_MODEL),
                YearModelMakeData::class.java
            )
            searchRadius = intent.getStringExtra(ARG_RADIUS)!!
            zipCode = intent.getStringExtra(ARG_ZIPCODE)!!
            binding.searchRadius = searchRadius.trim()
            binding.zipCode = zipCode.trim()
//            callRefreshTokenApi()
            callImageIdAPI()
        }
//        backButton()
        ivEdit.visibility = View.VISIBLE
        ivEdit.setOnClickListener(this)
        adapterUnlockedCarDeal = UnlockedCarDealAdapter(R.layout.list_item_unlocked_car, this)
        rvUnlockedCar.adapter = adapterUnlockedCarDeal
        adapterUnlockedCarDeal.addAll(arUnlocked)
        ivBack.setOnClickListener(this)
    }

    private fun backButton() {
        toolbar.setNavigationIcon(R.drawable.ic_back)
        toolbar.setTitleTextColor(resources.getColor(R.color.black))

        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
        }
    }


    override fun getViewActivity(): Activity? {
        return this
    }

    override fun onNetworkStateChange(isConnect: Boolean) {
    }

    private var selectPos = -1
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvSelectDeal -> {
                val pos = v.tag as Int
                if (selectPos != -1) {
                    val data = adapterUnlockedCarDeal.getItem(selectPos)
                    data.isSelect = false
                    adapterUnlockedCarDeal.update(selectPos, data)
                }

                val data = adapterUnlockedCarDeal.getItem(pos)
                data.isSelect = true
                adapterUnlockedCarDeal.update(pos, data)
                selectPos = pos

                startActivity<UCDDealSummaryStep2Activity>(
                    ARG_UCD_DEAL to Gson().toJson(data),
                    ARG_YEAR_MAKE_MODEL to Gson().toJson(yearModelMakeData),
                    ARG_IMAGE_ID to imageId
                )
            }
            R.id.tvViewOptions -> {
                val pos = v.tag as Int
                popupOption(adapterUnlockedCarDeal.getItem(pos))
            }
            R.id.ivBack -> {
                onBackPressed()
            }
            R.id.ivEdit -> {
                onBackPressed()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun popupOption(data: FindUcdDealData) {
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

                if (AppGlobal.isNotEmpty(miles)) {
                    tvDialogDisclosure.text =
                        getString(R.string.miles_approximate_odometer_reading, miles)
                } else {
                    tvDialogDisclosure.visibility = View.GONE
                    tvTitleDisclosure.visibility = View.GONE
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

    private fun callImageIdAPI() {
        if (Constant.isOnline(this)) {
            Constant.showLoader(this)
            val request = HashMap<String, Any>()
            yearModelMakeData.run {
                request[ApiConstant.vehicleYearID] = vehicleYearID!!
                request[ApiConstant.vehicleMakeID] = vehicleMakeID!!
                request[ApiConstant.vehicleModelID] = vehicleModelID!!
                request[ApiConstant.vehicleTrimID] = vehicleTrimID!!
            }
            Log.e("ImageId Request", Gson().toJson(request))
            imageIdViewModel.imageIdCall(this, request)!!
                .observe(this, Observer { data ->
                    Constant.dismissLoader()
                    imageId = data
                    callImageUrlAPI(data)
                }
                )

        } else {
            Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    private fun callImageUrlAPI(ImageId: String) {
        if (Constant.isOnline(this)) {
            Constant.showLoader(this)

            val request = HashMap<String, Any>()

            request[ApiConstant.ImageId] = ImageId!!
            request[ApiConstant.ImageProduct] = "Splash"

            imageUrlViewModel.imageUrlCall(this, request)!!
                .observe(this, Observer { data ->
                    Constant.dismissLoader()
                    if (data.isNotEmpty()) {
                        arImageUrl = data
                        AppGlobal.loadImageUrl(this, ivCar, arImageUrl[0])
                    }
                }
                )

        } else {
            Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    private fun callRefreshTokenApi() {
        if (Constant.isOnline(this)) {
            Constant.showLoader(this)
            val request = java.util.HashMap<String, Any>()
            request[ApiConstant.AuthToken] = pref?.getUserData()?.authToken!!
            request[ApiConstant.RefreshToken] = pref?.getUserData()?.refreshToken!!

            tokenModel.refresh(this, request)!!.observe(this, { data ->
                Constant.dismissLoader()
                val userData = pref?.getUserData()
                userData?.authToken = data.auth_token!!
                userData?.refreshToken = data.refresh_token!!
                pref?.setUserData(Gson().toJson(userData))
                callImageIdAPI()
            }
            )
        } else {
            Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        if (Constant.isInitProgress() && Constant.progress.isShowing)
            Constant.dismissLoader()
        super.onDestroy()
    }
}