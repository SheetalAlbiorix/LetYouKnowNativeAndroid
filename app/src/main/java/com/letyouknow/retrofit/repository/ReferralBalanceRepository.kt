package com.letyouknow.retrofit.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.letyouknow.retrofit.RetrofitClient
import com.letyouknow.utils.AppGlobal
import com.letyouknow.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


object ReferralBalanceRepository {

    fun currentBalanceCall(
        context: Context
    ): MutableLiveData<Double> {
        AppGlobal.printRequestAuth("ReferralBal req", "No Req")
        val minMsrpData = MutableLiveData<Double>()
        val call = RetrofitClient.apiInterface.referralProgramCurrentBalance()

        call.enqueue(object : Callback<Double> {
            override fun onResponse(call: Call<Double>, response: Response<Double>) {
                //  Log.v("DEBUG : ", response.body().toString())

                val data = response.body()
                if (response.code() == 200 || response.code() == 201) {
                    //   Log.v("ReferralBal Resp ", Gson().toJson(response.body()))
                    minMsrpData.value = data!!
                } else {
                    //  Log.v("ReferralBal Resp ", response.toString())
                    Constant.dismissLoader()
                    minMsrpData.value = 0.0
                }
            }

            override fun onFailure(call: Call<Double>, t: Throwable) {
                Constant.dismissLoader()
                // Log.v("DEBUG : ", t.message.toString())
            }
        })
        return minMsrpData
    }
}