package com.letyouknow.retrofit.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.letyouknow.model.VehiclePackagesData
import com.letyouknow.retrofit.RetrofitClient
import com.pionymessenger.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


object VehiclePackagesRepository {

    fun getVehiclePackagesCall(
        context: Context,
        productId: String?,
        yearId: String?,
        makeId: String?,
        modelId: String?,
        trimId: String?,
        exteriorColorId: String?,
        interiorColorId: String?,
        zipCode: String?
    ): MutableLiveData<ArrayList<VehiclePackagesData>> {
        val loginVo = MutableLiveData<ArrayList<VehiclePackagesData>>()
        val call = RetrofitClient.apiInterface.getVehiclePackages(
            productId,
            yearId,
            makeId,
            modelId,
            trimId,
            exteriorColorId,
            interiorColorId,
            zipCode
        )

        call.enqueue(object : Callback<ArrayList<VehiclePackagesData>> {
            override fun onFailure(call: Call<ArrayList<VehiclePackagesData>>, t: Throwable) {
                Log.v("DEBUG : ", t.message.toString())
            }

            override fun onResponse(
                call: Call<ArrayList<VehiclePackagesData>>,
                response: Response<ArrayList<VehiclePackagesData>>,
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