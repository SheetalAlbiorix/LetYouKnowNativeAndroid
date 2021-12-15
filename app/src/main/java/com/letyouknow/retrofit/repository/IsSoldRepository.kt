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


object IsSoldRepository {

    fun isSoldCall(
        context: Context,
        request: String
    ): MutableLiveData<Boolean> {
        AppGlobal.printRequestAuth("isSold req", Gson().toJson(request))
        val minMsrpData = MutableLiveData<Boolean>()
        val call = RetrofitClient.apiInterface.isSold(request)

        call.enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                Log.v("DEBUG : ", response.body().toString())

                val data = response.body()
                if (response.code() == 200 || response.code() == 201) {
                    Log.v("IsSold Resp ", Gson().toJson(response.body()))
                    Constant.dismissLoader()
                    minMsrpData.value = data!!
                } else {
                    Log.v("IsSold Resp ", response.toString())
                    Constant.dismissLoader()
                    response.errorBody()?.source()?.buffer?.snapshot()?.utf8()
                    if (response.errorBody()?.source()?.buffer?.snapshot()?.utf8() != null)
                        AppGlobal.alertError(
                            context,
                            response.errorBody()?.source()?.buffer?.snapshot()?.utf8()
                        )
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                Constant.dismissLoader()
                Log.v("DEBUG : ", t.message.toString())
            }
        })
        return minMsrpData
    }
}