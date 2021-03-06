package com.letyouknow.retrofit.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.letyouknow.retrofit.RetrofitClient
import com.letyouknow.utils.AppGlobal
import com.letyouknow.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


object VehicleZipCodeRepository {

    fun getVehicleZipCodeApiCall(
        context: Context,
        zipCode: String?
    ): MutableLiveData<Boolean> {
        AppGlobal.printRequestAuth("zipcode req", "zipCode: " + zipCode)
        val getVehicleZipCodeData = MutableLiveData<Boolean>()
        val call = RetrofitClient.apiInterface.isValidZip(zipCode)

        call.enqueue(object : Callback<Boolean> {
            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                Constant.dismissLoader()
                // Log.v("DEBUG : ", t.message.toString())
            }

            override fun onResponse(
                call: Call<Boolean>,
                response: Response<Boolean>,
            ) {

                val data = response.body()
                if (response.code() == 200 || response.code() == 201) {
                    //  Log.v("zipcode Resp : ", Gson().toJson(response.body()))
                    getVehicleZipCodeData.value = data!!
                } else {
                    // Log.v("zipcode Resp : ", response.toString())
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
        return getVehicleZipCodeData
    }
}