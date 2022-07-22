package com.letyouknow.retrofit.viewmodel

import android.app.Activity
import android.content.Context
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letyouknow.R
import com.letyouknow.model.*
import com.letyouknow.retrofit.RetrofitClient
import com.letyouknow.view.dropdown.*
import kotlinx.android.synthetic.main.popup_dialog.view.*
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Response

class LYK1ViewModel : ViewModel() {
    val arYear: MutableLiveData<ArrayList<VehicleYearData>>? = null


    val liveDataYear: MutableLiveData<MutableList<VehicleYearData>> =
        MutableLiveData(mutableListOf())
    val liveDataMake: MutableLiveData<MutableList<VehicleMakeData>> =
        MutableLiveData(mutableListOf())
    val liveDataModel: MutableLiveData<MutableList<VehicleModelData>> =
        MutableLiveData(mutableListOf())
    val liveDataTrim: MutableLiveData<MutableList<VehicleTrimData>> =
        MutableLiveData(mutableListOf())
    val liveDataExtColor: MutableLiveData<MutableList<ExteriorColorData>> =
        MutableLiveData(mutableListOf())
    val liveDataIntColor: MutableLiveData<MutableList<InteriorColorData>> =
        MutableLiveData(mutableListOf())


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

    var isShowYear = true
    var isShowMake = true
    var isShowModel = true
    var isShowTrim = true
    var isShowExterior = true
    var isShowInterior = true


    private lateinit var click: View.OnClickListener

    fun init(context: Activity, click: View.OnClickListener) {
        this.context = context
        this.click = click
    }

    fun getYear(
        productId: String?,
        zipCode: String?,
        type: Int? = 1,
        lowPrice: String? = "",
        highPrice: String? = "",

        ) {
        var lowPriceParam = lowPrice
        var highPriceParam = highPrice

        if (type == 3) {
            if (lowPrice != "ANY PRICE") {
                lowPriceParam =
                    if (TextUtils.isEmpty(
                            lowPrice
                        )
                    ) "0" else lowPrice!!
                highPriceParam =
                    if (TextUtils.isEmpty(highPrice)) "9999999" else highPrice
            } else {
                lowPriceParam = null
                highPriceParam = null
            }
        } else {
            lowPriceParam = null
            highPriceParam = null
        }

        viewModelScope.launch {
            val response: Deferred<Response<ArrayList<VehicleYearData>>> = async {
                RetrofitClient.apiInterface.getVehicleYears2(
                    productId,
                    zipCode,
                    lowPriceParam,
                    highPriceParam
                )

            }
            val data = response.await()
            if (data.isSuccessful()) {
                liveDataYear.postValue(data.body())
            } else {

            }
        }

    }

    fun getMake(
        productId: String?,
        zipCode: String?,
        yearID: String?,
        type: Int? = 1,
        lowPrice: String? = "",
        highPrice: String? = "",

        ) {
        var lowPriceParam = lowPrice
        var highPriceParam = highPrice

        if (type == 3) {
            if (lowPrice != "ANY PRICE") {
                lowPriceParam =
                    if (TextUtils.isEmpty(
                            lowPrice
                        )
                    ) "0" else lowPrice!!
                highPriceParam =
                    if (TextUtils.isEmpty(highPrice)) "9999999" else highPrice
            } else {
                lowPriceParam = null
                highPriceParam = null
            }
        } else {
            lowPriceParam = null
            highPriceParam = null
        }

        viewModelScope.launch {
            val response: Deferred<ArrayList<VehicleMakeData>> = async {
                RetrofitClient.apiInterface.getVehicleMake2(
                    productId,
                    yearID,
                    zipCode,
                    lowPriceParam,
                    highPriceParam
                )

            }
            val data = response.await()
            if (data.isNotEmpty()) {
                liveDataMake.postValue(data)
            }
        }

    }

    fun getModel(
        productId: String?,
        zipCode: String?,
        yearID: String?,
        makeId: String?,
        type: Int? = 1,
        lowPrice: String? = "",
        highPrice: String? = "",

        ) {
        var lowPriceParam = lowPrice
        var highPriceParam = highPrice

        if (type == 3) {
            if (lowPrice != "ANY PRICE") {
                lowPriceParam =
                    if (TextUtils.isEmpty(
                            lowPrice
                        )
                    ) "0" else lowPrice!!
                highPriceParam =
                    if (TextUtils.isEmpty(highPrice)) "9999999" else highPrice
            } else {
                lowPriceParam = null
                highPriceParam = null
            }
        } else {
            lowPriceParam = null
            highPriceParam = null
        }

        viewModelScope.launch {
            val response: Deferred<ArrayList<VehicleModelData>> = async {
                RetrofitClient.apiInterface.getVehicleModels2(
                    productId,
                    yearID,
                    makeId,
                    zipCode,
                    lowPriceParam,
                    highPriceParam
                )

            }
            val data = response.await()
            if (data.isNotEmpty()) {
                liveDataModel.postValue(data)
            }
        }

    }

    fun getTrim(
        productId: String?,
        zipCode: String?,
        yearID: String?,
        makeId: String?,
        modelId: String?,
        type: Int? = 1,
        lowPrice: String? = "",
        highPrice: String? = "",

        ) {
        var lowPriceParam = lowPrice
        var highPriceParam = highPrice

        if (type == 3) {
            if (lowPrice != "ANY PRICE") {
                lowPriceParam =
                    if (TextUtils.isEmpty(
                            lowPrice
                        )
                    ) "0" else lowPrice!!
                highPriceParam =
                    if (TextUtils.isEmpty(highPrice)) "9999999" else highPrice
            } else {
                lowPriceParam = null
                highPriceParam = null
            }
        } else {
            lowPriceParam = null
            highPriceParam = null
        }

        viewModelScope.launch {
            val response: Deferred<ArrayList<VehicleTrimData>> = async {
                RetrofitClient.apiInterface.getVehicleTrims2(
                    productId,
                    yearID,
                    makeId,
                    modelId,
                    zipCode,
                    lowPriceParam,
                    highPriceParam
                )

            }
            val data = response.await()
            if (data.isNotEmpty()) {
                liveDataTrim.postValue(data)
            }
        }

    }

    fun getExteriorColor(
        productId: String?,
        zipCode: String?,
        yearID: String?,
        makeId: String?,
        modelId: String?,
        trimId: String?,
        type: Int? = 1,
        lowPrice: String? = "",
        highPrice: String? = "",

        ) {
        var lowPriceParam = lowPrice
        var highPriceParam = highPrice

        if (type == 3) {
            if (lowPrice != "ANY PRICE") {
                lowPriceParam =
                    if (TextUtils.isEmpty(
                            lowPrice
                        )
                    ) "0" else lowPrice!!
                highPriceParam =
                    if (TextUtils.isEmpty(highPrice)) "9999999" else highPrice
            } else {
                lowPriceParam = null
                highPriceParam = null
            }
        } else {
            lowPriceParam = null
            highPriceParam = null
        }

        viewModelScope.launch {
            val response: Deferred<ArrayList<ExteriorColorData>> = async {
                RetrofitClient.apiInterface.getVehicleExteriorColors2(
                    productId,
                    yearID,
                    makeId,
                    modelId,
                    trimId,
                    zipCode,
                    lowPriceParam,
                    highPriceParam
                )

            }
            val data = response.await()
            if (data.isNotEmpty()) {
                liveDataExtColor.postValue(data)
            }
        }

    }

    fun getInteriorColor(
        productId: String?,
        zipCode: String?,
        yearID: String?,
        makeId: String?,
        modelId: String?,
        trimId: String?,
        extId: String?,
        type: Int? = 1,
        lowPrice: String? = "",
        highPrice: String? = "",

        ) {
        var lowPriceParam = lowPrice
        var highPriceParam = highPrice

        if (type == 3) {
            if (lowPrice != "ANY PRICE") {
                lowPriceParam =
                    if (TextUtils.isEmpty(
                            lowPrice
                        )
                    ) "0" else lowPrice!!
                highPriceParam =
                    if (TextUtils.isEmpty(highPrice)) "9999999" else highPrice
            } else {
                lowPriceParam = null
                highPriceParam = null
            }
        } else {
            lowPriceParam = null
            highPriceParam = null
        }

        viewModelScope.launch {
            val response: Deferred<ArrayList<InteriorColorData>> = async {
                RetrofitClient.apiInterface.getVehicleInteriorColors2(
                    productId,
                    yearID,
                    makeId,
                    modelId,
                    trimId,
                    extId,
                    zipCode,
                    lowPriceParam,
                    highPriceParam
                )

            }
            val data = response.await()
            if (data.isNotEmpty()) {
                liveDataIntColor.postValue(data)
            }
        }

    }


    fun showYearPopUp(v: View?, list: MutableList<VehicleYearData>) {
        val inflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.popup_dialog, null)

        popupYear = PopupWindow(
            view,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        popupYear.setBackgroundDrawable(context.resources.getDrawable(R.drawable.bg_dialog))

        adapterYear = YearAdapter(R.layout.list_item_year, click)
        view?.rvPopup?.adapter = adapterYear
        adapterYear.addAll(ArrayList(list))
        isShowYear = true
        popupYear.showAsDropDown(v, 100, -10, Gravity.CENTER)
    }

    fun showMakePopUp(v: View?, list: MutableList<VehicleMakeData>) {
        val inflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.popup_dialog, null)

        popupMake = PopupWindow(
            view,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        popupMake.setBackgroundDrawable(context.resources.getDrawable(R.drawable.bg_dialog))

        adapterMake = MakeAdapter(R.layout.list_item_make, click)
        view?.rvPopup?.adapter = adapterYear
        adapterMake.addAll(ArrayList(list))
        isShowYear = true
        popupMake.showAsDropDown(v, 100, -10, Gravity.CENTER)
    }
}

