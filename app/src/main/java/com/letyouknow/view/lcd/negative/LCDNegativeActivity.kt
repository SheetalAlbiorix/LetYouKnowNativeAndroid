package com.letyouknow.view.lcd.negative

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
import com.letyouknow.databinding.ActivityLcdNegativeBinding
import com.letyouknow.model.FindLCDDeaData
import com.letyouknow.model.PrefOneDealNearYouData
import com.letyouknow.retrofit.ApiConstant
import com.letyouknow.retrofit.viewmodel.CheckVehicleStockViewModel
import com.letyouknow.utils.AppGlobal
import com.letyouknow.view.dashboard.MainActivity
import com.letyouknow.view.gallery360view.Gallery360TabActivity
import com.pionymessenger.utils.Constant
import kotlinx.android.synthetic.main.activity_lcd_negative.*
import kotlinx.android.synthetic.main.dialog_option_accessories.*
import kotlinx.android.synthetic.main.layout_toolbar_blue.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import org.jetbrains.anko.startActivity

class LCDNegativeActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivityLcdNegativeBinding
    private lateinit var data: FindLCDDeaData
    private lateinit var arImage: ArrayList<String>
    private var imageId = "0"
    private var isShowPer = false
    private lateinit var checkVehicleStockViewModel: CheckVehicleStockViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lcd_negative)
        init()
    }

    private fun init() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_lcd_negative)
        binding.title = ""
        checkVehicleStockViewModel =
            ViewModelProvider(this).get(CheckVehicleStockViewModel::class.java)
        if (intent.hasExtra(Constant.ARG_SUBMIT_DEAL) && intent.hasExtra(Constant.ARG_IMAGE_URL) && intent.hasExtra(
                Constant.ARG_IMAGE_ID
            ) && intent.hasExtra(
                Constant.ARG_IS_SHOW_PER
            )
        ) {
            isShowPer = intent.getBooleanExtra(Constant.ARG_IS_SHOW_PER, false)
            if (isShowPer) {
                tvMessage.text = "Market's hot! The car is no longer available."
            } else {
                tvMessage.text =
                    "Something went wrong, but don't worry your card hasn't been charged"
            }
            data = Gson().fromJson(
                intent.getStringExtra(Constant.ARG_SUBMIT_DEAL),
                FindLCDDeaData::class.java
            )
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
            binding.ucdData = data
            tvViewOptions.setOnClickListener(this)
            if (imageId == "0") {
                llGallery.visibility = View.GONE
                ll360.visibility = View.GONE
            }
        }
        ivBack.visibility = View.GONE
        ivBack.setOnClickListener(this)
        btnTryAgain.setOnClickListener(this)
        llGallery.setOnClickListener(this)
        ll360.setOnClickListener(this)
    }

    override fun getViewActivity(): Activity? {
        return this
    }

    override fun onNetworkStateChange(isConnect: Boolean) {
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
                data.yearStr + " " + data.makeStr + " " + data.modelStr + " " + data.trimStr
            tvDialogExteriorColor.text = data.exteriorColorStr
            tvDialogInteriorColor.text = data.interiorColorStr
            tvDialogPackage.text = data.arPackage
            tvDialogOptions.text = data.arAccessories
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

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvViewOptions -> {
                popupOption()
            }
            R.id.ivBack -> {
                onBackPressed()
            }
            R.id.btnTryAgain -> {
                callCheckVehicleStockAPI()
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
        callCheckVehicleStockAPI()
    }

    private fun callCheckVehicleStockAPI() {
        if (Constant.isOnline(this)) {
            if (!Constant.isInitProgress()) {
                Constant.showLoader(this)
            } else if (!Constant.progress.isShowing) {
                Constant.showLoader(this)
            }
            val pkgList = JsonArray()
            for (i in 0 until data.arPackageId.size) {
                pkgList.add(data.arPackageId[i])
            }
            val accList = JsonArray()
            for (i in 0 until data.arAccessoriesId.size) {
                accList.add(data.arAccessoriesId[i])
            }

            val request = HashMap<String, Any>()
            request[ApiConstant.Product] = 2
            request[ApiConstant.YearId1] = data.yearId!!
            request[ApiConstant.MakeId1] = data.makeId!!
            request[ApiConstant.ModelID] = data.modelId!!
            request[ApiConstant.TrimID] = data.trimId!!
            request[ApiConstant.ExteriorColorID] = data.exteriorColorId!!
            request[ApiConstant.InteriorColorID] = data.interiorColorId!!
            request[ApiConstant.ZipCode1] = data.zipCode!!
            request[ApiConstant.SearchRadius1] = "100"
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
                        clearOneDealNearData()
                        startActivity(
                            intentFor<MainActivity>(Constant.ARG_SEL_TAB to Constant.TYPE_ONE_DEAL_NEAR_YOU).clearTask()
                                .newTask()
                        )
                    }
                }
                )
        } else {
            Toast.makeText(this, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    private fun clearOneDealNearData() {
        pref?.setOneDealNearYouData(Gson().toJson(PrefOneDealNearYouData()))
        pref?.setOneDealNearYou("")
    }
}