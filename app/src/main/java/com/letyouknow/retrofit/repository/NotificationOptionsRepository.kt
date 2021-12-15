package com.letyouknow.retrofit.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.letyouknow.model.NotificationOptionsData
import com.letyouknow.retrofit.RetrofitClient
import com.letyouknow.utils.AppGlobal
import com.pionymessenger.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object NotificationOptionsRepository {

    fun notificationOptionsRepositoryApiCall(
        context: Context,
    ): MutableLiveData<NotificationOptionsData> {
        AppGlobal.printRequestAuth("Notification req", "No request")
        val notificationOptionsData = MutableLiveData<NotificationOptionsData>()
        val call = RetrofitClient.apiInterface.notificationOptions(AppGlobal.getUserID())

        call.enqueue(object : Callback<NotificationOptionsData> {
            override fun onFailure(call: Call<NotificationOptionsData>, t: Throwable) {
                Constant.dismissLoader()
                Log.v("DEBUG : ", t.message.toString())
            }

            override fun onResponse(
                call: Call<NotificationOptionsData>,
                response: Response<NotificationOptionsData>,
            ) {

                val data = response.body()
                if (response.code() == 200 || response.code() == 201) {
                    Log.v("notiOptions Resp ", Gson().toJson(response.body()))
                    Constant.dismissLoader()
                    notificationOptionsData.value = data!!
                } else if (response.code() == 401) {
                    Log.v("notiOptions Resp ", response.toString())
                    AppGlobal.isAuthorizationFailed(context)
                } else {
                    Log.v("notiOptions Resp ", response.toString())
                    Constant.dismissLoader()
                    response.errorBody()?.source()?.buffer?.snapshot()?.utf8()
                    if (response.errorBody()?.source()?.buffer?.snapshot()?.utf8() != null)
                        AppGlobal.alertError(
                            context,
                            response.errorBody()?.source()?.buffer?.snapshot()?.utf8()
                        )
                }
            }
        })
        return notificationOptionsData
    }
}