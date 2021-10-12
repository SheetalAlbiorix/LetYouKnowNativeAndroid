package com.letyouknow.retrofit.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.letyouknow.model.BuyerInfoData
import com.letyouknow.retrofit.RetrofitClient
import com.pionymessenger.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


object BuyerRepository {

    fun buyerApiCall(
        context: Context,
        request: HashMap<String, Any>
    ): MutableLiveData<BuyerInfoData> {
        val forgotPasswordVo = MutableLiveData<BuyerInfoData>()
        val call = RetrofitClient.apiInterface.buyer(request)

        call.enqueue(object : Callback<BuyerInfoData> {
            override fun onFailure(call: Call<BuyerInfoData>, t: Throwable) {
                Log.v("DEBUG : ", t.message.toString())
            }

            override fun onResponse(
                call: Call<BuyerInfoData>,
                response: Response<BuyerInfoData>,
            ) {
                Log.v("DEBUG : ", response.body().toString())

                val data = response.body()
                if (response.code() == 200 || response.code() == 201) {
                    Constant.dismissLoader()
                    forgotPasswordVo.value = data!!
                } else {
                    Constant.dismissLoader()
                    response.errorBody()?.source()?.buffer?.snapshot()?.utf8()
                    if (response.errorBody()?.source()?.buffer?.snapshot()?.utf8() != null)
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