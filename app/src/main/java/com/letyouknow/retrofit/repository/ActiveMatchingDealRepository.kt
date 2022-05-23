package com.letyouknow.retrofit.repository

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.letyouknow.R
import com.letyouknow.model.ActiveMatchingData
import com.letyouknow.retrofit.RetrofitClient
import com.letyouknow.utils.AppGlobal
import com.letyouknow.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException


object ActiveMatchingDealRepository {
    fun matchingDealApiCall(
        context: Activity,
        request: String
    ): MutableLiveData<ActiveMatchingData> {
        AppGlobal.printRequestAuth("Add PushToken req", Gson().toJson(request))
        val pushTokenVo = MutableLiveData<ActiveMatchingData>()
//        request
        val call = RetrofitClient.apiInterface.activeMatchingDeal(request)

        call.enqueue(object : Callback<ActiveMatchingData> {
            override fun onFailure(call: Call<ActiveMatchingData>, t: Throwable) {
                Constant.dismissLoader()
                if (t is SocketTimeoutException) {
                    AppGlobal.alertError(
                        context,
                        context.resources.getString(R.string.socket_time_out)
                    )
                }
                // Log.v("DEBUG : ", t.message.toString())
            }

            override fun onResponse(
                call: Call<ActiveMatchingData>,
                response: Response<ActiveMatchingData>,
            ) {
                val data = response.body()
                if (response.code() == 200 || response.code() == 201) {
                    if (response.body() == null) {
                        pushTokenVo.value = ActiveMatchingData()
                    } else {
                        // Log.v("referral Resp : ", Gson().toJson(response.body()))
                        pushTokenVo.value = data!!
                    }
                } else if (response.code() == 401) {
                    Constant.dismissLoader()
                    AppGlobal.isAuthorizationFailed(context)
                } else if (response.code() == 500) {
                    Constant.dismissLoader()
                    AppGlobal.alertError(
                        context,
                        response.message()
                    )
                } else {
                    // Log.v("referral Resp : ", response.toString())
                    Constant.dismissLoader()
                    pushTokenVo.value = ActiveMatchingData()
                }
            }
        })
        return pushTokenVo
    }
}