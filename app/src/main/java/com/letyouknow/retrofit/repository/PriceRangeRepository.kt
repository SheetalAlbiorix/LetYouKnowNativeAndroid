package com.letyouknow.retrofit.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.letyouknow.model.PriceRangeData
import com.letyouknow.retrofit.RetrofitClient
import com.letyouknow.utils.AppGlobal
import com.letyouknow.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


object PriceRangeRepository {
    fun getPriceRangeApiCall(
        context: Context
    ): MutableLiveData<ArrayList<PriceRangeData>> {
        val getPriceRangeData = MutableLiveData<ArrayList<PriceRangeData>>()
        val call = RetrofitClient.apiInterface.priceRange()

        call.enqueue(object : Callback<ArrayList<PriceRangeData>> {
            override fun onFailure(call: Call<ArrayList<PriceRangeData>>, t: Throwable) {
                Constant.dismissLoader()
            }

            override fun onResponse(
                call: Call<ArrayList<PriceRangeData>>,
                response: Response<ArrayList<PriceRangeData>>,
            ) {

                val data = response.body()
                if (response.code() == 200 || response.code() == 201) {
                    //  Log.v("year Resp : ", Gson().toJson(response.body()))
                    getPriceRangeData.value = data!!
                } else {
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
        return getPriceRangeData
    }
}