package com.letyouknow.retrofit.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
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
        val notificationOptionsData = MutableLiveData<NotificationOptionsData>()
        val call = RetrofitClient.apiInterface.notificationOptions(AppGlobal.getUserID())

        call.enqueue(object : Callback<NotificationOptionsData> {
            override fun onFailure(call: Call<NotificationOptionsData>, t: Throwable) {
                Log.v("DEBUG : ", t.message.toString())
            }

            override fun onResponse(
                call: Call<NotificationOptionsData>,
                response: Response<NotificationOptionsData>,
            ) {
                Log.v("DEBUG : ", response.body().toString())

                val data = response.body()
                if (response.code() == 200 || response.code() == 201) {
                    Constant.dismissLoader()
                    notificationOptionsData.value = data!!
                } else if (response.code() == 401) {
                    AppGlobal.isAuthorizationFailed(context)
                } else {
                    Constant.dismissLoader()
                    response.errorBody()?.source()?.buffer?.snapshot()?.utf8()
                    if (response.errorBody()?.source()?.buffer?.snapshot()?.utf8() != null)
                        Toast.makeText(
                            context,
                            response.errorBody()?.source()?.buffer?.snapshot()?.utf8(),
                            Toast.LENGTH_LONG
                        ).show()
                }
            }
        })
        return notificationOptionsData
    }
}