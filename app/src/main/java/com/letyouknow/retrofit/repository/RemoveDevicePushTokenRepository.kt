package com.letyouknow.retrofit.repository

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.letyouknow.model.DevicePushTokenData
import com.letyouknow.retrofit.RetrofitClient
import com.letyouknow.utils.AppGlobal
import com.letyouknow.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException


object RemoveDevicePushTokenRepository {
    fun pushTokenApiCall(
        context: Activity,
        request: String?
    ): MutableLiveData<DevicePushTokenData> {
        AppGlobal.printRequestAuth("Remove PushToken req", Gson().toJson(request))
        val pushTokenVo = MutableLiveData<DevicePushTokenData>()
//        request
        val call = RetrofitClient.apiInterface.devicePushTokenDelete(request)

        call.enqueue(object : Callback<DevicePushTokenData> {
            override fun onFailure(call: Call<DevicePushTokenData>, t: Throwable) {
                Constant.dismissLoader()
                if (t is SocketTimeoutException) {
                    AppGlobal.alertError(
                        context,
                        "Socket Time out. Please try again."
                    )
                }
                //  Log.v("DEBUG : ", t.message.toString())
            }

            override fun onResponse(
                call: Call<DevicePushTokenData>,
                response: Response<DevicePushTokenData>,
            ) {
                val data = response.body()
                if (response.code() == 200 || response.code() == 201) {
                    if (response.body() == null) {
                        pushTokenVo.value = DevicePushTokenData()
                    } else {
                        // Log.v("Remove Token Resp : ", Gson().toJson(response.body()))
                        pushTokenVo.value = data!!
                    }
                } else if (response.code() == 500) {
                    Constant.dismissLoader()
                    AppGlobal.alertError(
                        context,
                        response.message()
                    )
                } else {
                    // Log.v("referral Resp : ", response.toString())
                    Constant.dismissLoader()
                    pushTokenVo.value = DevicePushTokenData()
                }
            }
        })
        return pushTokenVo
    }
}