package com.letyouknow.retrofit.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.letyouknow.model.CalculateTaxData
import com.letyouknow.retrofit.RetrofitClient
import com.letyouknow.utils.AppGlobal
import com.letyouknow.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


object CalculateTaxRepository {

    fun getCalculateTaxApiCall(
        context: Context,
        priceBid: Double?,
        promoCodeDiscount: Double?,
        lykDollars: Double?,
        abbrev: String?
    ): MutableLiveData<CalculateTaxData> {
        AppGlobal.printRequestAuth(
            "CalculateTax req",
            "priceBid: " + priceBid + " promoCodeDiscount: " + promoCodeDiscount + " lykDollars: " + lykDollars + " abbrev: " + abbrev
        )
        val taxData = MutableLiveData<CalculateTaxData>()
        val call = RetrofitClient.apiInterface.calculateTax(
            priceBid,
            promoCodeDiscount,
            lykDollars,
            abbrev
        )

        call.enqueue(object : Callback<CalculateTaxData> {
            override fun onFailure(call: Call<CalculateTaxData>, t: Throwable) {
                Constant.dismissLoader()
                // Log.v("DEBUG : ", t.message.toString())
            }

            override fun onResponse(
                call: Call<CalculateTaxData>,
                response: Response<CalculateTaxData>,
            ) {

                val data = response.body()
                if (response.code() == 200 || response.code() == 201) {
                    taxData.value = data!!
                } else {
                    taxData.value = CalculateTaxData()
                }
            }
        })
        return taxData
    }
}