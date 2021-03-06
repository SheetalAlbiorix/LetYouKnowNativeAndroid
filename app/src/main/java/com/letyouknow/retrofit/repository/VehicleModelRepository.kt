package com.letyouknow.retrofit.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.letyouknow.model.VehicleModelData
import com.letyouknow.retrofit.RetrofitClient
import com.letyouknow.utils.AppGlobal
import com.letyouknow.utils.Constant
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
        AppGlobal.printRequestAuth(
            "make req",
            "productId: " + productId + ", yearId: " + yearId + ", makeID: " + makeID + ", zipCode: " + zipCode
        )
        val getVehicleModelData = MutableLiveData<ArrayList<VehicleModelData>>()
        val call = RetrofitClient.apiInterface.getVehicleModels(productId, yearId, makeID, zipCode)

        call.enqueue(object : Callback<ArrayList<VehicleModelData>> {
            override fun onFailure(call: Call<ArrayList<VehicleModelData>>, t: Throwable) {
                Constant.dismissLoader()
                // Log.v("DEBUG : ", t.message.toString())
            }

            override fun onResponse(
                call: Call<ArrayList<VehicleModelData>>,
                response: Response<ArrayList<VehicleModelData>>,
            ) {

                val data = response.body()
                if (response.code() == 200 || response.code() == 201) {
                    // Log.v("model Resp : ", Gson().toJson(response.body()))
                    getVehicleModelData.value = data!!
                } else {
                    // Log.v("model Resp : ", response.toString())
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