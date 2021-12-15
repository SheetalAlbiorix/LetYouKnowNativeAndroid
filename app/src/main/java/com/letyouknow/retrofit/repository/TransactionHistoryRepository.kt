package com.letyouknow.retrofit.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.letyouknow.model.TransactionHistoryData
import com.letyouknow.retrofit.RetrofitClient
import com.letyouknow.utils.AppGlobal
import com.letyouknow.utils.AppGlobal.Companion.alertError
import com.pionymessenger.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


object TransactionHistoryRepository {

    fun transactionHistoryApiCall(
        context: Context
    ): MutableLiveData<ArrayList<TransactionHistoryData>> {
        AppGlobal.printRequestAuth("transHistory req", "No Req")
        val findUCDDealData = MutableLiveData<ArrayList<TransactionHistoryData>>()
        val call = RetrofitClient.apiInterface.transactionsHistory()

        call.enqueue(object : Callback<ArrayList<TransactionHistoryData>> {
            override fun onFailure(call: Call<ArrayList<TransactionHistoryData>>, t: Throwable) {
                Constant.dismissLoader()
                Log.v("DEBUG : ", t.message.toString())
            }

            override fun onResponse(
                call: Call<ArrayList<TransactionHistoryData>>,
                response: Response<ArrayList<TransactionHistoryData>>,
            ) {

                val data = response.body()
                if (response.code() == 200 || response.code() == 201) {
                    Log.v("TransHist Resp : ", Gson().toJson(response.body()))
                    Constant.dismissLoader()
                    findUCDDealData.value = data!!
                } else if (response.code() == 401) {
                    Log.v("TransHist Resp : ", response.toString())
                    AppGlobal.isAuthorizationFailed(context)
                } else {
                    Log.v("TransHist Resp : ", response.toString())
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