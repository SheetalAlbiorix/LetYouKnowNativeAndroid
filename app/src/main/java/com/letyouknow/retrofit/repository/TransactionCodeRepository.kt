package com.letyouknow.retrofit.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.letyouknow.model.TransactionCodeData
import com.letyouknow.retrofit.RetrofitClient
import com.letyouknow.utils.AppGlobal
import com.letyouknow.utils.AppGlobal.Companion.alertError
import com.pionymessenger.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


object TransactionCodeRepository {

    fun transactionCodeApiCall(
        context: Context,
        code: String?
    ): MutableLiveData<TransactionCodeData> {
        AppGlobal.printRequestAuth("transactionCode req", code!!)
        val findUCDDealData = MutableLiveData<TransactionCodeData>()
        val call = RetrofitClient.apiInterface.transactionCode(code)

        call.enqueue(object : Callback<TransactionCodeData> {
            override fun onFailure(call: Call<TransactionCodeData>, t: Throwable) {
                Constant.dismissLoader()
                Log.v("DEBUG : ", t.message.toString())
            }

            override fun onResponse(
                call: Call<TransactionCodeData>,
                response: Response<TransactionCodeData>,
            ) {
                val data = response.body()
                if (response.code() == 200 || response.code() == 201) {
                    Log.v("transactionCode Resp : ", Gson().toJson(response.body()))
                    Constant.dismissLoader()
                    findUCDDealData.value = data!!
                } else if (response.code() == 401) {
                    Log.v("transactionCode Resp : ", response.toString())
                    AppGlobal.isAuthorizationFailed(context)
                } else {
                    Log.v("transactionCode Resp : ", response.toString())
                    Constant.dismissLoader()
                    response.errorBody()?.source()?.buffer?.snapshot()?.utf8()

                    if (response.errorBody()?.source()?.buffer?.snapshot()?.utf8() != null)
                        alertError(
                            context,
                            response.errorBody()?.source()?.buffer?.snapshot()?.utf8()
                        )
                }
            }
        })
        return findUCDDealData
    }
}