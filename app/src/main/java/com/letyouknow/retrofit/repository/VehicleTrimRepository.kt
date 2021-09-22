package com.letyouknow.retrofit.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.letyouknow.model.VehicleTrimData
import com.letyouknow.retrofit.RetrofitClient
import com.pionymessenger.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


object VehicleTrimRepository {

    fun getVehicleTrimApiCall(
        context: Context,
        productId: String?,
        yearId: String?,
        makeId: String?,
        modelId: String?
    ): MutableLiveData<ArrayList<VehicleTrimData>> {
        val loginVo = MutableLiveData<ArrayList<VehicleTrimData>>()
        val call = RetrofitClient.apiInterface.getVehicleTrims(productId, yearId, makeId, modelId)

        call.enqueue(object : Callback<ArrayList<VehicleTrimData>> {
            override fun onFailure(call: Call<ArrayList<VehicleTrimData>>, t: Throwable) {
                Log.v("DEBUG : ", t.message.toString())
            }

            override fun onResponse(
                call: Call<ArrayList<VehicleTrimData>>,
                response: Response<ArrayList<VehicleTrimData>>,
            ) {
                Log.v("DEBUG : ", response.body().toString())

                val data = response.body()
                if (response.code() == 200 || response.code() == 201) {
                    loginVo.value = data!!
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
        return loginVo
    }
}