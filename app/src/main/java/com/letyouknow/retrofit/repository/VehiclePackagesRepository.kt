package com.letyouknow.retrofit.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.letyouknow.model.VehiclePackagesData
import com.letyouknow.retrofit.RetrofitClient
import com.letyouknow.utils.AppGlobal
import com.letyouknow.utils.Constant
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
        AppGlobal.printRequestAuth(
            "Package req",
            "ProductId: " + productId + ", " + "yearId: " + yearId + ", " + "makeId: " + makeId + ", " + "modelId: " + modelId + ", " + "trimId: " + trimId + ", " + "exteriorColorId: " + exteriorColorId + ", " + "interiorColorId: " + interiorColorId + ", " + "zipCode: " + zipCode
        )
        val getVehiclePackagesData = MutableLiveData<ArrayList<VehiclePackagesData>>()
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
                Constant.dismissLoader()
                // Log.v("DEBUG : ", t.message.toString())
            }

            override fun onResponse(
                call: Call<ArrayList<VehiclePackagesData>>,
                response: Response<ArrayList<VehiclePackagesData>>,
            ) {

                val data = response.body()
                if (response.code() == 200 || response.code() == 201) {
                    //  Log.v("Package Resp : ", Gson().toJson(response.body()))
                    getVehiclePackagesData.value = data!!
                } else {
                    //Log.v("Package Resp : ", response.toString())
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
        return getVehiclePackagesData
    }
}