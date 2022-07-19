package com.letyouknow.retrofit.repository

import android.content.Context
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.letyouknow.model.VehicleYearData
import com.letyouknow.retrofit.RetrofitClient
import com.letyouknow.utils.AppGlobal
import com.letyouknow.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


object VehicleYearRepository {
    fun getVehicleYearApiCall(
        context: Context,
        productId: String?,
        zipCode: String?,
        type: Int? = 1,
        lowPrice: String? = "",
        highPrice: String? = ""
    ): MutableLiveData<ArrayList<VehicleYearData>> {
        val getVehicleYearData = MutableLiveData<ArrayList<VehicleYearData>>()
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
        AppGlobal.printRequestAuth(
            "Year req",
            "ProductId: " + productId + ", zipCode: " + zipCode + ", Type: " + type + " LowPrice: " + lowPriceParam + " HighPrice: " + highPriceParam
        )
        val call = RetrofitClient.apiInterface.getVehicleYears(
            productId,
            zipCode,
            lowPriceParam,
            highPriceParam
        )
        call.enqueue(object : Callback<ArrayList<VehicleYearData>> {
            override fun onFailure(call: Call<ArrayList<VehicleYearData>>, t: Throwable) {
                Constant.dismissLoader()
                // Log.v("DEBUG : ", t.message.toString())
            }

            override fun onResponse(
                call: Call<ArrayList<VehicleYearData>>,
                response: Response<ArrayList<VehicleYearData>>,
            ) {

                val data = response.body()
                if (response.code() == 200 || response.code() == 201) {
                    //  Log.v("year Resp : ", Gson().toJSon(response.body()))
                    getVehicleYearData.value = data!!
                } else {
                    // Log.v("year Resp : ", response.toString())
                    Constant.dismissLoader()
                    response.errorBody()?.source()?.buffer?.snapshot()?.utf8()
                    if (response.errorBody()?.source()?.buffer?.snapshot()?.utf8() != null)
                        AppGlobal.alertError(
                            context,
                            response.errorBody()?.source()?.buffer?.snapshot()?.utf8()
                        )
                }
            }
        })
        return getVehicleYearData
    }

    suspend fun getVehicleYearApiCall1(
        context: Context,
        productId: String?,
        zipCode: String?,
        type: Int? = 1,
        lowPrice: String? = "",
        highPrice: String? = ""
    ): MutableLiveData<ArrayList<VehicleYearData>> {
        val getVehicleYearData = MutableLiveData<ArrayList<VehicleYearData>>()
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
        AppGlobal.printRequestAuth(
            "Year req",
            "ProductId: " + productId + ", zipCode: " + zipCode + ", Type: " + type + " LowPrice: " + lowPriceParam + " HighPrice: " + highPriceParam
        )
        RetrofitClient.apiInterface.getVehicleYears2(
            productId,
            zipCode,
            lowPriceParam,
            highPriceParam
        )
        /* call.enqueue(object : Callback<ArrayList<VehicleYearData>> {
             override fun onFailure(call: Call<ArrayList<VehicleYearData>>, t: Throwable) {
                 Constant.dismissLoader()
                 // Log.v("DEBUG : ", t.message.toString())
             }

             override fun onResponse(
                 call: Call<ArrayList<VehicleYearData>>,
                 response: Response<ArrayList<VehicleYearData>>,
             ) {

                 val data = response.body()
                 if (response.code() == 200 || response.code() == 201) {
                     //  Log.v("year Resp : ", Gson().toJSon(response.body()))
                     getVehicleYearData.value = data!!
                 } else {
                     // Log.v("year Resp : ", response.toString())
                     Constant.dismissLoader()
                     response.errorBody()?.source()?.buffer?.snapshot()?.utf8()
                     if (response.errorBody()?.source()?.buffer?.snapshot()?.utf8() != null)
                         AppGlobal.alertError(
                             context,
                             response.errorBody()?.source()?.buffer?.snapshot()?.utf8()
                         )
                 }
             }
         })*/
        return getVehicleYearData
    }
}