package com.letyouknow.retrofit.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.letyouknow.model.BidPriceData
import com.letyouknow.retrofit.RetrofitClient
import com.letyouknow.utils.AppGlobal
import com.letyouknow.utils.AppGlobal.Companion.alertError
import com.pionymessenger.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


object BidHistoryRepository {

    fun bidHistoryApiCall(
        context: Context
    ): MutableLiveData<ArrayList<BidPriceData>> {
        val findUCDDealData = MutableLiveData<ArrayList<BidPriceData>>()
        val call = RetrofitClient.apiInterface.priceBid()

        call.enqueue(object : Callback<ArrayList<BidPriceData>> {
            override fun onFailure(call: Call<ArrayList<BidPriceData>>, t: Throwable) {
                Log.v("DEBUG : ", t.message.toString())
            }

            override fun onResponse(
                call: Call<ArrayList<BidPriceData>>,
                response: Response<ArrayList<BidPriceData>>,
            ) {
                Log.v("DEBUG : ", response.body().toString())

                val data = response.body()
                if (response.code() == 200 || response.code() == 201) {
                    Constant.dismissLoader()
                    findUCDDealData.value = data!!
                } else if (response.code() == 401) {
                    AppGlobal.isAuthorizationFailed(context)
                } else {
                    Constant.dismissLoader()
                    response.errorBody()?.source()?.buffer?.snapshot()?.utf8()
                    /* Toast.makeText(
                         context,
                         response.errorBody()?.source()?.buffer?.snapshot()?.utf8(),
                         Toast.LENGTH_LONG
                     ).show()*/
                    alertError(context, response.errorBody()?.source()?.buffer?.snapshot()?.utf8())
                }
            }
        })
        return findUCDDealData
    }
}