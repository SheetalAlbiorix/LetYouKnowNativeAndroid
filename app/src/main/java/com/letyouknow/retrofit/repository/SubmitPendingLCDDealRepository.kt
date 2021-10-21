package com.letyouknow.retrofit.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.letyouknow.model.SubmitPendingUcdData
import com.letyouknow.retrofit.RetrofitClient
import com.letyouknow.utils.AppGlobal
import com.pionymessenger.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


object SubmitPendingLCDDealRepository {

    fun pendinLCDDealApiCall(
        context: Context,
        request: HashMap<String, Any>
    ): MutableLiveData<SubmitPendingUcdData> {
        val pendinLCDDealData = MutableLiveData<SubmitPendingUcdData>()
        val call = RetrofitClient.apiInterface.submitPendingDealLCD(request)

        call.enqueue(object : Callback<SubmitPendingUcdData> {
            override fun onFailure(call: Call<SubmitPendingUcdData>, t: Throwable) {
                Log.v("DEBUG : ", t.message.toString())
            }

            override fun onResponse(
                call: Call<SubmitPendingUcdData>,
                response: Response<SubmitPendingUcdData>,
            ) {
                Log.v("DEBUG : ", response.body().toString())

                val data = response.body()
                if (response.code() == 200 || response.code() == 201) {
                    Constant.dismissLoader()
                    pendinLCDDealData.value = data!!
                } else if (response.code() == 401) {
                    AppGlobal.isAuthorizationFailed(context)
                } else {
                    Constant.dismissLoader()
                    response.errorBody()?.source()?.buffer?.snapshot()?.utf8()
                    Constant.dismissLoader()
                    val dataError = Gson().fromJson(
                        response.errorBody()?.source()?.buffer?.snapshot()?.utf8(),
                        SubmitPendingUcdData::class.java
                    )
                    var msgStr = ""
                    var isFirst = true

                    for (i in 0 until dataError?.messageList?.size!!) {
                        if (isFirst) {
                            isFirst = false
                            msgStr = dataError.messageList[i]
                        } else {
                            msgStr = msgStr + ",\n" + dataError.messageList[i]
                        }

                    }
                    if (msgStr != null)
                        AppGlobal.alertError(
                            context,
                            msgStr
                        )
                }
            }
        })
        return pendinLCDDealData
    }
}