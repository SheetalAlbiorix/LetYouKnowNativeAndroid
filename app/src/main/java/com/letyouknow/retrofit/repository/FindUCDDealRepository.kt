package com.letyouknow.retrofit.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.letyouknow.model.FindUcdDealData
import com.letyouknow.retrofit.RetrofitClient
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
        val findUCDDealData = MutableLiveData<ArrayList<FindUcdDealData>>()
        val call = RetrofitClient.apiInterface.findUCDDeal(request)

        call.enqueue(object : Callback<ArrayList<FindUcdDealData>> {
            override fun onFailure(call: Call<ArrayList<FindUcdDealData>>, t: Throwable) {
                Log.v("DEBUG : ", t.message.toString())
            }

            override fun onResponse(
                call: Call<ArrayList<FindUcdDealData>>,
                response: Response<ArrayList<FindUcdDealData>>,
            ) {
                Log.v("DEBUG : ", response.body().toString())

                val data = response.body()
                if (response.code() == 200 || response.code() == 201) {
                    Constant.dismissLoader()
                    findUCDDealData.value = data!!
                } else if (response.code() == 401) {
                    isAuthorizationFailed(context)
                } else {
                    Constant.dismissLoader()
                    response.errorBody()?.source()?.buffer?.snapshot()?.utf8()
                    Toast.makeText(
                        context,
                        response.errorBody()?.source()?.buffer?.snapshot()?.utf8(),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })
        return findUCDDealData
    }
}