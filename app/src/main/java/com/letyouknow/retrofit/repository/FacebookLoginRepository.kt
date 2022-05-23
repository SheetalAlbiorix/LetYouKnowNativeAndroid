package com.letyouknow.retrofit.repository

import android.content.Context
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.letyouknow.model.LoginData
import com.letyouknow.model.LoginErrorData
import com.letyouknow.retrofit.RetrofitClient
import com.letyouknow.utils.AppGlobal
import com.letyouknow.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException


object FacebookLoginRepository {
    fun getLoginApiCall(
        context: Context,
        request: HashMap<String, Any>
    ): MutableLiveData<LoginData> {
        AppGlobal.printRequestAuth("login req", Gson().toJson(request))
        val loginVo = MutableLiveData<LoginData>()
        val call = RetrofitClient.apiInterface.facebookLogin(request)

        call.enqueue(object : Callback<LoginData> {
            override fun onFailure(call: Call<LoginData>, t: Throwable) {
                if (t is SocketTimeoutException) {
                    Constant.dismissLoader()
                    AppGlobal.alertError(
                        context,
                        "Socket Time out. Please try again."
                    )
                }
                // Log.v("DEBUG : ", t.message.toString())
            }

            override fun onResponse(
                call: Call<LoginData>,
                response: Response<LoginData>,
            ) {
                val data = response.body()
                if (response.code() == 200 || response.code() == 201) {
                    // Log.v("Login Resp : ", Gson().toJson(response.body()))
                    loginVo.value = data!!
                } else if (response.code() == 401) {
                    Constant.dismissLoader()
                    AppGlobal.isAuthorizationFailed(
                        context
                    )
                } else if (response.code() == 500) {
                    Constant.dismissLoader()
                    AppGlobal.alertError(
                        context,
                        response.message()
                    )
                } else {
                    // Log.v("LoginFB Resp : ", response.toString())
                    Constant.dismissLoader()
                    response.errorBody()?.source()?.buffer?.snapshot()?.utf8()
                    if (!TextUtils.isEmpty(
                            response.errorBody()?.source()?.buffer?.snapshot()?.utf8()
                        )
                    ) {
                        if (response.errorBody()?.source()?.buffer?.snapshot()?.utf8()
                                ?.contains('{')!!
                        ) {
                            val dataError = Gson().fromJson(
                                response.errorBody()?.source()?.buffer?.snapshot()?.utf8(),
                                LoginErrorData::class.java
                            )
                            if (dataError != null) {

                                if (!dataError.DuplicateUserName.isNullOrEmpty()) {
                                    val msg = dataError.DuplicateUserName[0]
                                    AppGlobal.alertError(
                                        context,
                                        msg
                                    )
                                } else if (!dataError.login_failure.isNullOrEmpty()) {
                                    val msg = dataError.login_failure[0]
                                    AppGlobal.alertError(
                                        context,
                                        msg
                                    )
                                }
                            }
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
        return loginVo
    }
}