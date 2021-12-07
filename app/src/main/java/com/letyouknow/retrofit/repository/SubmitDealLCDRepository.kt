package com.letyouknow.retrofit.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.letyouknow.model.SubmitDealLCDData
import com.letyouknow.retrofit.RetrofitClient
import com.letyouknow.utils.AppGlobal
import com.pionymessenger.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


object SubmitDealLCDRepository {

    fun submitDealLCDApiCall(
        context: Context,
        request: HashMap<String, Any>
    ): MutableLiveData<SubmitDealLCDData> {
        val submitDealLCDData = MutableLiveData<SubmitDealLCDData>()
        val call = RetrofitClient.apiInterface.submitdeallcd(request)

        call.enqueue(object : Callback<SubmitDealLCDData> {
            override fun onFailure(call: Call<SubmitDealLCDData>, t: Throwable) {
                Constant.dismissLoader()
                Log.v("DEBUG : ", t.message.toString())
            }

            override fun onResponse(
                call: Call<SubmitDealLCDData>,
                response: Response<SubmitDealLCDData>,
            ) {
                Log.v("DEBUG : ", response.body().toString())

                val data = response.body()
                if (response.code() == 200 || response.code() == 201) {
                    Constant.dismissLoader()
                    submitDealLCDData.value = data!!
                } else if (response.code() == 401) {
                    AppGlobal.isAuthorizationFailed(context)
                } else {
                    Constant.dismissLoader()
                    val dataError = Gson().fromJson(
                        response.errorBody()?.source()?.buffer?.snapshot()?.utf8(),
                        SubmitDealLCDData::class.java
                    )
                    if (!dataError.isBadRequest!!) {
                        submitDealLCDData.value = dataError!!
                    } else {
                        var msgStr = ""
                        var isFirst = true

                        for (i in 0 until dataError?.messageList?.size!!) {
                            if (isFirst) {
                                isFirst = false
                                msgStr = dataError?.messageList[i]!!
                            } else {
                                msgStr = msgStr + ",\n" + dataError?.messageList[i]!!
                            }

                        }
                        if (msgStr != null)
                            AppGlobal.alertError(
                                context,
                                msgStr
                            )
                    }
                }
            }
        })
        return submitDealLCDData
    }
}