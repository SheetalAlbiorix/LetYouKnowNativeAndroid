package com.letyouknow.retrofit.repository

import android.content.Context
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.letyouknow.model.VehicleTrimData
import com.letyouknow.retrofit.RetrofitClient
import com.letyouknow.utils.AppGlobal
import com.letyouknow.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


object VehicleTrimRepository {

    fun getVehicleTrimApiCall(
        context: Context,
        productId: String?,
        yearId: String?,
        makeId: String?,
        modelId: String?,
        zipCode: String?,
        type: Int? = 1,
        lowPrice: String? = "",
        highPrice: String? = ""
    ): MutableLiveData<ArrayList<VehicleTrimData>> {
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
            "Trim req",
            "ProductId: " + productId + ", " + "yearId: " + yearId + ", " + "makeId: " + makeId + ", " + "modelId: " + modelId + ", zipCode: " + zipCode
                    + ", Type: " + type + " LowPrice: " + lowPriceParam + " HighPrice: " + highPriceParam
        )

        val getVehicleTrimData = MutableLiveData<ArrayList<VehicleTrimData>>()
        val call =
            RetrofitClient.apiInterface.getVehicleTrims(
                productId,
                yearId,
                makeId,
                modelId,
                zipCode,
                lowPriceParam,
                highPriceParam
            )

        call.enqueue(object : Callback<ArrayList<VehicleTrimData>> {
            override fun onFailure(call: Call<ArrayList<VehicleTrimData>>, t: Throwable) {
                Constant.dismissLoader()
                //   Log.v("DEBUG : ", t.message.toString())
            }

            override fun onResponse(
                call: Call<ArrayList<VehicleTrimData>>,
                response: Response<ArrayList<VehicleTrimData>>,
            ) {

                val data = response.body()
                if (response.code() == 200 || response.code() == 201) {
                    //  Log.v("trim Resp : ", Gson().toJson(response.body()))
                    getVehicleTrimData.value = data!!
                } else {
                    // Log.v("trim Resp : ", response.toString())
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
        return getVehicleTrimData
    }
}