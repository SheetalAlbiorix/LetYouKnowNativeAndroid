package com.letyouknow.retrofit.repository

import android.content.Context
import android.text.TextUtils
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


object SubmitPendingDealRepository {

    fun pendingDealApiCall(
        context: Context,
        request: HashMap<String, Any>
    ): MutableLiveData<SubmitPendingUcdData> {
        AppGlobal.printRequestAuth("pendingDeal req", Gson().toJson(request))
        val pendinLCDDealData = MutableLiveData<SubmitPendingUcdData>()
        val call = RetrofitClient.apiInterface.submitPendingDeal(request)

        call.enqueue(object : Callback<SubmitPendingUcdData> {
            override fun onFailure(call: Call<SubmitPendingUcdData>, t: Throwable) {
                Constant.dismissLoader()
                Log.v("DEBUG : ", t.message.toString())
            }

            override fun onResponse(
                call: Call<SubmitPendingUcdData>,
                response: Response<SubmitPendingUcdData>,
            ) {

                val data = response.body()
                if (response.code() == 200 || response.code() == 201) {
                    Log.v("PendingDeal Resp ", Gson().toJson(response.body()))
                    Constant.dismissLoader()
                    pendinLCDDealData.value = data!!
                } else if (response.code() == 401) {
                    Log.v("PendingDeal Resp ", response.toString())
                    AppGlobal.isAuthorizationFailed(context)
                } else {
                    Log.v("PendingDeal Resp ", response.toString())
                    Constant.dismissLoader()
                    response.errorBody()?.source()?.buffer?.snapshot()?.utf8()
                    if (response.errorBody()?.source()?.buffer?.snapshot()?.utf8() != null) {
                        val resp = Gson().fromJson(
                            response.errorBody()?.source()?.buffer?.snapshot()?.utf8(),
                            SubmitPendingUcdData::class.java
                        )
                        if (!resp.messageList.isNullOrEmpty()) {
                            var isFirst = true
                            var msg = ""
                            for (i in 0 until resp.messageList.size) {
                                if (isFirst) {
                                    msg = resp.messageList[i]
                                    isFirst = false
                                } else {
                                    msg = msg + "\n" + resp.messageList[i]
                                }
                            }
                            if (!TextUtils.isEmpty(msg))
                                AppGlobal.alertError(
                                    context,
                                    msg
                                )
                        } else {
                            AppGlobal.alertError(
                                context,
                                response.errorBody()?.source()?.buffer?.snapshot()?.utf8()
                            )
                        }
                    }
                }
            }
        })
        return pendinLCDDealData
    }
}