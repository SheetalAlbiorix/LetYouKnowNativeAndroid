package com.letyouknow.retrofit.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.letyouknow.retrofit.RetrofitClient
import com.letyouknow.utils.AppGlobal
import com.pionymessenger.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


object MinMSRPRepository {

    fun minMsrpApiCall(
        context: Context,
        request: HashMap<String, Any>
    ): MutableLiveData<Double> {
        AppGlobal.printRequestAuth("MinMSRP req", Gson().toJson(request))
        val minMsrpData = MutableLiveData<Double>()
        val call = RetrofitClient.apiInterface.getMinMSRP(request)

        call.enqueue(object : Callback<Double> {
            override fun onResponse(call: Call<Double>, response: Response<Double>) {
                Log.v("DEBUG : ", response.body().toString())

                val data = response.body()
                if (response.code() == 200 || response.code() == 201) {
                    Log.v("minMSRP Resp ", Gson().toJson(response.body()))
                    Constant.dismissLoader()
                    minMsrpData.value = data!!
                } else {
                    Log.v("minMSRP Resp ", response.toString())
                    Constant.dismissLoader()
                    response.errorBody()?.source()?.buffer?.snapshot()?.utf8()
                    if (response.errorBody()?.source()?.buffer?.snapshot()?.utf8() != null)
                        AppGlobal.alertError(
                            context,
                            response.errorBody()?.source()?.buffer?.snapshot()?.utf8()
                        )
                }
            }

            override fun onFailure(call: Call<Double>, t: Throwable) {
                Constant.dismissLoader()
                Log.v("DEBUG : ", t.message.toString())
            }
        })
        return minMsrpData
    }
}