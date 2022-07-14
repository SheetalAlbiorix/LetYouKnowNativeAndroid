package com.letyouknow.retrofit.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.letyouknow.retrofit.RetrofitClient
import com.letyouknow.utils.AppGlobal
import com.letyouknow.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


object CheckVehicleStockPriceBidRepository {
    fun checkVehicleStockCall(
        context: Context,
        request: HashMap<String, Any>
    ): MutableLiveData<Boolean> {
        AppGlobal.printRequestAuth("checkStock req", Gson().toJson(request))
        val checkVehicleData = MutableLiveData<Boolean>()
        val call = RetrofitClient.apiInterface.checkVehicleStockPriceBid(request)

        call.enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                val data = response.body()
                if (response.code() == 200 || response.code() == 201) {
                    // Log.v("checkStock Resp : ", Gson().toJson(response.body()))
                    Constant.dismissLoader()
                    checkVehicleData.value = data!!
                } else {
                    // Log.v("checkStock Resp : ", response.toString())
                    Constant.dismissLoader()
                    checkVehicleData.value = false
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                Constant.dismissLoader()
                // Log.v("DEBUG : ", t.message.toString())
            }
        })
        return checkVehicleData
    }
}