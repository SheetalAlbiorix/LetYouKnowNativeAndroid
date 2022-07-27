package com.letyouknow.retrofit.viewmodel

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Handler
import android.text.InputFilter
import android.text.TextUtils
import android.view.*
import android.widget.EditText
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.letyouknow.R
import com.letyouknow.model.*
import com.letyouknow.retrofit.ApiConstant
import com.letyouknow.retrofit.RetrofitClient
import com.letyouknow.utils.AppGlobal
import com.letyouknow.utils.AppGlobal.Companion.pref
import com.letyouknow.utils.AppGlobal.Companion.setLayoutParam
import com.letyouknow.utils.AppGlobal.Companion.showWarningDialog
import com.letyouknow.utils.Constant
import com.letyouknow.view.dropdown.*
import com.letyouknow.view.lcd.summary.LCDDealSummaryStep1Activity
import com.letyouknow.view.spinneradapter.OptionsAdapter
import com.letyouknow.view.spinneradapter.PackagesAdapter
import kotlinx.android.synthetic.main.dialog_mobile_no.*
import kotlinx.android.synthetic.main.dialog_vehicle_options.*
import kotlinx.android.synthetic.main.dialog_vehicle_packages.*
import kotlinx.android.synthetic.main.popup_dialog.view.*
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.jetbrains.anko.startActivity
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class LCDViewModel : ViewModel() {

    lateinit var adapterYear: YearAdapter
    lateinit var adapterMake: MakeAdapter
    lateinit var adapterModel: ModelAdapter
    lateinit var adapterTrim: TrimAdapter
    lateinit var adapterExterior: ExteriorColorAdapter
    lateinit var adapterInterior: InteriorColorAdapter
    lateinit var adapterPackages: PackagesAdapter
    lateinit var adapterOptions: OptionsAdapter


    private lateinit var context: Activity

    lateinit var popupYear: PopupWindow
    lateinit var popupMake: PopupWindow
    lateinit var popupModel: PopupWindow
    lateinit var popupTrim: PopupWindow
    lateinit var popupExterior: PopupWindow
    lateinit var popupInterior: PopupWindow

    var hasMatchPackage = false
    var hasMatchOptions = false

    lateinit var click: View.OnClickListener
    lateinit var prefLCDData: PrefOneDealNearYouData
    lateinit var handler: Handler

    var productId = "2"
    var isValidZipCode = false
    var zipCode = ""
    var yearId = ""
    var makeId = ""
    var modelId = ""
    var trimId = ""
    var extColorId = ""
    var intColorId = ""

    var yearStr = ""
    var makeStr = ""
    var modelStr = ""
    var trimStr = ""
    var extColorStr = ""
    var intColorStr = ""

    lateinit var edtZipCode: EditText
    lateinit var tvYear: TextView
    lateinit var tvMake: TextView
    lateinit var tvModel: TextView
    lateinit var tvTrim: TextView
    lateinit var tvExtColor: TextView
    lateinit var tvIntColor: TextView
    lateinit var tvPackages: TextView
    lateinit var tvOptions: TextView

    var liveDataPromotion: MutableLiveData<PromotionData>? = MutableLiveData()
    var liveDataZipCode: MutableLiveData<Boolean>? = MutableLiveData()

    fun init(
        context: Activity,
        click: View.OnClickListener,
        edtZipCode: EditText,
        tvYear: TextView,
        tvMake: TextView,
        tvModel: TextView,
        tvTrim: TextView,
        tvExtColor: TextView,
        tvIntColor: TextView,
        tvPackages: TextView,
        tvOptions: TextView
    ) {
        this.context = context
        this.click = click

        this.edtZipCode = edtZipCode
        this.tvYear = tvYear
        this.tvMake = tvMake
        this.tvModel = tvModel
        this.tvTrim = tvTrim
        this.tvExtColor = tvExtColor
        this.tvIntColor = tvIntColor
        this.tvPackages = tvPackages
        this.tvOptions = tvOptions
    }

    fun callVehicleZipCodeAPI(
        zipCode: String?
    ) {
        Constant.showLoader(context)
        try {
            viewModelScope.launch {
                val response: Deferred<Response<Boolean>> = async {
                    RetrofitClient.apiInterface.isValidZip2(
                        zipCode
                    )
                }
                val data = response.await()
                Constant.dismissLoader()
                if (data.isSuccessful) {
                    liveDataZipCode?.postValue(data.body())
                } else {
                    liveDataZipCode?.postValue(false)
                }
            }
        } catch (e: Exception) {

        }
    }

    fun getYear(
        v: View?
    ) {
        Constant.showLoader(context)
        try {
            viewModelScope.launch {

                val response: Deferred<Response<ArrayList<VehicleYearData>>> = async {
                    RetrofitClient.apiInterface.getVehicleYears2(
                        productId,
                        zipCode,
                        null,
                        null
                    )

                }
                val data = response.await()
                Constant.dismissLoader()
                if (data.isSuccessful) {
                    if (data.body().isNullOrEmpty())
                        showWarningDialog(context)
                    else
                        showYearPopUp(v, data.body()!!)
                } else {
                    showWarningDialog(context)
                }
            }
        } catch (e: Exception) {

        }
    }

    fun getMake(
        view: View?
    ) {
        Constant.showLoader(context)

        try {
            viewModelScope.launch {
                val response: Deferred<Response<ArrayList<VehicleMakeData>>> = async {
                    RetrofitClient.apiInterface.getVehicleMake2(
                        productId,
                        yearId,
                        zipCode,
                        null,
                        null
                    )
                }
                val data = response.await()
                Constant.dismissLoader()
                if (data.isSuccessful) {
                    if (data.body().isNullOrEmpty())
                        showWarningDialog(context)
                    else
                        showMakePopUp(view, data.body()!!)
                } else {
                    showWarningDialog(context)
                }
            }
        } catch (e: Exception) {

        }
    }

    fun getModel(
        view: View?,
    ) {
        Constant.showLoader(context)

        try {
            viewModelScope.launch {
                val response: Deferred<Response<ArrayList<VehicleModelData>>> = async {
                    RetrofitClient.apiInterface.getVehicleModels2(
                        productId,
                        yearId,
                        makeId,
                        zipCode,
                        null,
                        null
                    )
                }
                val data = response.await()
                Constant.dismissLoader()
                if (data.isSuccessful) {
                    if (data.body().isNullOrEmpty())
                        showWarningDialog(context)
                    else
                        showModelPopUp(view, data.body()!!)
                } else {
                    showWarningDialog(context)
                }
            }
        } catch (e: Exception) {

        }
    }

    fun getTrim(
        view: View?
    ) {
        Constant.showLoader(context)

        try {
            viewModelScope.launch {
                val response: Deferred<Response<ArrayList<VehicleTrimData>>> = async {
                    RetrofitClient.apiInterface.getVehicleTrims2(
                        productId,
                        yearId,
                        makeId,
                        modelId,
                        zipCode,
                        null,
                        null
                    )
                }
                val data = response.await()
                Constant.dismissLoader()
                if (data.isSuccessful) {
                    if (data.body().isNullOrEmpty())
                        showWarningDialog(context)
                    else
                        showTrimPopUp(view, data.body()!!)
                } else {
                    showWarningDialog(context)
                }
            }
        } catch (e: Exception) {

        }
    }

    fun getExteriorColor(
        view: View?
    ) {
        Constant.showLoader(context)
        viewModelScope.launch {
            val response: Deferred<Response<ArrayList<ExteriorColorData>>> = async {
                RetrofitClient.apiInterface.getVehicleExteriorColors2(
                    productId,
                    yearId,
                    makeId,
                    modelId,
                    trimId,
                    zipCode,
                    null,
                    null
                )

            }
            val data = response.await()
            Constant.dismissLoader()
            if (data.isSuccessful) {
                if (data.body().isNullOrEmpty()) {
                    val extData = ExteriorColorData()
                    extData.exteriorColor = "ANY"
                    extData.vehicleExteriorColorID = "0"
                    showExteriorPopUp(view, arrayListOf(extData))
                } else {
                    val extData = ExteriorColorData()
                    extData.exteriorColor = "ANY"
                    extData.vehicleExteriorColorID = "0"
                    data.body()?.add(0, extData)
                    showExteriorPopUp(view, data.body()!!)
                }
            } else {
                val extData = ExteriorColorData()
                extData.exteriorColor = "ANY"
                extData.vehicleExteriorColorID = "0"
                showExteriorPopUp(view, arrayListOf(extData))
            }
        }

    }

    fun getInteriorColor(
        view: View?
    ) {
        Constant.showLoader(context)

        viewModelScope.launch {
            val response: Deferred<Response<ArrayList<InteriorColorData>>> = async {
                RetrofitClient.apiInterface.getVehicleInteriorColors2(
                    productId,
                    yearId,
                    makeId,
                    modelId,
                    trimId,
                    extColorId,
                    zipCode,
                    null,
                    null
                )

            }
            val data = response.await()
            Constant.dismissLoader()
            if (data.isSuccessful) {
                if (data.body().isNullOrEmpty()) {
                    val intData = InteriorColorData()
                    intData.interiorColor = "Any"
                    intData.vehicleInteriorColorID = "0"
                    showInteriorPopUp(view, arrayListOf(intData))
                } else {
                    val intData = InteriorColorData()
                    intData.interiorColor = "Any"
                    intData.vehicleInteriorColorID = "0"
                    data.body()?.add(0, intData)
                    showInteriorPopUp(view, data.body()!!)
                }
            } else {
                val intData = InteriorColorData()
                intData.interiorColor = "Any"
                intData.vehicleInteriorColorID = "0"
                showInteriorPopUp(view, arrayListOf(intData))
            }
        }
    }

    fun getPackages(
    ) {
        Constant.showLoader(context)

        viewModelScope.launch {
            val response: Deferred<Response<ArrayList<VehiclePackagesData>>> = async {
                RetrofitClient.apiInterface.getVehiclePackages2(
                    productId,
                    yearId,
                    makeId,
                    modelId,
                    trimId,
                    extColorId,
                    intColorId,
                    zipCode
                )

            }
            val data = response.await()
            Constant.dismissLoader()
            if (data.isSuccessful) {
                if (data.body().isNullOrEmpty()) {
                    val packageData = VehiclePackagesData()
                    packageData.packageName = "Any"
                    packageData.vehiclePackageID = "0"
                    popupPackages(arrayListOf(packageData))

                } else {
                    val packageData = VehiclePackagesData()
                    packageData.packageName = "Any"
                    packageData.vehiclePackageID = "0"
                    data.body()?.add(0, packageData)
                    popupPackages(data.body()!!)
                }
            } else {
                val packageData = VehiclePackagesData()
                packageData.packageName = "Any"
                packageData.vehiclePackageID = "0"
                popupPackages(arrayListOf(packageData))
            }
        }
    }

    fun getOptionsAccessories(
    ) {
        Constant.showLoader(context)
        val jsonArray = JsonArray()
        if (!prefLCDData.packagesData.isNullOrEmpty()) {
            for (i in 0 until prefLCDData.packagesData?.size!!) {
                if (prefLCDData.packagesData!![i].isSelect!! || prefLCDData.packagesData!![i].isOtherSelect!!) {
                    jsonArray.add(prefLCDData.packagesData!![i].vehiclePackageID)
                }
            }
        }
        val request = HashMap<String, Any>()
        request[ApiConstant.packageList] = jsonArray
        request[ApiConstant.productId] = productId
        request[ApiConstant.yearId] = prefLCDData.yearId!!
        request[ApiConstant.makeId] = prefLCDData.makeId!!
        request[ApiConstant.modelId] = prefLCDData.modelId!!
        request[ApiConstant.trimId] = prefLCDData.trimId!!
        request[ApiConstant.exteriorColorId] = prefLCDData.extColorId!!
        request[ApiConstant.interiorColorId] = prefLCDData.intColorId!!
        request[ApiConstant.zipCode] = zipCode
        viewModelScope.launch {
            val response: Deferred<Response<ArrayList<VehicleAccessoriesData>>> = async {
                RetrofitClient.apiInterface.getVehicleDealerAccessories2(
                    request
                )
            }
            val data = response.await()
            Constant.dismissLoader()
            if (data.isSuccessful) {
                if (data.body().isNullOrEmpty()) {
                    val optionsData = VehicleAccessoriesData()
                    optionsData.accessory = "Any"
                    optionsData.dealerAccessoryID = "0"
                    popupOptions(arrayListOf(optionsData))

                } else {
                    val optionsData = VehicleAccessoriesData()
                    optionsData.accessory = "Any"
                    optionsData.dealerAccessoryID = "0"
                    data.body()?.add(0, optionsData)
                    popupOptions(data.body()!!)
                }
            } else {
                val optionsData = VehicleAccessoriesData()
                optionsData.accessory = "Any"
                optionsData.dealerAccessoryID = "0"
                popupOptions(arrayListOf(optionsData))
            }
        }
    }

    fun callCheckedPackageAPI(
    ) {
        Constant.showLoader(context)

        viewModelScope.launch {
            val response: Deferred<Response<CheckedPackageData>> = async {
                val jsonArray = JsonArray()
                for (i in 0 until adapterPackages.itemCount) {
                    if (adapterPackages.getItem(i).isSelect!! || adapterPackages.getItem(i).isOtherSelect!!) {
                        jsonArray.add(adapterPackages.getItem(i).vehiclePackageID)
                    }
                }
                val request = HashMap<String, Any>()
                request[ApiConstant.checkedList] = jsonArray
                request[ApiConstant.productId] = productId
                request[ApiConstant.yearId] = yearId
                request[ApiConstant.makeId] = makeId
                request[ApiConstant.modelId] = modelId
                request[ApiConstant.trimId] = trimId
                request[ApiConstant.exteriorColorId] = extColorId
                request[ApiConstant.interiorColorId] = intColorId
                request[ApiConstant.zipCode] = zipCode

                RetrofitClient.apiInterface.checkVehiclePackagesInventory2(
                    request
                )

            }
            val data = response.await()
            Constant.dismissLoader()
            if (data.isSuccessful) {
                hasMatchPackage = data.body()?.hasMatch!!
                Constant.dismissLoader()
                if (data.body()?.status == 1) {
                    if (::dialogPackage.isInitialized)
                        dialogPackage.dismiss()
                    AppGlobal.alertError(context, context.getString(R.string.market_hot_msg))
                    tvPackages.text = context.getString(R.string.packages_title)
                    prefLCDData = pref?.getOneDealNearYouData()!!
                    prefLCDData.packagesData = java.util.ArrayList()
                    pref?.setOneDealNearYouData(Gson().toJson(prefLCDData))
                    setCurrentTime()
                } else {
                    if (data.body()?.status == 0 || data.body()?.status == 3) {
                        showHighlightedDialog()
                    }
                    if (!data.body()?.autoCheckList.isNullOrEmpty()) {
                        for (i in 0 until data.body()?.autoCheckList?.size!!) {
                            for (j in 0 until adapterPackages.itemCount) {
                                if (adapterPackages.getItem(j).vehiclePackageID == data.body()?.autoCheckList?.get(
                                        i
                                    )
                                ) {
                                    val dataCheck = adapterPackages.getItem(j)
                                    dataCheck.isGray = false
                                    dataCheck.isOtherSelect = true
                                    adapterPackages.update(j, dataCheck)
                                }
                            }
                        }
                    }
                    if (!data.body()?.grayOutList.isNullOrEmpty()) {
                        for (i in 0 until data.body()?.grayOutList?.size!!) {
                            for (j in 0 until adapterPackages.itemCount) {
                                if (adapterPackages.getItem(j).vehiclePackageID == data.body()?.grayOutList!![i]) {
                                    val dataGray = adapterPackages.getItem(j)
                                    dataGray.isGray = true
                                    adapterPackages.update(j, dataGray)
                                }
                            }
                        }
                    }
                }
            } else {

            }
        }
    }

    fun callCheckedOptionsAPI(
    ) {
        Constant.showLoader(context)

        viewModelScope.launch {
            val response: Deferred<Response<CheckedPackageData>> = async {
                val jsonArray = JsonArray()
                for (i in 0 until adapterOptions.itemCount) {
                    if (adapterOptions.getItem(i).isSelect!! || adapterOptions.getItem(i).isOtherSelect!!) {
                        jsonArray.add(adapterOptions.getItem(i).dealerAccessoryID)
                    }
                }
                val jsonArrayPackage = JsonArray()
                for (i in 0 until prefLCDData.packagesData!!.size) {
                    if (prefLCDData.packagesData!![i].isSelect!! || prefLCDData.packagesData!![i].isOtherSelect!!) {
                        jsonArrayPackage.add(prefLCDData.packagesData!![i].vehiclePackageID)
                    }
                }
                val request = HashMap<String, Any>()
                request[ApiConstant.packageList] = jsonArrayPackage
                request[ApiConstant.checkedList] = jsonArray
                request[ApiConstant.productId] = productId
                request[ApiConstant.yearId] = prefLCDData.yearId!!
                request[ApiConstant.makeId] = prefLCDData.makeId!!
                request[ApiConstant.modelId] = prefLCDData.modelId!!
                request[ApiConstant.trimId] = prefLCDData.trimId!!
                request[ApiConstant.exteriorColorId] = prefLCDData.extColorId!!
                request[ApiConstant.interiorColorId] = prefLCDData.intColorId!!
                request[ApiConstant.zipCode] = zipCode

                RetrofitClient.apiInterface.checkVehicleAccessoriesInventory2(
                    request
                )

            }
            val data = response.await()
            Constant.dismissLoader()
            if (data.isSuccessful) {
                hasMatchOptions = data.body()?.hasMatch!!
                if (data.body()?.status == 1) {
                    if (::dialogOptions.isInitialized)
                        dialogOptions.dismiss()
                    AppGlobal.alertError(context, context.getString(R.string.market_hot_msg))
                    tvOptions.text = context.getString(R.string.options_accessories_title)
                    prefLCDData = pref?.getOneDealNearYouData()!!
                    prefLCDData.optionsData = java.util.ArrayList()
                    pref?.setOneDealNearYouData(Gson().toJson(prefLCDData))
                    setCurrentTime()
                } else {
                    if (data.body()!!.status == 0 || data.body()!!.status == 3) {
                        showHighlightedDialog()
                    }
                    if (!data.body()!!.autoCheckList.isNullOrEmpty()) {
                        for (i in 0 until data.body()!!.autoCheckList.size) {
                            for (j in 0 until adapterOptions.itemCount) {
                                if (adapterOptions.getItem(j).dealerAccessoryID == data.body()!!.autoCheckList[i]) {
                                    val dataCheck = adapterOptions.getItem(j)
                                    dataCheck.isGray = false
                                    dataCheck.isOtherSelect = true
                                    adapterOptions.update(j, dataCheck)
                                }
                            }
                        }
                    }
                    if (!data.body()!!.grayOutList.isNullOrEmpty()) {
                        for (i in 0 until data.body()!!.grayOutList.size) {
                            for (j in 0 until adapterOptions.itemCount) {
                                if (adapterOptions.getItem(j).dealerAccessoryID == data.body()!!.grayOutList[i]) {
                                    val dataGray = adapterOptions.getItem(j)
                                    dataGray.isGray = true
                                    adapterOptions.update(j, dataGray)
                                }
                            }
                        }
                    }
                }
            } else {

            }
        }
    }

    fun showYearPopUp(v: View?, list: ArrayList<VehicleYearData>) {
        val inflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.popup_dialog, null)

        popupYear = PopupWindow(
            view,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        popupTouchable(popupYear)
        popupYear.setBackgroundDrawable(context.resources.getDrawable(R.drawable.bg_dialog))

        adapterYear = YearAdapter(R.layout.list_item_year, click)
        view?.rvPopup?.adapter = adapterYear
        adapterYear.addAll(list)
        popupYear.showAsDropDown(v, 100, -10, Gravity.CENTER)
    }

    fun showMakePopUp(v: View?, list: ArrayList<VehicleMakeData>) {
        val inflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.popup_dialog, null)

        popupMake = PopupWindow(
            view,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        popupTouchable(popupMake)
        popupMake.setBackgroundDrawable(context.resources.getDrawable(R.drawable.bg_dialog))

        adapterMake = MakeAdapter(R.layout.list_item_make, click)
        view?.rvPopup?.adapter = adapterMake
        adapterMake.addAll(list)
        popupMake.showAsDropDown(v, 100, -10, Gravity.CENTER)
    }

    fun showModelPopUp(v: View?, list: ArrayList<VehicleModelData>) {
        val inflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.popup_dialog, null)

        popupModel = PopupWindow(
            view,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        popupTouchable(popupModel)
        popupModel.setBackgroundDrawable(context.resources.getDrawable(R.drawable.bg_dialog))

        adapterModel = ModelAdapter(R.layout.list_item_model, click)
        view?.rvPopup?.adapter = adapterModel
        adapterModel.addAll(list)
        popupModel.showAsDropDown(v, 100, -10, Gravity.CENTER)
    }

    fun showTrimPopUp(v: View?, list: ArrayList<VehicleTrimData>) {
        val inflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.popup_dialog, null)

        popupTrim = PopupWindow(
            view,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        popupTouchable(popupTrim)
        popupTrim.setBackgroundDrawable(context.resources.getDrawable(R.drawable.bg_dialog))

        adapterTrim = TrimAdapter(R.layout.list_item_trim, click)
        view?.rvPopup?.adapter = adapterTrim
        adapterTrim.addAll(list)
        popupTrim.showAsDropDown(v, 100, -10, Gravity.CENTER)
    }

    fun showExteriorPopUp(v: View?, list: ArrayList<ExteriorColorData>) {
        val inflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.popup_dialog, null)

        popupExterior = PopupWindow(
            view,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        popupTouchable(popupExterior)
        popupExterior.setBackgroundDrawable(context.resources.getDrawable(R.drawable.bg_dialog))

        adapterExterior = ExteriorColorAdapter(R.layout.list_item_exterior_color, click)
        view?.rvPopup?.adapter = adapterExterior
        adapterExterior.addAll(list)
        popupExterior.showAsDropDown(v, 100, -10, Gravity.CENTER)
    }

    fun showInteriorPopUp(v: View?, list: ArrayList<InteriorColorData>) {
        val inflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.popup_dialog, null)

        popupInterior = PopupWindow(
            view,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        popupTouchable(popupInterior)
        popupInterior.setBackgroundDrawable(context.resources.getDrawable(R.drawable.bg_dialog))

        adapterInterior = InteriorColorAdapter(R.layout.list_item_interior_color, click)
        view?.rvPopup?.adapter = adapterInterior
        adapterInterior.addAll(list)
        popupInterior.showAsDropDown(v, 100, -10, Gravity.CENTER)
    }

    lateinit var dialogPackage: Dialog
    private fun popupPackages(data: ArrayList<VehiclePackagesData>) {
        if (!prefLCDData.packagesData.isNullOrEmpty()) {
            for (i in 0 until data.size) {
                for (j in 0 until prefLCDData.packagesData!!.size) {
                    if (data[i].vehiclePackageID == prefLCDData.packagesData!![j].vehiclePackageID) {
                        data[i] = prefLCDData.packagesData!![j]
                    }
                }
            }
        }
        dialogPackage = Dialog(context, R.style.FullScreenDialog)
        dialogPackage.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogPackage.setCancelable(true)
        dialogPackage.setCanceledOnTouchOutside(true)
        dialogPackage.setContentView(R.layout.dialog_vehicle_packages)
        dialogPackage.run {
            adapterPackages =
                PackagesAdapter(R.layout.list_item_packages, click)
            dialogPackage.rvPackages.adapter = adapterPackages
            adapterPackages.addAll(data)

            tvCancelPackage.setOnClickListener(click)
            tvResetPackage.setOnClickListener(click)
            tvApplyPackage.setOnClickListener(click)
        }
        setLayoutParam(dialogPackage)
        dialogPackage.show()
    }

    lateinit var dialogOptions: Dialog
    private fun popupOptions(data: java.util.ArrayList<VehicleAccessoriesData>) {
        if (!prefLCDData.optionsData.isNullOrEmpty()) {
            for (i in 0 until data.size) {
                for (j in 0 until prefLCDData.optionsData!!.size) {
                    if (data[i].dealerAccessoryID == prefLCDData.optionsData!![j].dealerAccessoryID) {
                        data[i] = prefLCDData.optionsData!![j]
                    }
                }
            }
        }
        dialogOptions = Dialog(context, R.style.FullScreenDialog)
        dialogOptions.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogOptions.setCancelable(true)
        dialogOptions.setCanceledOnTouchOutside(true)
        dialogOptions.setContentView(R.layout.dialog_vehicle_options)
        dialogOptions.run {
            adapterOptions =
                OptionsAdapter(R.layout.list_item_options, click)
            rvOptions.adapter = adapterOptions
            adapterOptions.addAll(data)

            tvCancelOption.setOnClickListener(click)
            tvResetOption.setOnClickListener(click)
            tvApplyOption.setOnClickListener(click)
        }
        setLayoutParam(dialogOptions)
        dialogOptions.show()
    }

    private fun popupTouchable(popup: PopupWindow) {
        popup.isTouchable = true
        popup.isOutsideTouchable = true
    }

    fun showHighlightedDialog() {
        val dialog = Dialog(context, R.style.FullScreenDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_highlight_inventory)
        dialog.run {
            Handler().postDelayed({
                dismiss()
            }, 3000)
        }
        setLayoutParam(dialog)
        dialog.show()
    }

    fun showOtherInventoryEmptyDialog() {
        val dialog = Dialog(context, R.style.FullScreenDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_other_inventory_availability)
        dialog.run {
            Handler().postDelayed({
                dismiss()
            }, 3000)
        }
        setLayoutParam(dialog)
        dialog.show()
    }

    fun showApplyEmptyDialog() {
        val dialog = Dialog(context, R.style.FullScreenDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_inventory_availability)
        dialog.run {
            Handler().postDelayed({
                dismiss()
            }, 3000)
        }
        setLayoutParam(dialog)
        dialog.show()
    }

    fun setRadius() {
        pref?.setRadius("")
    }

    fun setUCDCurrentTime() {
        val df = SimpleDateFormat("yyyy MM d, HH:mm:ss a")
        val date = df.format(Calendar.getInstance().time)
        pref?.setSearchDealTime(date)
        startHandler()
    }

    fun setLCDPrefData() {
        prefLCDData.isLCD = true
        pref?.setOneDealNearYouData(Gson().toJson(prefLCDData))
        setCurrentTime()
    }

    fun setCurrentTime() {
        val df = SimpleDateFormat("yyyy MM d, HH:mm:ss a")
        val date = df.format(Calendar.getInstance().time)
        pref?.setOneDealNearYou(date)
//        Log.e("Submit Date", date)
        startHandler()
    }

    fun startHandler() {
        if (!AppGlobal.isEmpty(pref?.getOneDealNearYou())) {
            handler = Handler()
            handler.postDelayed(runnable, 1000)
        } else {
            yearStr = context.getString(R.string.year_new_cars_title)
            makeStr = context.getString(R.string.make_title)
            modelStr = context.getString(R.string.model_title)
            trimStr = context.getString(R.string.trim_title)
            extColorStr = context.getString(R.string.exterior_color_title)
            intColorStr = context.getString(R.string.interior_color_title)
        }
    }

    private var runnable = object : Runnable {
        override fun run() {
            try {
                val date = Calendar.getInstance().time
                val lastDate = AppGlobal.stringToDate(pref?.getOneDealNearYou())

                val diff: Long = date.time - (lastDate?.time ?: 0)
                print(diff)

                val seconds = diff / 1000
                val minutes = seconds / 60
                if (minutes >= 30) {
                    handler.removeCallbacks(this)
                    pref?.setOneDealNearYouData(Gson().toJson(PrefSubmitPriceData()))
                    pref?.setOneDealNearYou("")
                    setTimerPrefData()
                } else {
                    handler.postDelayed(this, 1000)
                }
            } catch (e: Exception) {
            }
        }
    }

    fun setTimerPrefData() {
        prefLCDData = pref?.getOneDealNearYouData()!!
        isValidZipCode = prefLCDData.isZipCode!!
        zipCode = prefLCDData.zipCode!!
        yearId = prefLCDData.yearId!!
        makeId = prefLCDData.makeId!!
        modelId = prefLCDData.modelId!!
        trimId = prefLCDData.trimId!!
        extColorId = prefLCDData.extColorId!!
        intColorId = prefLCDData.intColorId!!
        yearStr = prefLCDData.yearStr!!
        makeStr = prefLCDData.makeStr!!
        modelStr = prefLCDData.modelStr!!
        trimStr = prefLCDData.trimStr!!
        extColorStr = prefLCDData.extColorStr!!
        intColorStr = prefLCDData.intColorStr!!

        if (TextUtils.isEmpty(zipCode) || zipCode.length < 5) {
            yearStr = context.getString(R.string.year_new_cars_title)
            makeStr = context.getString(R.string.make_title)
            modelStr = context.getString(R.string.model_title)
            trimStr = context.getString(R.string.trim_title)
            extColorStr = context.getString(R.string.exterior_color_title)
            intColorStr = context.getString(R.string.interior_color_title)

            edtZipCode.setText(zipCode)
            tvYear.text = context.getString(R.string.year_new_cars_title)
            tvMake.text = context.getString(R.string.make_title)
            tvModel.text = context.getString(R.string.model_title)
            tvTrim.text = context.getString(R.string.trim_title)
            tvExtColor.text = context.getString(R.string.exterior_color_title)
            tvIntColor.text = context.getString(R.string.interior_color_title)
            tvPackages.text = context.getString(R.string.packages_title)
            tvOptions.text = context.getString(R.string.options_accessories_title)

            tvYear.isEnabled = false
            tvMake.isEnabled = false
            tvModel.isEnabled = false
            tvTrim.isEnabled = false
            tvExtColor.isEnabled = false
            tvIntColor.isEnabled = false
            tvPackages.isEnabled = false
            tvOptions.isEnabled = false
        }
    }

    fun clickSearchBtn() {
        if (pref?.getUserData()?.isSocial!!) {
            if (!pref?.isUpdateSocialMobile()!!) {
                dialogPhoneNo(true)
            } else {
                callMinMSRPAPI()
            }
        } else {
            callMinMSRPAPI()
        }
    }

    private lateinit var dialogMobileNo: Dialog
    private fun dialogPhoneNo(isCancel: Boolean) {
        dialogMobileNo = Dialog(context, R.style.FullScreenDialog)
        dialogMobileNo.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogMobileNo.setCancelable(true)
        dialogMobileNo.setContentView(R.layout.dialog_mobile_no)
        Constant.onTextChange(
            context,
            dialogMobileNo.edtPhoneNumber,
            dialogMobileNo.tvErrorPhoneNo
        )
        dialogMobileNo.edtPhoneNumber.filters =
            arrayOf<InputFilter>(filterSocMob, InputFilter.LengthFilter(13))
        if (isCancel) {
            dialogMobileNo.ivSocClose.visibility = View.VISIBLE
        }
        dialogMobileNo.run {
            btnDialogSave.setOnClickListener {
                if (TextUtils.isEmpty(edtPhoneNumber.text.toString().trim())) {
                    Constant.setErrorBorder(edtPhoneNumber, tvErrorPhoneNo)
                    tvErrorPhoneNo.text = context.getString(R.string.enter_phonenumber)
                } else if (edtPhoneNumber.text.toString().trim().length != 13) {
                    Constant.setErrorBorder(edtPhoneNumber, tvErrorPhoneNo)
                    tvErrorPhoneNo.text = context.getString(R.string.enter_valid_phone_number)
                } else {
                    tvErrorPhoneNo.visibility = View.GONE
                    callSocialMobileAPI(edtPhoneNumber.text.toString().trim())
                    dismiss()
                }
            }
            ivSocClose.setOnClickListener {
                dismiss()
            }
        }
        setLayoutParam(dialogMobileNo)
        dialogMobileNo.show()
    }

    fun callSocialMobileAPI(
        phoneNo: String

    ) {
        Constant.showLoader(context)
        try {
            val data: LoginData = pref?.getUserData()!!
            viewModelScope.launch {
                val request = HashMap<String, Any>()
                request[ApiConstant.FirstNameSoc] = data.firstName!!
                request[ApiConstant.LastNameSoc] = data.lastName!!
                request[ApiConstant.UserNameSoc] = data.userName!!
                request[ApiConstant.EmailSoc] = data.userName!!
                request[ApiConstant.PhoneNumberSoc] = phoneNo
                val response: Deferred<Response<LoginData>> = async {
                    RetrofitClient.apiInterface.socialMobile2(
                        request
                    )
                }
                val dataLogin = response.await()
                Constant.dismissLoader()
                if (dataLogin.isSuccessful) {
                    data.message = dataLogin.body()?.message!!
                    if (data.buyerId != 0) {
                        pref?.setLogin(true)
                        data.isSocial = true
                        pref?.setUserData(Gson().toJson(data))
                        pref?.updateSocialMobile(true)
                        callMinMSRPAPI()
                    } else {
                        Toast.makeText(
                            context,
                            context.resources.getString(R.string.login_failed),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {

                }
            }
        } catch (e: Exception) {

        }
    }


    fun callMinMSRPAPI(
    ) {
        Constant.showLoader(context)
        try {
            viewModelScope.launch {
                val jsonArrayAccessories = JsonArray()
                var accessoriesStr = ""
                var isFirstAcce = true
                val arAccId: java.util.ArrayList<String> = java.util.ArrayList()
                for (i in 0 until prefLCDData.optionsData!!.size) {
                    if (prefLCDData.optionsData!![i].isSelect!! || prefLCDData.optionsData!![i].isOtherSelect!!) {
                        jsonArrayAccessories.add(prefLCDData.optionsData!![i].dealerAccessoryID)
                        arAccId.add(prefLCDData.optionsData!![i].dealerAccessoryID!!)
                        if (isFirstAcce) {
                            isFirstAcce = false
                            accessoriesStr = prefLCDData.optionsData!![i].accessory!!
                        } else
                            accessoriesStr += ",\n" + prefLCDData.optionsData!![i].accessory!!
                    }
                }
                val jsonArrayPackage = JsonArray()
                var packageStr = ""
                var isFirstPackage = true
                val arPackageId: java.util.ArrayList<String> = java.util.ArrayList()

                for (i in 0 until prefLCDData.packagesData?.size!!) {
                    if (prefLCDData.packagesData!![i].isSelect!! || prefLCDData.packagesData!![i].isOtherSelect!!) {
                        jsonArrayPackage.add(prefLCDData.packagesData!![i].vehiclePackageID)
                        arPackageId.add(prefLCDData.packagesData!![i].vehiclePackageID!!)
                        if (isFirstPackage) {
                            isFirstPackage = false
                            packageStr = prefLCDData.packagesData!![i].packageName!!
                        } else {
                            packageStr =
                                packageStr + ",\n" + prefLCDData.packagesData!![i].packageName!!
                        }
                    }
                }
                val request = HashMap<String, Any>()
                request[ApiConstant.vehicleYearID] = yearId
                request[ApiConstant.vehicleMakeID] = makeId
                request[ApiConstant.vehicleModelID] = modelId
                request[ApiConstant.vehicleTrimID] = trimId
                request[ApiConstant.vehicleExteriorColorID] = extColorId
                request[ApiConstant.vehicleInteriorColorID] = intColorId
                request[ApiConstant.zipCode] = edtZipCode.text.toString().trim()
                request[ApiConstant.dealerAccessoryIDs] = jsonArrayAccessories
                request[ApiConstant.vehiclePackageIDs] = jsonArrayPackage

                val response: Deferred<Response<FindLCDDeaData>> = async {
                    RetrofitClient.apiInterface.findLCDDeal2(
                        request
                    )
                }

                val dataResp = response.await()
                Constant.dismissLoader()
                if (dataResp.isSuccessful) {
                    val data = dataResp.body()
                    val arOptions: ArrayList<VehicleAccessoriesData> =
                        ArrayList()
                    for (i in 0 until prefLCDData.optionsData!!.size) {
                        if (prefLCDData.optionsData!![i].isSelect!! || prefLCDData.optionsData!![i].isOtherSelect!!) {
                            arOptions.add(prefLCDData.optionsData!![i])
                        }
                    }
                    val arPackage: ArrayList<VehiclePackagesData> = ArrayList()

                    for (i in 0 until prefLCDData.packagesData!!.size) {
                        if (prefLCDData.packagesData!![i].isSelect!! || prefLCDData.packagesData!![i].isOtherSelect!!) {
                            arPackage.add(prefLCDData.packagesData!![i])
                        }
                    }
                    data?.productId = productId
                    data?.yearId = yearId
                    data?.makeId = makeId
                    data?.modelId = modelId
                    data?.trimId = trimId
                    data?.exteriorColorId = extColorId
                    data?.interiorColorId = intColorId
                    data?.yearStr = yearStr
                    data?.makeStr = makeStr
                    data?.modelStr = modelStr
                    data?.trimStr = trimStr
                    data?.exteriorColorStr = extColorStr
                    data?.interiorColorStr = intColorStr
                    data?.arPackage = packageStr
                    data?.arAccessories = accessoriesStr
                    data?.arAccessoriesId = arAccId
                    data?.arPackageId = arPackageId
                    pref?.setOneDealNearYouData(Gson().toJson(pref?.getOneDealNearYouData()))
                    context.startActivity<LCDDealSummaryStep1Activity>(
                        Constant.ARG_LCD_DEAL_GUEST to Gson().toJson(
                            data
                        )
                    )
                } else {

                }
            }
        } catch (e: Exception) {

        }
    }

    fun callPromotionAPI(
    ) {
        try {
            viewModelScope.launch {
                val response: Deferred<Response<PromotionData>> = async {
                    RetrofitClient.apiInterface.promotionPublic2()
                }
                val data = response.await()
                if (data.isSuccessful) {
                    liveDataPromotion?.postValue(data.body())
                } else {
                }
            }
        } catch (e: Exception) {

        }
    }


    private var filterSocMob = InputFilter { source, start, end, dest, dstart, dend ->
        dialogMobileNo.run {
            var source = source
            if (source.length > 0) {
                if (!Character.isDigit(source[0])) return@InputFilter "" else {
                    if (source.toString().length > 1) {
                        val number = source.toString()
                        val digits1 = number.toCharArray()
                        val digits2 = number.split("(?<=.)").toTypedArray()
                        source = digits2[digits2.size - 1]
                    }
                    if (edtPhoneNumber.text.toString().isEmpty()) {
                        return@InputFilter "($source"
                    } else if (edtPhoneNumber.text.toString().length > 1 && edtPhoneNumber.text.toString()
                            .length <= 3
                    ) {
                        return@InputFilter source
                    } else if (edtPhoneNumber.text.toString().length > 3 && edtPhoneNumber.text.toString()
                            .length <= 5
                    ) {
                        val isContain = dest.toString().contains(")")
                        return@InputFilter if (isContain) {
                            source
                        } else {
                            ")$source"
                        }
                    } else if (edtPhoneNumber.text.toString().length > 5 && edtPhoneNumber.text.toString()
                            .length <= 7
                    ) {
                        return@InputFilter source
                    } else if (edtPhoneNumber.text.toString().length > 7) {
                        val isContain = dest.toString().contains("-")
                        return@InputFilter if (isContain) {
                            source
                        } else {
                            "-$source"
                        }
                    }
                }
            } else {
            }
            source
        }
    }
}