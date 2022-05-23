package com.letyouknow.retrofit.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.letyouknow.retrofit.RetrofitClient
import com.letyouknow.utils.AppGlobal
import com.letyouknow.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object SavingsToDateRepository {

    fun savingstodateApiCall(
        context: Context,
    ): MutableLiveData<Float> {
        AppGlobal.printRequestAuth("SavingsDate req", "No Request")
        val savingstodateData = MutableLiveData<Float>()
        val call = RetrofitClient.apiInterface.savingsToDate()

        call.enqueue(object : Callback<Float> {
            override fun onResponse(call: Call<Float>, response: Response<Float>) {
                // Log.v("DEBUG : ", response.body().toString())

                val data = response.body()
                if (response.code() == 200 || response.code() == 201) {
                    //  Log.v("savingDate Resp ", Gson().toJson(response.body()))
                    savingstodateData.value = data!!
                } else {
                    // Log.v("savingDate Resp ", response.toString())
                    Constant.dismissLoader()
                    response.errorBody()?.source()?.buffer?.snapshot()?.utf8()
                    if (response.errorBody()?.source()?.buffer?.snapshot()?.utf8() != null)
                        AppGlobal.alertError(
                            context,
                            response.errorBody()?.source()?.buffer?.snapshot()?.utf8()
                        )
                }
            }

            override fun onFailure(call: Call<Float>, t: Throwable) {
                Constant.dismissLoader()
                // Log.v("DEBUG : ", t.message.toString())
            }
        })
        return savingstodateData
    }
}