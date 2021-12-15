package com.letyouknow.retrofit.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.letyouknow.model.InteriorColorData
import com.letyouknow.retrofit.RetrofitClient
import com.letyouknow.utils.AppGlobal
import com.pionymessenger.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


object InteriorColorRepository {

    fun getInteriorColorCall(
        context: Context,
        productId: String?,
        yearId: String?,
        makeId: String?,
        modelId: String?,
        trimId: String?,
        exteriorColorId: String?,
        zipCode: String?
    ): MutableLiveData<ArrayList<InteriorColorData>> {
        AppGlobal.printRequestAuth(
            "Interior req",
            "ProductId: " + productId + ", " + "yearId: " + yearId + ", " + "makeId: " + makeId + ", " + "modelId: " + modelId + ", " + "trimId: " + trimId + ", " + "exteriorColorId: " + exteriorColorId + ", " + "zipCode: " + zipCode
        )
        val getInteriorColorData = MutableLiveData<ArrayList<InteriorColorData>>()
        val call = RetrofitClient.apiInterface.getVehicleInteriorColors(
            productId,
            yearId,
            makeId,
            modelId,
            trimId,
            exteriorColorId,
            zipCode
        )

        call.enqueue(object : Callback<ArrayList<InteriorColorData>> {
            override fun onFailure(call: Call<ArrayList<InteriorColorData>>, t: Throwable) {
                Constant.dismissLoader()
                Log.v("DEBUG : ", t.message.toString())
            }

            override fun onResponse(
                call: Call<ArrayList<InteriorColorData>>,
                response: Response<ArrayList<InteriorColorData>>,
            ) {
                Log.v("DEBUG : ", response.body().toString())

                val data = response.body()
                if (response.code() == 200 || response.code() == 201) {
                    Log.v("interiorColor Resp ", Gson().toJson(response.body()))
                    getInteriorColorData.value = data!!
                } else {
                    Log.v("interiorColor Resp ", response.toString())
                    Constant.dismissLoader()
                    response.errorBody()?.source()?.buffer?.snapshot()?.utf8()
                    AppGlobal.alertError(
                        context,
                        response.errorBody()?.source()?.buffer?.snapshot()?.utf8()
                    )
                }
            }
        })
        return getInteriorColorData
    }
}