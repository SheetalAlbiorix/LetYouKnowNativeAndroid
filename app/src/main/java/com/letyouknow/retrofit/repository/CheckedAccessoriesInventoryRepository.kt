package com.letyouknow.retrofit.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.letyouknow.model.CheckedPackageData
import com.letyouknow.retrofit.RetrofitClient
import com.letyouknow.utils.AppGlobal
import com.pionymessenger.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


object CheckedAccessoriesInventoryRepository {

    fun checkedAccessoriesInventoryApiCall(
        context: Context,
        request: HashMap<String, Any>
    ): MutableLiveData<CheckedPackageData> {
        AppGlobal.printRequestAuth("checkAcc req", Gson().toJson(request))
        val checkedAccessoriesInventoryData = MutableLiveData<CheckedPackageData>()
        val call = RetrofitClient.apiInterface.checkVehicleAccessoriesInventory(request)

        call.enqueue(object : Callback<CheckedPackageData> {
            override fun onFailure(call: Call<CheckedPackageData>, t: Throwable) {
                Constant.dismissLoader()
                Log.v("DEBUG : ", t.message.toString())
            }

            override fun onResponse(
                call: Call<CheckedPackageData>,
                response: Response<CheckedPackageData>,
            ) {
                Log.v("DEBUG : ", response.body().toString())

                val data = response.body()
                if (response.code() == 200 || response.code() == 201) {
                    Log.v("checkAcc Resp ", Gson().toJson(response.body()))
                    Constant.dismissLoader()
                    checkedAccessoriesInventoryData.value = data!!
                } else {
                    Log.v("checkAcc Resp ", response.toString())
                    Constant.dismissLoader()
                    response.errorBody()?.source()?.buffer?.snapshot()?.utf8()
                    AppGlobal.alertError(
                        context,
                        response.errorBody()?.source()?.buffer?.snapshot()?.utf8()
                    )
                }
            }
        })
        return checkedAccessoriesInventoryData
    }
}