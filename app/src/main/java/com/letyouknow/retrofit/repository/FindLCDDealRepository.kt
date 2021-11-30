package com.letyouknow.retrofit.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.letyouknow.model.FindLCDDeaData
import com.letyouknow.retrofit.RetrofitClient
import com.letyouknow.utils.AppGlobal
import com.pionymessenger.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


object FindLCDDealRepository {

    fun findLCDDealApiCall(
        context: Context,
        request: HashMap<String, Any>
    ): MutableLiveData<FindLCDDeaData> {
        val findLCDDealData = MutableLiveData<FindLCDDeaData>()
        val call = RetrofitClient.apiInterface.findLCDDeal(request)

        call.enqueue(object : Callback<FindLCDDeaData> {
            override fun onFailure(call: Call<FindLCDDeaData>, t: Throwable) {
                Constant.dismissLoader()
                Log.v("DEBUG : ", t.message.toString())
            }

            override fun onResponse(
                call: Call<FindLCDDeaData>,
                response: Response<FindLCDDeaData>,
            ) {
                Log.v("DEBUG : ", response.body().toString())

                val data = response.body()
                if (response.code() == 200 || response.code() == 201) {
                    Constant.dismissLoader()
                    findLCDDealData.value = data!!
                } else if (response.code() == 401) {
                    AppGlobal.isAuthorizationFailed(context)
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
        return findLCDDealData
    }
}