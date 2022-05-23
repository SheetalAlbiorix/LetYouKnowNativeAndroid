package com.letyouknow.retrofit.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.letyouknow.retrofit.RetrofitClient
import com.letyouknow.utils.AppGlobal
import com.letyouknow.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


object LYKDollarRepository {

    fun getDollarApiCall(
        context: Context,
        dealerId: String?
    ): MutableLiveData<String> {
        AppGlobal.printRequestAuth("LYKDollar req", dealerId!!)
        val getDollarData = MutableLiveData<String>()
        val call = RetrofitClient.apiInterface.getlykdollar(dealerId)

        call.enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                Constant.dismissLoader()
                // Log.v("DEBUG : ", t.message.toString())
            }

            override fun onResponse(
                call: Call<String>,
                response: Response<String>,
            ) {

                val data = response.body()
                if (response.code() == 200 || response.code() == 201) {
                    // Log.v("LYKDollar Resp ", Gson().toJson(response.body()))
                    getDollarData.value = data!!
                } else {
                    //  Log.v("LYKDollar Resp ", response.toString())
                    Constant.dismissLoader()
                    getDollarData.value = "0.0"
                    /*response.errorBody()?.source()?.buffer?.snapshot()?.utf8()
                    AppGlobal.alertError(
                        context,
                        response.errorBody()?.source()?.buffer?.snapshot()?.utf8()
                    )*/
                }
            }
        })
        return getDollarData
    }
}