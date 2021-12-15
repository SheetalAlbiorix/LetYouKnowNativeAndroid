package com.letyouknow.retrofit.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.letyouknow.model.PromoCodeData
import com.letyouknow.retrofit.RetrofitClient
import com.letyouknow.utils.AppGlobal
import com.pionymessenger.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


object PromoCodeRepository {
    fun getPromoCodeApiCall(
        context: Context,
        promoCode: String?,
        dealerId: String?
    ): MutableLiveData<PromoCodeData> {
        AppGlobal.printRequestAuth(
            "Promo req",
            "promoCode: " + promoCode + ", " + "dealerId: " + dealerId
        )
        val getPromoCodeData = MutableLiveData<PromoCodeData>()
        val call = RetrofitClient.apiInterface.validatePromoCode(promoCode, dealerId)

        call.enqueue(object : Callback<PromoCodeData> {
            override fun onFailure(call: Call<PromoCodeData>, t: Throwable) {
                Constant.dismissLoader()
                Log.v("DEBUG : ", t.message.toString())
            }

            override fun onResponse(
                call: Call<PromoCodeData>,
                response: Response<PromoCodeData>,
            ) {

                val data = response.body()
                if (response.code() == 200 || response.code() == 201) {
                    Log.v("promoCode Resp ", Gson().toJson(response.body()))
                    getPromoCodeData.value = data!!
                } else {
                    Log.v("promoCode Resp ", response.toString())
                    val loginVoData = PromoCodeData()
                    loginVoData?.discount = 0.0f
                    loginVoData?.promotionID = "-1"
                    getPromoCodeData.value = loginVoData
                }
            }
        })
        return getPromoCodeData
    }
}