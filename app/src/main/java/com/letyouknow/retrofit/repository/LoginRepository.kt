package com.letyouknow.retrofit.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.letyouknow.model.LoginData
import com.letyouknow.retrofit.RetrofitClient
import com.letyouknow.utils.AppGlobal
import com.pionymessenger.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException


object LoginRepository {
    fun getLoginApiCall(
        context: Context,
        request: HashMap<String, String>
    ): MutableLiveData<LoginData> {
        val loginVo = MutableLiveData<LoginData>()
        val call = RetrofitClient.apiInterface.login(request)

        call.enqueue(object : Callback<LoginData> {
            override fun onFailure(call: Call<LoginData>, t: Throwable) {
                if (t is SocketTimeoutException) {
                    Constant.dismissLoader()
                    AppGlobal.alertError(
                        context,
                        "Socket Time out. Please try again."
                    )
                }
                Log.v("DEBUG : ", t.message.toString())
            }

            override fun onResponse(
                call: Call<LoginData>,
                response: Response<LoginData>,
            ) {
                Log.v("DEBUG : ", response.body().toString())
                val data = response.body()
                if (response.code() == 200 || response.code() == 201) {
                    loginVo.value = data!!
                } else {
                    Constant.dismissLoader()
                    response.errorBody()?.source()?.buffer?.snapshot()?.utf8()
                    AppGlobal.alertError(
                        context,
                        response.errorBody()?.source()?.buffer?.snapshot()?.utf8()
                    )
                }
            }
        })
        return loginVo
    }
}