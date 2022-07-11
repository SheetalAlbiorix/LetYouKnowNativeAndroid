package com.letyouknow.retrofit.repository

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.letyouknow.R
import com.letyouknow.model.RebateCheckedData
import com.letyouknow.retrofit.RetrofitClient
import com.letyouknow.utils.AppGlobal
import com.letyouknow.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException


object RebateCheckedRepository {
    fun rebateCheckApiCall(
        context: Activity,
        request: HashMap<String, Any>
    ): MutableLiveData<RebateCheckedData> {
        AppGlobal.printRequestAuth("Add RebateCheck req", Gson().toJson(request))
        val rebateData = MutableLiveData<RebateCheckedData>()
//        request
        val call = RetrofitClient.apiInterface.checkRebate(request)

        call.enqueue(object : Callback<RebateCheckedData> {
            override fun onFailure(call: Call<RebateCheckedData>, t: Throwable) {
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
                call: Call<RebateCheckedData>,
                response: Response<RebateCheckedData>,
            ) {
                val data = response.body()
                if (response.code() == 200 || response.code() == 201) {
                    if (response.body() == null) {
                        rebateData.value = RebateCheckedData()
                    } else {
                        // Log.v("referral Resp : ", Gson().toJson(response.body()))
                        rebateData.value = data!!
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
                    rebateData.value = RebateCheckedData()
                }
            }
        })
        return rebateData
    }
}