package com.letyouknow.retrofit.viewmodel

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Handler
import android.text.InputFilter
import android.text.TextUtils
import android.view.*
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
import com.letyouknow.view.lyk.summary.LYKStep1Activity
import kotlinx.android.synthetic.main.dialog_mobile_no.*
import kotlinx.android.synthetic.main.popup_dialog.view.*
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.jetbrains.anko.startActivity
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class UCDViewModel : ViewModel() {
    val arYear: MutableLiveData<ArrayList<VehicleYearData>>? = null

    lateinit var adapterYear: YearAdapter
    lateinit var adapterMake: MakeAdapter
    lateinit var adapterModel: ModelAdapter
    lateinit var adapterTrim: TrimAdapter
    lateinit var adapterExterior: ExteriorColorAdapter
    lateinit var adapterInterior: InteriorColorAdapter


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
    lateinit var prefUCD: PrefSearchDealData
    lateinit var handler: Handler

    var productId = "1"
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

    lateinit var tvYear: TextView
    lateinit var tvMake: TextView
    lateinit var tvModel: TextView
    lateinit var tvTrim: TextView
    lateinit var tvExtColor: TextView
    lateinit var tvIntColor: TextView


    var liveDataPromotion: MutableLiveData<PromotionData>? = MutableLiveData()

    fun init(
        context: Activity,
        click: View.OnClickListener,
        tvYear: TextView,
        tvMake: TextView,
        tvModel: TextView,
        tvTrim: TextView,
        tvExtColor: TextView,
        tvIntColor: TextView
    ) {
        this.context = context
        this.click = click

        this.tvYear = tvYear
        this.tvMake = tvMake
        this.tvModel = tvModel
        this.tvTrim = tvTrim
        this.tvExtColor = tvExtColor
        this.tvIntColor = tvIntColor
    }


    fun getYear(
        v: View?,
        zipCode: String?

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
                        setClearData()
                    else
                        showYearPopUp(v, data.body()!!)
                } else {
                    setClearData()
                }
            }
        } catch (e: Exception) {

        }
    }

    fun getMake(
        view: View?,
        zipCode: String?,
        yearID: String?
    ) {
        Constant.showLoader(context)

        try {
            viewModelScope.launch {
                val response: Deferred<Response<ArrayList<VehicleMakeData>>> = async {
                    RetrofitClient.apiInterface.getVehicleMake2(
                        productId,
                        yearID,
                        zipCode,
                        null,
                        null
                    )
                }
                val data = response.await()
                Constant.dismissLoader()
                if (data.isSuccessful) {
                    if (data.body().isNullOrEmpty())
                        setClearData()
                    else
                        showMakePopUp(view, data.body()!!)
                } else {
                    setClearData()
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
                        setClearData()
                    else
                        showModelPopUp(view, data.body()!!)
                } else {
                    setClearData()
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
                        setClearData()
                    else
                        showTrimPopUp(view, data.body()!!)
                } else {
                    setClearData()
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

    fun setUCDPrefData() {
        prefUCD.isUCDSel = true
        pref?.setSearchDealData(Gson().toJson(prefUCD))
        setCurrentTime()
    }

    fun setCurrentTime() {
        val df = SimpleDateFormat("yyyy MM d, HH:mm:ss a")
        val date = df.format(Calendar.getInstance().time)
        pref?.setSearchDealTime(date)
//        Log.e("Submit Date", date)
        startHandler()
    }

    fun startHandler() {
        if (!AppGlobal.isEmpty(pref?.getSearchDealTime())) {
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
                val lastDate = AppGlobal.stringToDate(pref?.getSearchDealTime())

                val diff: Long = date.time - (lastDate?.time ?: 0)

                val seconds = diff / 1000
                val minutes = seconds / 60
                if (minutes >= 30) {
                    handler.removeCallbacks(this)
                    pref?.setSearchDealData(Gson().toJson(PrefSearchDealData()))
                    pref?.setSearchDealTime("")
                    setTimerPrefData()
                } else {
                    handler.postDelayed(this, 1000)
                }
            } catch (e: Exception) {
            }
        }
    }

    fun setTimerPrefData() {
        prefUCD = pref?.getSearchDealData()!!
        yearId = prefUCD.yearId!!
        makeId = prefUCD.makeId!!
        modelId = prefUCD.modelId!!
        trimId = prefUCD.trimId!!
        extColorId = prefUCD.extColorId!!
        intColorId = prefUCD.intColorId!!
        yearStr = prefUCD.yearStr!!
        makeStr = prefUCD.makeStr!!
        modelStr = prefUCD.modelStr!!
        trimStr = prefUCD.trimStr!!
        extColorStr = prefUCD.extColorStr!!
        intColorStr = prefUCD.intColorStr!!

        if (TextUtils.isEmpty(yearId)) {
            yearStr = context.getString(R.string.year_new_cars_title)
            makeStr = context.getString(R.string.make_title)
            modelStr = context.getString(R.string.model_title)
            trimStr = context.getString(R.string.trim_title)
            extColorStr = context.getString(R.string.exterior_color_title)
            intColorStr = context.getString(R.string.interior_color_title)

            tvYear.text = context.getString(R.string.year_new_cars_title)
            tvMake.text = context.getString(R.string.make_title)
            tvModel.text = context.getString(R.string.model_title)
            tvTrim.text = context.getString(R.string.trim_title)
            tvExtColor.text = context.getString(R.string.exterior_color_title)
            tvIntColor.text = context.getString(R.string.interior_color_title)

            tvMake.isEnabled = false
            tvModel.isEnabled = false
            tvTrim.isEnabled = false
            tvExtColor.isEnabled = false
            tvIntColor.isEnabled = false
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
                val jsonArray = JsonArray()
                val jsonArrayPackage = JsonArray()
                val request = HashMap<String, Any>()
                request[ApiConstant.packageList] = jsonArrayPackage
                request[ApiConstant.checkedList] = jsonArray
                request[ApiConstant.yearId] = yearId
                request[ApiConstant.makeId] = makeId
                request[ApiConstant.modelId] = modelId
                request[ApiConstant.trimId] = trimId
                request[ApiConstant.exteriorColorId] = extColorId
                request[ApiConstant.interiorColorId] = intColorId

                val response: Deferred<Response<Double>> = async {
                    RetrofitClient.apiInterface.getMinMSRP2(
                        request
                    )
                }
                val dataMSRP = response.await()
                Constant.dismissLoader()
                if (dataMSRP.isSuccessful) {
                    val arOptions: ArrayList<VehicleAccessoriesData> =
                        ArrayList()
                    val arPackage: ArrayList<VehiclePackagesData> = ArrayList()

                    val data = YearModelMakeData()
                    data.vehicleYearID = yearId
                    data.vehicleMakeID = makeId
                    data.vehicleModelID = modelId
                    data.vehicleTrimID = trimId
                    data.vehicleExtColorID = extColorId
                    data.vehicleIntColorID = intColorId
                    data.vehicleYearStr = yearStr
                    data.vehicleMakeStr = makeStr
                    data.vehicleModelStr = modelStr
                    data.vehicleTrimStr = trimStr
                    data.vehicleExtColorStr = extColorStr
                    data.vehicleIntColorStr = intColorStr
                    data.arPackages = arPackage
                    data.arOptions = arOptions
                    data.msrp = dataMSRP.body()?.toFloat()
                    pref?.setSearchDealData(Gson().toJson(pref?.getSearchDealData()))
                    context.startActivity<LYKStep1Activity>(
                        Constant.ARG_YEAR_MAKE_MODEL to Gson().toJson(
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

    fun isEnableField(
        isYear: Boolean,
        isMake: Boolean,
        isModel: Boolean,
        isTrim: Boolean,
        isExtColor: Boolean,
        isIntColor: Boolean
    ) {
        tvYear.isEnabled = isYear
        tvMake.isEnabled = isMake
        tvModel.isEnabled = isModel
        tvTrim.isEnabled = isTrim
        tvExtColor.isEnabled = isExtColor
        tvIntColor.isEnabled = isIntColor
    }

    fun isReInitField(
        isYear: Boolean,
        isMake: Boolean,
        isModel: Boolean,
        isTrim: Boolean,
        isExtColor: Boolean,
        isIntColor: Boolean
    ) {
        if (!isYear) {
            tvYear.text = context.getString(R.string.year_new_cars_title)

            yearId = ""
            yearStr = ""
            makeId = ""
            makeStr = ""
            modelId = ""
            modelStr = ""
            trimId = ""
            trimStr = ""
            extColorId = ""
            extColorStr = ""
            intColorId = ""
            intColorStr = ""

        }
        if (!isMake) {
            tvMake.text = context.getString(R.string.make_title)

            makeId = ""
            makeStr = ""
            modelId = ""
            modelStr = ""
            trimId = ""
            trimStr = ""
            extColorId = ""
            extColorStr = ""
            intColorId = ""
            intColorStr = ""
        }
        if (!isModel) {
            tvModel.text = context.getString(R.string.model_title)
            modelId = ""
            modelStr = ""
            trimId = ""
            trimStr = ""
            extColorId = ""
            extColorStr = ""
            intColorId = ""
            intColorStr = ""
        }
        if (!isTrim) {
            tvTrim.text = context.getString(R.string.trim_title)
            trimId = ""
            trimStr = ""
            extColorId = ""
            extColorStr = ""
            intColorId = ""
            intColorStr = ""

        }
        if (!isExtColor) {
            tvExtColor.text = context.getString(R.string.exterior_color_title)

            extColorId = ""
            extColorStr = ""
            intColorId = ""
            intColorStr = ""

        }
        if (!isIntColor) {
            tvIntColor.text =
                context.getString(R.string.interior_color_title)

            intColorId = ""
            intColorStr = ""
        }

        prefUCD.yearId = yearId
        prefUCD.makeId = makeId
        prefUCD.modelId = modelId
        prefUCD.trimId = trimId
        prefUCD.extColorId = extColorId
        prefUCD.intColorId = intColorId
        prefUCD.yearStr = yearStr
        prefUCD.makeStr = makeStr
        prefUCD.modelStr = modelStr
        prefUCD.trimStr = trimStr
        prefUCD.extColorStr = extColorStr
        prefUCD.intColorStr = intColorStr
        setUCDPrefData()

    }

    private fun setClearData() {
        showWarningDialog(context)
        pref?.setSearchDealData(Gson().toJson(PrefSearchDealData()))
        pref?.setSearchDealTime("")
        setTimerPrefData()
    }

}

