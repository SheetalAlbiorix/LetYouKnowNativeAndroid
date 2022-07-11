package com.letyouknow.retrofit.repository

import android.content.Context
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.letyouknow.model.VehicleMakeData
import com.letyouknow.retrofit.RetrofitClient
import com.letyouknow.utils.AppGlobal
import com.letyouknow.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


object VehicleMakeRepository {

    fun getVehicleMakeApiCall(
        context: Context,
        productId: String?,
        yearId: String?,
        zipCode: String?,
        type: Int? = 1,
        lowPrice: String? = "",
        highPrice: String? = ""
    ): MutableLiveData<ArrayList<VehicleMakeData>> {
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
            "make req",
            "productId: " + productId + ", yearId: " + yearId + ", zipCode: " + zipCode
                    + ", Type: " + type + " LowPrice: " + lowPriceParam + " HighPrice: " + highPriceParam
        )
        val getVehicleMakeData = MutableLiveData<ArrayList<VehicleMakeData>>()
        val call = RetrofitClient.apiInterface.getVehicleMake(
            productId,
            yearId,
            zipCode,
            lowPriceParam,
            highPriceParam
        )

        call.enqueue(object : Callback<ArrayList<VehicleMakeData>> {
            override fun onFailure(call: Call<ArrayList<VehicleMakeData>>, t: Throwable) {
                Constant.dismissLoader()
                //Log.v("DEBUG : ", t.message.toString())
            }

            override fun onResponse(
                call: Call<ArrayList<VehicleMakeData>>,
                response: Response<ArrayList<VehicleMakeData>>,
            ) {

                val data = response.body()
                if (response.code() == 200 || response.code() == 201) {
                    //  Log.v("make Resp : ", Gson().toJson(response.body()))
                    getVehicleMakeData.value = data!!
                } else {
                    // Log.v("make Resp : ", response.toString())
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
        return getVehicleMakeData
    }
}