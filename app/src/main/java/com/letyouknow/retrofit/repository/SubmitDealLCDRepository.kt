package com.letyouknow.retrofit.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.letyouknow.model.SubmitDealLCDData
import com.letyouknow.retrofit.RetrofitClient
import com.letyouknow.utils.AppGlobal
import com.letyouknow.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


object SubmitDealLCDRepository {

    fun submitDealLCDApiCall(
        context: Context,
        request: HashMap<String, Any>
    ): MutableLiveData<SubmitDealLCDData> {
        AppGlobal.printRequestAuth("submitLCD req", Gson().toJson(request))
        val submitDealLCDData = MutableLiveData<SubmitDealLCDData>()
        val call = RetrofitClient.apiInterface.submitdeallcd(request)

        call.enqueue(object : Callback<SubmitDealLCDData> {
            override fun onFailure(call: Call<SubmitDealLCDData>, t: Throwable) {
                Constant.dismissLoader()
                //   Log.v("DEBUG : ", t.message.toString())
            }

            override fun onResponse(
                call: Call<SubmitDealLCDData>,
                response: Response<SubmitDealLCDData>,
            ) {
                //    Log.v("DEBUG : ", response.body().toString())

                val data = response.body()
                if (response.code() == 200 || response.code() == 201) {
                    //  Log.v("submitLCD Resp ", Gson().toJson(response.body()))
                    Constant.dismissLoader()
                    submitDealLCDData.value = data!!
                } else if (response.code() == 401) {
                    //  Log.v("submitLCD Resp ", response.toString())
                    AppGlobal.isAuthorizationFailed(context)
                } else {
                    //  Log.v("submitLCD Resp ", response.toString())
                    Constant.dismissLoader()
                    val dataError = Gson().fromJson(
                        response.errorBody()?.source()?.buffer?.snapshot()?.utf8(),
                        SubmitDealLCDData::class.java
                    )
                    submitDealLCDData.value = dataError!!
                    /* if (!dataError.isBadRequest!! || dataError.messageList == null) {
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
                         if (!TextUtils.isEmpty(msgStr))
                             AppGlobal.alertError(
                                 context,
                                 msgStr
                             )
                     }*/
                }
            }
        })
        return submitDealLCDData
    }
}