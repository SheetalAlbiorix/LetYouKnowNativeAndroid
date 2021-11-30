package com.letyouknow.retrofit.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.letyouknow.model.VehicleYearData
import com.letyouknow.retrofit.RetrofitClient
import com.letyouknow.utils.AppGlobal
import com.pionymessenger.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


object VehicleYearRepository {

    fun getVehicleYearApiCall(
        context: Context,
        productId: String?,
        zipCode: String?
    ): MutableLiveData<ArrayList<VehicleYearData>> {
        val getVehicleYearData = MutableLiveData<ArrayList<VehicleYearData>>()
        val call = RetrofitClient.apiInterface.getVehicleYears(productId, zipCode)

        call.enqueue(object : Callback<ArrayList<VehicleYearData>> {
            override fun onFailure(call: Call<ArrayList<VehicleYearData>>, t: Throwable) {
                Constant.dismissLoader()
                Log.v("DEBUG : ", t.message.toString())
            }

            override fun onResponse(
                call: Call<ArrayList<VehicleYearData>>,
                response: Response<ArrayList<VehicleYearData>>,
            ) {
                Log.v("DEBUG : ", response.body().toString())

                val data = response.body()
                if (response.code() == 200 || response.code() == 201) {
                    getVehicleYearData.value = data!!
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
        return getVehicleYearData
    }
}