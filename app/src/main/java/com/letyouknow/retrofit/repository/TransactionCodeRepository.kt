package com.letyouknow.retrofit.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
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