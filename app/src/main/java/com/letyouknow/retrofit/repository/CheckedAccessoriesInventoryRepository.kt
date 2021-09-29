package com.letyouknow.retrofit.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.letyouknow.model.CheckedPackageData
import com.letyouknow.retrofit.RetrofitClient
import com.pionymessenger.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


object CheckedAccessoriesInventoryRepository {

    fun checkedAccessoriesInventoryApiCall(
        context: Context,
        request: HashMap<String, Any>
    ): MutableLiveData<CheckedPackageData> {
        val forgotPasswordVo = MutableLiveData<CheckedPackageData>()
        val call = RetrofitClient.apiInterface.checkVehicleAccessoriesInventory(request)

        call.enqueue(object : Callback<CheckedPackageData> {
            override fun onFailure(call: Call<CheckedPackageData>, t: Throwable) {
                Log.v("DEBUG : ", t.message.toString())
            }

            override fun onResponse(
                call: Call<CheckedPackageData>,
                response: Response<CheckedPackageData>,
            ) {
                Log.v("DEBUG : ", response.body().toString())

                val data = response.body()
                if (response.code() == 200 || response.code() == 201) {
                    Constant.dismissLoader()
                    forgotPasswordVo.value = data!!
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
        return forgotPasswordVo
    }
}