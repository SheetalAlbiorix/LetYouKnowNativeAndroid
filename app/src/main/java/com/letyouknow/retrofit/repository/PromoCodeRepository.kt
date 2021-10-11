package com.letyouknow.retrofit.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.letyouknow.model.PromoCodeData
import com.letyouknow.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


object PromoCodeRepository {
    fun getPromoCodeApiCall(
        context: Context,
        promoCode: String?,
        dealerId: String?
    ): MutableLiveData<PromoCodeData> {
        val loginVo = MutableLiveData<PromoCodeData>()
        val call = RetrofitClient.apiInterface.validatePromoCode(promoCode, dealerId)

        call.enqueue(object : Callback<PromoCodeData> {
            override fun onFailure(call: Call<PromoCodeData>, t: Throwable) {
                Log.v("DEBUG : ", t.message.toString())
            }

            override fun onResponse(
                call: Call<PromoCodeData>,
                response: Response<PromoCodeData>,
            ) {
                Log.v("DEBUG : ", response.body().toString())

                val data = response.body()
                if (response.code() == 200 || response.code() == 201) {
                    loginVo.value = data!!
                } else {
                    val loginVoData = PromoCodeData()
                    loginVoData?.discount = 0.0f
                    loginVoData?.promotionID = "-1"
                    loginVo.value = loginVoData
                }
            }
        })
        return loginVo
    }
}