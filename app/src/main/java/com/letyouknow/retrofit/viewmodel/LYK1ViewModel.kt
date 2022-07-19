package com.letyouknow.retrofit.viewmodel

import android.app.Activity
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letyouknow.model.VehicleYearData
import com.letyouknow.retrofit.RetrofitClient
import com.letyouknow.view.dropdown.YearAdapter
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class LYK1ViewModel() : ViewModel() {
    val arYear: MutableLiveData<ArrayList<VehicleYearData>>? = null


    val liveDataYear: MutableLiveData<MutableList<VehicleYearData>> =
        MutableLiveData(mutableListOf())
    private lateinit var adapterYear: YearAdapter

    private lateinit var context: Activity

    private lateinit var click: View.OnClickListener

    fun init(context: Activity, click: View.OnClickListener) {
        this.context = context
        this.click = click
    }

    fun getYear(
        productId: String?,
        zipCode: String?,
        view: View?,
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
            val response: Deferred<ArrayList<VehicleYearData>> = async {
                RetrofitClient.apiInterface.getVehicleYears2(
                    productId,
                    zipCode,
                    lowPriceParam,
                    highPriceParam
                )

            }
            val data = response.await()
            if (data.isNotEmpty()) {
                liveDataYear.postValue(data)
            }
//            if (response.isCompleted) {
//                withContext(Dispatchers.Main) {
//                    liveDataYear?.value = (response.getCompleted())
//                    Log.e("Popup", "Show Popup" + Gson().toJson(liveDataYear?.value))
//                    showAlertFilter(view)
//                }
//            }
        }

//        CoroutineScope(Dispatchers.IO).launch {
//            val response: Deferred<ArrayList<VehicleYearData>> = async {
//                RetrofitClient.apiInterface.getVehicleYears2(
//                    productId,
//                    zipCode,
//                    lowPriceParam,
//                    highPriceParam
//                )
//
//            }
//            response.await()
//            if (response.isCompleted) {
//                withContext(Dispatchers.Main) {
//                    liveDataYear?.value = (response.getCompleted())
//                    Log.e("Popup", "Show Popup" + Gson().toJson(liveDataYear?.value))
//                    showAlertFilter(view)
//                }
//            }
//        }

    }


}

