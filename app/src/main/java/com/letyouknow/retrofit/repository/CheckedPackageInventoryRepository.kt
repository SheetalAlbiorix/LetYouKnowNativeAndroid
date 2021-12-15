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


object CheckedPackageInventoryRepository {

    fun checkedPackageInventoryApiCall(
        context: Context,
        request: HashMap<String, Any>
    ): MutableLiveData<CheckedPackageData> {
        AppGlobal.printRequestAuth("checkPack req", Gson().toJson(request))
        val checkedPackageInventoryData = MutableLiveData<CheckedPackageData>()
        val call = RetrofitClient.apiInterface.checkVehiclePackagesInventory(request)

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
                    Log.v("checkPack Resp ", Gson().toJson(response.body()))
                    Constant.dismissLoader()
                    checkedPackageInventoryData.value = data!!
                } else {
                    Log.v("checkPack Resp ", response.toString())
                    Constant.dismissLoader()
                    response.errorBody()?.source()?.buffer?.snapshot()?.utf8()
                    AppGlobal.alertError(
                        context,
                        response.errorBody()?.source()?.buffer?.snapshot()?.utf8()
                    )
                }
            }
        })
        return checkedPackageInventoryData
    }
}