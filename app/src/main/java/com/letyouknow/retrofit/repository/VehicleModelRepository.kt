package com.letyouknow.retrofit.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.letyouknow.model.VehicleModelData
import com.letyouknow.retrofit.RetrofitClient
import com.letyouknow.utils.AppGlobal
import com.pionymessenger.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


object VehicleModelRepository {

    fun getVehicleModelApiCall(
        context: Context,
        productId: String?,
        yearId: String?,
        makeID: String?,
        zipCode: String?
    ): MutableLiveData<ArrayList<VehicleModelData>> {
        val getVehicleModelData = MutableLiveData<ArrayList<VehicleModelData>>()
        val call = RetrofitClient.apiInterface.getVehicleModels(productId, yearId, makeID, zipCode)

        call.enqueue(object : Callback<ArrayList<VehicleModelData>> {
            override fun onFailure(call: Call<ArrayList<VehicleModelData>>, t: Throwable) {
                Log.v("DEBUG : ", t.message.toString())
            }

            override fun onResponse(
                call: Call<ArrayList<VehicleModelData>>,
                response: Response<ArrayList<VehicleModelData>>,
            ) {
                Log.v("DEBUG : ", response.body().toString())

                val data = response.body()
                if (response.code() == 200 || response.code() == 201) {
                    getVehicleModelData.value = data!!
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
        return getVehicleModelData
    }
}