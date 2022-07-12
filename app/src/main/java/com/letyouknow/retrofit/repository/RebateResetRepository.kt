package com.letyouknow.retrofit.repository

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.letyouknow.R
import com.letyouknow.model.CalculateTaxData
import com.letyouknow.retrofit.RetrofitClient
import com.letyouknow.utils.AppGlobal
import com.letyouknow.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException


object RebateResetRepository {
    fun rebateResetApiCall(
        context: Activity,
        request: HashMap<String, Any>
    ): MutableLiveData<CalculateTaxData> {
        AppGlobal.printRequestAuth("Add RebateReset req", Gson().toJson(request))
        val rebateData = MutableLiveData<CalculateTaxData>()
//        request
        val call = RetrofitClient.apiInterface.rebateReset(request)

        call.enqueue(object : Callback<CalculateTaxData> {
            override fun onFailure(call: Call<CalculateTaxData>, t: Throwable) {
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
                call: Call<CalculateTaxData>,
                response: Response<CalculateTaxData>,
            ) {
                val data = response.body()
                if (response.code() == 200 || response.code() == 201) {
                    if (response.body() == null) {
                        rebateData.value = CalculateTaxData()
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
                    rebateData.value = CalculateTaxData()
                }
            }
        })
        return rebateData
    }
}