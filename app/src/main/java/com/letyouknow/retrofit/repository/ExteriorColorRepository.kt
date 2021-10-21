package com.letyouknow.retrofit.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.letyouknow.model.ExteriorColorData
import com.letyouknow.retrofit.RetrofitClient
import com.letyouknow.utils.AppGlobal
import com.pionymessenger.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


object ExteriorColorRepository {

    fun getExteriorColorCall(
        context: Context,
        productId: String?,
        yearId: String?,
        makeId: String?,
        modelId: String?,
        trimId: String?,
        zipCode: String?
    ): MutableLiveData<ArrayList<ExteriorColorData>> {
        val getExteriorColorData = MutableLiveData<ArrayList<ExteriorColorData>>()
        val call = RetrofitClient.apiInterface.getVehicleExteriorColors(
            productId,
            yearId,
            makeId,
            modelId,
            trimId,
            zipCode
        )

        call.enqueue(object : Callback<ArrayList<ExteriorColorData>> {
            override fun onFailure(call: Call<ArrayList<ExteriorColorData>>, t: Throwable) {
                Log.v("DEBUG : ", t.message.toString())
            }

            override fun onResponse(
                call: Call<ArrayList<ExteriorColorData>>,
                response: Response<ArrayList<ExteriorColorData>>,
            ) {
                Log.v("DEBUG : ", response.body().toString())

                val data = response.body()
                if (response.code() == 200 || response.code() == 201) {
                    getExteriorColorData.value = data!!
                } else {
                    Constant.dismissLoader()
                    response.errorBody()?.source()?.buffer?.snapshot()?.utf8()
                    AppGlobal.alertError(
                        context,
                        response.errorBody()?.source()?.buffer?.snapshot()?.utf8()
                    )
                }
            }
        })
        return getExteriorColorData
    }
}