package com.letyouknow.retrofit.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.letyouknow.retrofit.RetrofitClient
import com.letyouknow.utils.AppGlobal
import com.pionymessenger.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


object InteriorRepository {

    fun interiorApiCall(
        context: Context,
        request: HashMap<String, Any>
    ): MutableLiveData<ArrayList<String>> {
        val interiorData = MutableLiveData<ArrayList<String>>()
        val call = RetrofitClient.apiInterface.interior360(request)

        call.enqueue(object : Callback<ArrayList<String>> {
            override fun onFailure(call: Call<ArrayList<String>>, t: Throwable) {
                Log.v("DEBUG : ", t.message.toString())
            }

            override fun onResponse(
                call: Call<ArrayList<String>>,
                response: Response<ArrayList<String>>,
            ) {
                Log.v("DEBUG : ", response.body().toString())

                val data = response.body()
                if (response.code() == 200 || response.code() == 201) {
                    Constant.dismissLoader()
                    interiorData.value = data!!
                } else if (response.code() == 401) {
                    AppGlobal.isAuthorizationFailed(context)
                } else {
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
        return interiorData
    }
}