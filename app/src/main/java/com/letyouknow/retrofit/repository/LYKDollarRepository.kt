package com.letyouknow.retrofit.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.letyouknow.retrofit.RetrofitClient
import com.letyouknow.utils.AppGlobal
import com.pionymessenger.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


object LYKDollarRepository {

    fun getDollarApiCall(
        context: Context,
        dealerId: String?
    ): MutableLiveData<String> {
        val getDollarData = MutableLiveData<String>()
        val call = RetrofitClient.apiInterface.getlykdollar(dealerId)

        call.enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                Constant.dismissLoader()
                Log.v("DEBUG : ", t.message.toString())
            }

            override fun onResponse(
                call: Call<String>,
                response: Response<String>,
            ) {
                Log.v("DEBUG : ", response.body().toString())

                val data = response.body()
                if (response.code() == 200 || response.code() == 201) {
                    getDollarData.value = data!!
                } else {
                    Constant.dismissLoader()
                    response.errorBody()?.source()?.buffer?.snapshot()?.utf8()
                    AppGlobal.alertError(
                        context,
                        response.errorBody()?.source()?.buffer?.snapshot()?.utf8()
                    )
                }
            }
        })
        return getDollarData
    }
}