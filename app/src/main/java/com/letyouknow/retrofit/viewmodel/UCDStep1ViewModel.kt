package com.letyouknow.retrofit.viewmodel

import android.app.Activity
import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.letyouknow.model.FindUCDMainData
import com.letyouknow.model.FindUcdDealData
import com.letyouknow.model.YearModelMakeData
import com.letyouknow.retrofit.ApiConstant
import com.letyouknow.retrofit.repository.FindUCDDealRepository
import com.letyouknow.utils.Constant
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.set

class UCDStep1ViewModel : ViewModel() {
    private var arMainUCD: MutableLiveData<ArrayList<FindUCDMainData>> =
        MutableLiveData<ArrayList<FindUCDMainData>>()
    private var arUCD: LiveData<ArrayList<FindUcdDealData>> =
        MutableLiveData<ArrayList<FindUcdDealData>>()

    var yearModelMakeData = MutableLiveData<YearModelMakeData>()
    var searchRadius: MutableLiveData<String> = MutableLiveData<String>("")
    var zipCode: MutableLiveData<String> = MutableLiveData<String>("")
    var liveDataUCD: MutableLiveData<ArrayList<FindUcdDealData>>? = null
    private lateinit var context: Activity

    fun initUCD(context: Activity) {
        this.context = context
        callSearchFindDealAPI()
    }

    private fun callSearchFindDealAPI() {
        viewModelScope.launch {
            coroutineScope {
                if (Constant.isOnline(context)) {
                    if (Constant.isInitProgress() && !Constant.progress.isShowing)
                        Constant.dismissLoader()
                    if (!Constant.isInitProgress()) {
                        Constant.showLoader(context)
                    } else if (Constant.isInitProgress() && !Constant.progress.isShowing) {
                        Constant.showLoader(context)
                    }
                    val request = HashMap<String, Any>()
                    request[ApiConstant.vehicleYearID] =
                        yearModelMakeData.value?.vehicleYearID!!
                    request[ApiConstant.vehicleMakeID] = yearModelMakeData.value?.vehicleMakeID!!
                    request[ApiConstant.vehicleModelID] = yearModelMakeData.value?.vehicleModelID!!
                    request[ApiConstant.vehicleTrimID] = yearModelMakeData.value?.vehicleTrimID!!
                    request[ApiConstant.vehicleExteriorColorID] =
                        yearModelMakeData.value?.vehicleExtColorID!!
                    request[ApiConstant.vehicleInteriorColorID] =
                        yearModelMakeData.value?.vehicleIntColorID!!
                    request[ApiConstant.zipCode] = yearModelMakeData.value?.zipCode!!
                    request[ApiConstant.searchRadius] =
                        if (yearModelMakeData.value?.radius == "ALL") "6000" else yearModelMakeData.value?.radius!!.replace(
                            "mi",
                            ""
                        ).trim()
                    if (yearModelMakeData.value?.LowPrice != "ANY PRICE") {
                        request[ApiConstant.LowPrice] =
                            if (TextUtils.isEmpty(
                                    yearModelMakeData.value?.LowPrice!!
                                )
                            ) "1" else yearModelMakeData.value?.LowPrice!!
                        request[ApiConstant.HighPrice] =
                            if (TextUtils.isEmpty(yearModelMakeData.value?.HighPrice!!)) "1000000" else yearModelMakeData.value?.HighPrice!!
                    }
                    Log.e("Request Find Deal", Gson().toJson(request))
                    /*val arData = async {
                         FindUCDDealRepository.findUCDDealApiCall(
                            context,
                            request
                        )
                    }*/
//                    arData.aw
//                    Log.e("LiveUCD", Gson().toJson(arData.await()))
                } else {
                    Toast.makeText(context, Constant.noInternet, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun findUCDDeal(
        context: Context,
        request: HashMap<String, Any>
    ): LiveData<ArrayList<FindUcdDealData>>? {
        liveDataUCD = FindUCDDealRepository.findUCDDealApiCall(
            context,
            request
        )
        return liveDataUCD
    }

}