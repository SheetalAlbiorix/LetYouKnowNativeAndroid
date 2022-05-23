package com.letyouknow.retrofit.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.letyouknow.model.VehicleAccessoriesData
import com.letyouknow.retrofit.RetrofitClient
import com.letyouknow.utils.AppGlobal
import com.letyouknow.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


object VehicleOptionalAccessoriesRepository {
    fun getOptionalCall(
        context: Context,
        request: HashMap<String, Any>
    ): MutableLiveData<ArrayList<VehicleAccessoriesData>> {
        AppGlobal.printRequestAuth("OptionalAcc req", Gson().toJson(request))
        val getOptionalData = MutableLiveData<ArrayList<VehicleAccessoriesData>>()
        val call = RetrofitClient.apiInterface.getVehicleDealerAccessories(
            request
        )

        call.enqueue(object : Callback<ArrayList<VehicleAccessoriesData>> {
            override fun onFailure(call: Call<ArrayList<VehicleAccessoriesData>>, t: Throwable) {
                Constant.dismissLoader()
                // Log.v("DEBUG : ", t.message.toString())
            }

            override fun onResponse(
                call: Call<ArrayList<VehicleAccessoriesData>>,
                response: Response<ArrayList<VehicleAccessoriesData>>,
            ) {

                val data = response.body()
                if (response.code() == 200 || response.code() == 201) {
                    // Log.v("Acc Resp : ", Gson().toJson(response.body()))
                    getOptionalData.value = data!!
                } else {
                    // Log.v("Acc Resp : ", response.toString())
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
        return getOptionalData
    }
}