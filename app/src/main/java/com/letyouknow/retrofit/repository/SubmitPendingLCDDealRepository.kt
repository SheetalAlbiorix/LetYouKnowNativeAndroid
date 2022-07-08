package com.letyouknow.retrofit.repository

import android.app.Activity
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.letyouknow.model.SubmitPendingUcdData
import com.letyouknow.retrofit.RetrofitClient
import com.letyouknow.utils.AppGlobal
import com.letyouknow.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


object SubmitPendingLCDDealRepository {

    fun pendinLCDDealApiCall(
        context: Activity,
        request: HashMap<String, Any>
    ): MutableLiveData<SubmitPendingUcdData> {
        AppGlobal.printRequestAuth("pendingLCD req", Gson().toJson(request))
        val pendinLCDDealData = MutableLiveData<SubmitPendingUcdData>()
        val call = RetrofitClient.apiInterface.submitPendingDealLCD(request)

        call.enqueue(object : Callback<SubmitPendingUcdData> {
            override fun onFailure(call: Call<SubmitPendingUcdData>, t: Throwable) {
                Constant.dismissLoader()
                // Log.v("DEBUG : ", t.message.toString())
            }

            override fun onResponse(
                call: Call<SubmitPendingUcdData>,
                response: Response<SubmitPendingUcdData>,
            ) {

                val data = response.body()
                if (response.code() == 200 || response.code() == 201) {
                    // Log.v("PendingLCD Resp ", Gson().toJson(response.body()))
                    Constant.dismissLoader()
                    pendinLCDDealData.value = data!!
                } else if (response.code() == 401) {
                    //  Log.v("PendingLCD Resp ", response.toString())
                    AppGlobal.isAuthorizationFailed(context)
                } else {
                    // Log.v("PendingLCD Resp ", response.toString())
                    Constant.dismissLoader()
                    response.errorBody()?.source()?.buffer?.snapshot()?.utf8()
                    Constant.dismissLoader()
                    val dataError = Gson().fromJson(
                        response.errorBody()?.source()?.buffer?.snapshot()?.utf8(),
                        SubmitPendingUcdData::class.java
                    )

                    if (!dataError.messageList.isNullOrEmpty()) {
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
                        if (!TextUtils.isEmpty(msgStr)) {
                            AppGlobal.alertError(
                                context,
                                msgStr
                            )
                        }
                    } else {
                        AppGlobal.alertError(
                            context,
                            response.errorBody()?.source()?.buffer?.snapshot()?.utf8()
                        )
                    }
                }
            }
        })
        return pendinLCDDealData
    }
}