package com.letyouknow.retrofit.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.letyouknow.model.FindUcdDealData
import com.letyouknow.retrofit.RetrofitClient
import com.letyouknow.utils.AppGlobal
import com.letyouknow.utils.AppGlobal.Companion.isAuthorizationFailed
import com.pionymessenger.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


object FindUCDDealRepository {

    fun findUCDDealApiCall(
        context: Context,
        request: HashMap<String, Any>
    ): MutableLiveData<ArrayList<FindUcdDealData>> {
        AppGlobal.printRequestAuth("FindUCD req", Gson().toJson(request))
        val findUCDDealData = MutableLiveData<ArrayList<FindUcdDealData>>()
        val call = RetrofitClient.apiInterface.findUCDDeal(request)

        call.enqueue(object : Callback<ArrayList<FindUcdDealData>> {
            override fun onFailure(call: Call<ArrayList<FindUcdDealData>>, t: Throwable) {
                Constant.dismissLoader()
                Log.v("DEBUG : ", t.message.toString())
            }

            override fun onResponse(
                call: Call<ArrayList<FindUcdDealData>>,
                response: Response<ArrayList<FindUcdDealData>>,
            ) {

                val data = response.body()
                if (response.code() == 200 || response.code() == 201) {
                    Log.v("findUCD Resp ", Gson().toJson(response.body()))
                    Constant.dismissLoader()
                    findUCDDealData.value = data!!
                } else if (response.code() == 401) {
                    Log.v("findUCD Resp ", response.toString())
                    isAuthorizationFailed(context)
                } else {
                    Log.v("findUCD Resp ", response.toString())
                    Constant.dismissLoader()
                    response.errorBody()?.source()?.buffer?.snapshot()?.utf8()
                    AppGlobal.alertError(
                        context,
                        response.errorBody()?.source()?.buffer?.snapshot()?.utf8()
                    )
                }
            }
        })
        return findUCDDealData
    }
}