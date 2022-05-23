package com.letyouknow.retrofit.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.letyouknow.retrofit.RetrofitClient
import com.letyouknow.utils.AppGlobal
import com.letyouknow.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


object InteriorRepository {

    fun interiorApiCall(
        context: Context,
        request: HashMap<String, Any>
    ): MutableLiveData<ArrayList<String>> {
        AppGlobal.printRequestAuth("InteriorImg req", Gson().toJson(request))
        val interiorData = MutableLiveData<ArrayList<String>>()
        val call = RetrofitClient.apiInterface.interior360(request)

        call.enqueue(object : Callback<ArrayList<String>> {
            override fun onFailure(call: Call<ArrayList<String>>, t: Throwable) {
                Constant.dismissLoader()
                // Log.v("DEBUG : ", t.message.toString())
            }

            override fun onResponse(
                call: Call<ArrayList<String>>,
                response: Response<ArrayList<String>>,
            ) {
                val data = response.body()
                if (response.code() == 200 || response.code() == 201) {
                    //  Log.v("interior Resp ", Gson().toJson(response.body()))
                    //  Constant.dismissLoader()
                    interiorData.value = data!!
                } else if (response.code() == 401) {
                    Constant.dismissLoader()
                    //  Log.v("interior Resp ", response.toString())
                    AppGlobal.isAuthorizationFailed(context)
                } else {
                    //  Log.v("interior Resp ", response.toString())
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