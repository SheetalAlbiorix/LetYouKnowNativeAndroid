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


object ImageIdGroupRepository {

    fun imageIdApiCall(
        context: Context,
        request: HashMap<String, Any>
    ): MutableLiveData<String> {
        AppGlobal.printRequestAuth("ImageID req", Gson().toJson(request))
        val imageIdData = MutableLiveData<String>()
        val call = RetrofitClient.apiInterface.getImageIdGroup(request)

        call.enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                Constant.dismissLoader()
                //  Log.v("DEBUG : ", t.message.toString())
            }

            override fun onResponse(
                call: Call<String>,
                response: Response<String>,
            ) {
                // Log.v("DEBUG : ", response.body().toString())

                val data = response.body()
                if (response.code() == 200 || response.code() == 201) {
                    // Log.v("imgID Resp ", Gson().toJson(response.body()))
                    Constant.dismissLoader()
                    imageIdData.value = data!!
                } else {
                    // Log.v("imgID Resp ", response.toString())
                    Constant.dismissLoader()
                    /*  response.errorBody()?.source()?.buffer?.snapshot()?.utf8()
                      if (response.errorBody()?.source()?.buffer?.snapshot()
                              ?.utf8() != null
                      )
                          AppGlobal.alertError(
                              context,
                              response.errorBody()?.source()?.buffer?.snapshot()?.utf8()
                          )*/
                    imageIdData.value = "0"
                }
            }
        })
        return imageIdData
    }
}