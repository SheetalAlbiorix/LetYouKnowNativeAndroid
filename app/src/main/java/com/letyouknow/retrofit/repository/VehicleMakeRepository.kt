package com.letyouknow.retrofit.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.letyouknow.model.VehicleMakeData
import com.letyouknow.retrofit.RetrofitClient
import com.letyouknow.utils.AppGlobal
import com.pionymessenger.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


object VehicleMakeRepository {

    fun getVehicleMakeApiCall(
        context: Context,
        productId: String?,
        yearId: String?,
        zipCode: String?
    ): MutableLiveData<ArrayList<VehicleMakeData>> {
        val getVehicleMakeData = MutableLiveData<ArrayList<VehicleMakeData>>()
        val call = RetrofitClient.apiInterface.getVehicleMake(productId, yearId, zipCode)

        call.enqueue(object : Callback<ArrayList<VehicleMakeData>> {
            override fun onFailure(call: Call<ArrayList<VehicleMakeData>>, t: Throwable) {
                Log.v("DEBUG : ", t.message.toString())
            }

            override fun onResponse(
                call: Call<ArrayList<VehicleMakeData>>,
                response: Response<ArrayList<VehicleMakeData>>,
            ) {
                Log.v("DEBUG : ", response.body().toString())

                val data = response.body()
                if (response.code() == 200 || response.code() == 201) {
                    getVehicleMakeData.value = data!!
                } else {
                    Constant.dismissLoader()
                    response.errorBody()?.source()?.buffer?.snapshot()?.utf8()
                    if (response.errorBody()?.source()?.buffer?.snapshot()?.utf8() != null)
                        AppGlobal.alertError(
                            context,
                            response.errorBody()?.source()?.buffer?.snapshot()?.utf8()
                        )
                }
            }
        })
        return getVehicleMakeData
    }
}