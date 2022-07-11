package com.letyouknow.retrofit.repository

import android.content.Context
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.letyouknow.model.VehicleModelData
import com.letyouknow.retrofit.RetrofitClient
import com.letyouknow.utils.AppGlobal
import com.letyouknow.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


object VehicleModelRepository {
    fun getVehicleModelApiCall(
        context: Context,
        productId: String?,
        yearId: String?,
        makeID: String?,
        zipCode: String?,
        type: Int? = 1,
        lowPrice: String? = "",
        highPrice: String? = ""
    ): MutableLiveData<ArrayList<VehicleModelData>> {
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
            "productId: " + productId + ", yearId: " + yearId + ", makeID: " + makeID + ", zipCode: " + zipCode + ", Type: " + type + " LowPrice: " + lowPriceParam + " HighPrice: " + highPriceParam
        )
        val getVehicleModelData = MutableLiveData<ArrayList<VehicleModelData>>()
        val call = RetrofitClient.apiInterface.getVehicleModels(
            productId,
            yearId,
            makeID,
            zipCode,
            lowPriceParam,
            highPriceParam
        )

        call.enqueue(object : Callback<ArrayList<VehicleModelData>> {
            override fun onFailure(call: Call<ArrayList<VehicleModelData>>, t: Throwable) {
                Constant.dismissLoader()
                // Log.v("DEBUG : ", t.message.toString())
            }

            override fun onResponse(
                call: Call<ArrayList<VehicleModelData>>,
                response: Response<ArrayList<VehicleModelData>>,
            ) {

                val data = response.body()
                if (response.code() == 200 || response.code() == 201) {
                    // Log.v("model Resp : ", Gson().toJson(response.body()))
                    getVehicleModelData.value = data!!
                } else {
                    // Log.v("model Resp : ", response.toString())
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
        return getVehicleModelData
    }
}