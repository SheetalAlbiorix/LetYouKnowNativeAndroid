package com.letyouknow.retrofit.repository

import android.content.Context
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.letyouknow.model.ExteriorColorData
import com.letyouknow.retrofit.RetrofitClient
import com.letyouknow.utils.AppGlobal
import com.letyouknow.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


object ExteriorColorRepository {
    fun getExteriorColorCall(
        context: Context,
        productId: String?,
        yearId: String?,
        makeId: String?,
        modelId: String?,
        trimId: String?,
        zipCode: String?,
        type: Int? = 1,
        lowPrice: String? = "",
        highPrice: String? = ""
    ): MutableLiveData<ArrayList<ExteriorColorData>> {
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
            "Ext req",
            "ProductId: " + productId + ", " + "yearId: " + yearId + ", " + "makeId: " + makeId + ", " + "modelId: " + modelId + ", " + "trimId: " + trimId + ", " + "zipCode: " + zipCode + ", Type: " + type + " LowPrice: " + lowPriceParam + " HighPrice: " + highPriceParam
        )
        val getExteriorColorData = MutableLiveData<ArrayList<ExteriorColorData>>()
        val call = RetrofitClient.apiInterface.getVehicleExteriorColors(
            productId,
            yearId,
            makeId,
            modelId,
            trimId,
            zipCode,
            lowPriceParam,
            highPriceParam
        )

        call.enqueue(object : Callback<ArrayList<ExteriorColorData>> {
            override fun onFailure(call: Call<ArrayList<ExteriorColorData>>, t: Throwable) {
                Constant.dismissLoader()
                ////  Log.v("DEBUG : ", t.message.toString())
            }

            override fun onResponse(
                call: Call<ArrayList<ExteriorColorData>>,
                response: Response<ArrayList<ExteriorColorData>>,
            ) {

                val data = response.body()
                if (response.code() == 200 || response.code() == 201) {
                    // Log.v("extColor Resp ", Gson().toJson(response.body()))
                    getExteriorColorData.value = data!!
                } else {
                    // Log.v("extColor Resp ", response.toString())
                    Constant.dismissLoader()
                    response.errorBody()?.source()?.buffer?.snapshot()?.utf8()
                    AppGlobal.alertError(
                        context,
                        response.errorBody()?.source()?.buffer?.snapshot()?.utf8()
                    )
                }
            }
        })
        return getExteriorColorData
    }
}