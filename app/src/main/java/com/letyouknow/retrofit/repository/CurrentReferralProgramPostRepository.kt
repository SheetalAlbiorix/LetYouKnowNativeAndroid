package com.letyouknow.retrofit.repository

import android.app.Activity
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.letyouknow.model.CurrentReferralProgramData
import com.letyouknow.retrofit.RetrofitClient
import com.letyouknow.utils.AppGlobal
import com.letyouknow.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException


object CurrentReferralProgramPostRepository {
    fun getReferralApiCall(
        context: Activity,
        request: HashMap<String, Any>
    ): MutableLiveData<CurrentReferralProgramData> {
        AppGlobal.printRequestAuth("referral req", Gson().toJson(request))
        val loginVo = MutableLiveData<CurrentReferralProgramData>()
//        request
        val call = RetrofitClient.apiInterface.referralProgramCurrentPost(request)

        call.enqueue(object : Callback<CurrentReferralProgramData> {
            override fun onFailure(call: Call<CurrentReferralProgramData>, t: Throwable) {
                Constant.dismissLoader()
                if (t is SocketTimeoutException) {
                    AppGlobal.alertError(
                        context,
                        "Socket Time out. Please try again."
                    )
                }
                Log.v("DEBUG : ", t.message.toString())
            }

            override fun onResponse(
                call: Call<CurrentReferralProgramData>,
                response: Response<CurrentReferralProgramData>,
            ) {
                val data = response.body()
                if (response.code() == 200 || response.code() == 201) {
                    if (response.body() == null) {
                        loginVo.value = CurrentReferralProgramData()
                    } else {
                        Log.v("referral Resp : ", Gson().toJson(response.body()))
                        loginVo.value = data!!
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
                    loginVo.value = CurrentReferralProgramData()

                    /* response.errorBody()?.source()?.buffer?.snapshot()?.utf8()
                     AppGlobal.alertErrorDialog(
                         context,
                         response.errorBody()?.source()?.buffer?.snapshot()?.utf8()
                     )*/
                }
            }
        })
        return loginVo
    }
}