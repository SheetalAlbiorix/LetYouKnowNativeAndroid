package com.letyouknow.retrofit.repository

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import com.letyouknow.model.PromotionData
import com.letyouknow.retrofit.RetrofitClient
import com.letyouknow.utils.AppGlobal
import com.letyouknow.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException


object PromotionGeneralRepository {
    fun promotionApiCall(
        context: Activity,
    ): MutableLiveData<PromotionData> {
        val promotionVo = MutableLiveData<PromotionData>()
//        request
        val call = RetrofitClient.apiInterface.promotionPublic()

        call.enqueue(object : Callback<PromotionData> {
            override fun onFailure(call: Call<PromotionData>, t: Throwable) {
                if (t is SocketTimeoutException) {

                    Constant.dismissLoader()
                    AppGlobal.alertError(
                        context,
                        "Socket Time out. Please try again."
                    )
                }
                // Log.v("DEBUG : ", t.message.toString())

            }

            override fun onResponse(
                call: Call<PromotionData>,
                response: Response<PromotionData>,
            ) {
                val data = response.body()
                if (response.code() == 200 || response.code() == 201) {
                    if (response.body() == null) {
                        promotionVo.value = PromotionData()
                    } else {
                        //  Log.v("promotion Resp : ", Gson().toJson(response.body()))
                        promotionVo.value = data!!
                    }
                } else if (response.code() == 500) {
                    AppGlobal.alertError(
                        context,
                        response.message()
                    )
                } else if (response.code() == 204) {
                    promotionVo.value = PromotionData()
                } else {
                    promotionVo.value = PromotionData()
                }
                Constant.dismissLoader()
            }
        })
        return promotionVo
    }
}