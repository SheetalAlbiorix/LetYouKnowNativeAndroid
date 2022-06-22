package com.letyouknow.retrofit.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.letyouknow.model.SubmitDealLCDData
import com.letyouknow.retrofit.RetrofitClient
import com.letyouknow.utils.AppGlobal
import com.letyouknow.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


object SubmitDealRepository {

    fun submitDealApiCall(
        context: Context,
        request: HashMap<String, Any>
    ): MutableLiveData<SubmitDealLCDData> {
        AppGlobal.printRequestAuth("submitDeal req", Gson().toJson(request))
        val submitDealLCDData = MutableLiveData<SubmitDealLCDData>()
        val call = RetrofitClient.apiInterface.submitdeal(request)

        call.enqueue(object : Callback<SubmitDealLCDData> {
            override fun onFailure(call: Call<SubmitDealLCDData>, t: Throwable) {
                Constant.dismissLoader()
                // Log.v("DEBUG : ", t.message.toString())
            }

            override fun onResponse(
                call: Call<SubmitDealLCDData>,
                response: Response<SubmitDealLCDData>,
            ) {

                val data = response.body()
                if (response.code() == 200 || response.code() == 201) {
                    // Log.v("submitDeal Resp ", Gson().toJson(response.body()))
                    Constant.dismissLoader()
                    submitDealLCDData.value = data!!
                } else if (response.code() == 401) {
                    //  Log.v("submitDeal Resp ", response.toString())
                    AppGlobal.isAuthorizationFailed(context)
                } else {
                    //  Log.v("submitDeal Resp ", response.toString())
                    Constant.dismissLoader()

                    val errorData = Gson().fromJson(
                        response.errorBody()?.source()?.buffer?.snapshot()?.utf8(),
                        SubmitDealLCDData::class.java
                    )
                    submitDealLCDData.value = errorData

                }
            }
        })
        return submitDealLCDData
    }
}