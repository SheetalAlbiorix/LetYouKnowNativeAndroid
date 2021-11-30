package com.letyouknow.retrofit.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.letyouknow.LetYouKnowApp
import com.letyouknow.model.CardStripeData
import com.letyouknow.retrofit.RetrofitClient
import com.letyouknow.utils.AppGlobal
import com.pionymessenger.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


object PaymentMethodRepository {
//    ,
//    billing_detailsaddresspostal_code, guid, muid, sid, time_on_page, key
fun paymentMethodApiCall(
    context: Context,
    type: String?,
    cardnumber: String?,
    cardcvc: String?,
    cardexp_month: String?,
    cardexp_year: String?,
    billing_detailsaddresspostal_code: String?,
    guid: String?,
    muid: String?,
        sid: String?,
        time_on_page: String?,
        key: String?
    ): MutableLiveData<CardStripeData> {
        val paymentMethodData = MutableLiveData<CardStripeData>()
    val call = RetrofitClient.apiInterface.paymentMethods(
        type,
        cardnumber,
        cardcvc,
        cardexp_month,
        cardexp_year,
        billing_detailsaddresspostal_code
    )
        val pref = LetYouKnowApp.getInstance()?.getAppPreferencesHelper()

        call.enqueue(object : Callback<CardStripeData> {
            override fun onFailure(call: Call<CardStripeData>, t: Throwable) {
                pref?.setPaymentToken(false)
                Constant.dismissLoader()
                Log.v("DEBUG : ", t.message.toString())
            }

            override fun onResponse(
                call: Call<CardStripeData>,
                response: Response<CardStripeData>,
            ) {
                pref?.setPaymentToken(false)
                Log.v("DEBUG : ", response.body().toString())

                val data = response.body()
                if (response.code() == 200 || response.code() == 201) {
                    Constant.dismissLoader()
                    paymentMethodData.value = data!!
                } else if (response.code() == 401) {
                    AppGlobal.isAuthorizationFailed(context)
                } else {
                    Constant.dismissLoader()
                    response.errorBody()?.source()?.buffer?.snapshot()?.utf8()
                    Log.e(
                        "Error Payment",
                        response.errorBody()?.source()?.buffer?.snapshot()?.utf8()!!
                    )
                    if (response.errorBody()?.source()?.buffer?.snapshot()?.utf8() != null)
                        AppGlobal.alertError(
                            context,
                            response.errorBody()?.source()?.buffer?.snapshot()?.utf8()
                        )
                }
            }
        })
    return paymentMethodData
    }
}