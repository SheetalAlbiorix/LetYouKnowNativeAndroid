package com.letyouknow.retrofit.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.letyouknow.retrofit.RetrofitClient
import com.pionymessenger.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


object ImageIdRepository {

    fun imageIdApiCall(
        context: Context,
        request: HashMap<String, Any>
    ): MutableLiveData<String> {
        val forgotPasswordVo = MutableLiveData<String>()
        val call = RetrofitClient.apiInterface.getImageId(request)

        call.enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.v("DEBUG : ", t.message.toString())
            }

            override fun onResponse(
                call: Call<String>,
                response: Response<String>,
            ) {
                Log.v("DEBUG : ", response.body().toString())

                val data = response.body()
                if (response.code() == 200 || response.code() == 201) {
                    Constant.dismissLoader()
                    forgotPasswordVo.value = data!!
                } else {
                    Constant.dismissLoader()
                    response.errorBody()?.source()?.buffer?.snapshot()?.utf8()
                    if (response.errorBody()?.source()?.buffer?.snapshot()
                            ?.utf8() != null
                    )
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