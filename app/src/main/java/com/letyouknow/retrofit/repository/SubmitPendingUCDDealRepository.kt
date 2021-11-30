package com.letyouknow.retrofit.repository

import android.app.Activity
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


object SubmitPendingUCDDealRepository {

    fun pendingUCDDealApiCall(
        context: Activity,
        request: HashMap<String, Any>
    ): MutableLiveData<SubmitPendingUcdData> {
        val pendingUCDDealData = MutableLiveData<SubmitPendingUcdData>()
        val call = RetrofitClient.apiInterface.submitPendingDealUcd(request)

        call.enqueue(object : Callback<SubmitPendingUcdData> {
            override fun onFailure(call: Call<SubmitPendingUcdData>, t: Throwable) {
                Constant.dismissLoader()
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
                    pendingUCDDealData.value = data!!
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
                        AppGlobal.alertErrorDialog(
                            context,
                            msgStr
                        )

                }
            }
        })
        return pendingUCDDealData
    }
}