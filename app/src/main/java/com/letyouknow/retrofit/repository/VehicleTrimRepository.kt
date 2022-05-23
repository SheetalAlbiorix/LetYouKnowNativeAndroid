package com.letyouknow.retrofit.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.letyouknow.model.VehicleTrimData
import com.letyouknow.retrofit.RetrofitClient
import com.letyouknow.utils.AppGlobal
import com.letyouknow.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


object VehicleTrimRepository {

    fun getVehicleTrimApiCall(
        context: Context,
        productId: String?,
        yearId: String?,
        makeId: String?,
        modelId: String?,
        zipCode: String?
    ): MutableLiveData<ArrayList<VehicleTrimData>> {
        AppGlobal.printRequestAuth(
            "Trim req",
            "ProductId: " + productId + ", " + "yearId: " + yearId + ", " + "makeId: " + makeId + ", " + "modelId: " + modelId + ", zipCode: " + zipCode
        )
        val getVehicleTrimData = MutableLiveData<ArrayList<VehicleTrimData>>()
        val call =
            RetrofitClient.apiInterface.getVehicleTrims(productId, yearId, makeId, modelId, zipCode)

        call.enqueue(object : Callback<ArrayList<VehicleTrimData>> {
            override fun onFailure(call: Call<ArrayList<VehicleTrimData>>, t: Throwable) {
                Constant.dismissLoader()
                //   Log.v("DEBUG : ", t.message.toString())
            }

            override fun onResponse(
                call: Call<ArrayList<VehicleTrimData>>,
                response: Response<ArrayList<VehicleTrimData>>,
            ) {

                val data = response.body()
                if (response.code() == 200 || response.code() == 201) {
                    //  Log.v("trim Resp : ", Gson().toJson(response.body()))
                    getVehicleTrimData.value = data!!
                } else {
                    // Log.v("trim Resp : ", response.toString())
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
        return getVehicleTrimData
    }
}