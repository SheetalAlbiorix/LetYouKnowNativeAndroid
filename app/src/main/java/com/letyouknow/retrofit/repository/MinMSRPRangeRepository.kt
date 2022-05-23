package com.letyouknow.retrofit.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.letyouknow.retrofit.RetrofitClient
import com.letyouknow.utils.AppGlobal
import com.letyouknow.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


object MinMSRPRangeRepository {

    fun minMsrpApiCall(
        context: Context,
        request: HashMap<String, Any>
    ): MutableLiveData<ArrayList<String>> {
        AppGlobal.printRequestAuth("MinMSRP req", Gson().toJson(request))
        val minMsrpData = MutableLiveData<ArrayList<String>>()
        val call = RetrofitClient.apiInterface.getMSRPRange(request)

        call.enqueue(object : Callback<ArrayList<String>> {
            override fun onResponse(
                call: Call<ArrayList<String>>,
                response: Response<ArrayList<String>>
            ) {
                //Log.v("DEBUG : ", response.body().toString())

                val data = response.body()
                if (response.code() == 200 || response.code() == 201) {
                    //  Log.v("minMSRP Resp ", Gson().toJson(response.body()))
                    Constant.dismissLoader()
                    minMsrpData.value = data!!
                } else {
                    //    Log.v("minMSRP Resp ", response.toString())
                    Constant.dismissLoader()
                    response.errorBody()?.source()?.buffer?.snapshot()?.utf8()
                    if (response.errorBody()?.source()?.buffer?.snapshot()?.utf8() != null)
                        AppGlobal.alertError(
                            context,
                            response.errorBody()?.source()?.buffer?.snapshot()?.utf8()
                        )
                }
            }

            override fun onFailure(call: Call<ArrayList<String>>, t: Throwable) {
                Constant.dismissLoader()
                //  Log.v("DEBUG : ", t.message.toString())
            }
        })
        return minMsrpData
    }
}