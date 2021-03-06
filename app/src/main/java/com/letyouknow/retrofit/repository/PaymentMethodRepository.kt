package com.letyouknow.retrofit.repository

import android.content.Context
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.letyouknow.LetYouKnowApp
import com.letyouknow.model.CardStripeData
import com.letyouknow.model.ErrorStripeData
import com.letyouknow.retrofit.RetrofitClient
import com.letyouknow.utils.AppGlobal
import com.letyouknow.utils.Constant
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
        AppGlobal.printRequestAuth(
            "payment req",
            "type: " + type + ", " + "cardnumber: " + cardnumber + ", " + "cardcvc: " + cardcvc + ", " + "cardexp_month: " + cardexp_month + ", " + "cardexp_year: " + cardexp_year + ", " + "billing_detailsaddresspostal_code: " + billing_detailsaddresspostal_code
        )
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
                //  Log.v("DEBUG : ", t.message.toString())
            }

            override fun onResponse(
                call: Call<CardStripeData>,
                response: Response<CardStripeData>,
            ) {
                pref?.setPaymentToken(false)
                // Log.v("DEBUG : ", response.body().toString())

                val data = response.body()
                if (response.code() == 200 || response.code() == 201) {
                    //  Log.v("payment Resp ", Gson().toJson(response.body()))
                    Constant.dismissLoader()
                    paymentMethodData.value = data!!
                } else if (response.code() == 401) {
                    //  Log.v("payment Resp ", response.toString())
                    AppGlobal.isAuthorizationFailed(context)
                } else {
                    // Log.v("payment Resp ", response.toString())
                    Constant.dismissLoader()
                    response.errorBody()?.source()?.buffer?.snapshot()?.utf8()
                    // Log.e("Error Payment", response.errorBody()?.source()?.buffer?.snapshot()?.utf8()!!)
                    if (response.errorBody()?.source()?.buffer?.snapshot()?.utf8() != null) {
                        Constant.dismissLoader()
                        val dataError = Gson().fromJson(
                            response.errorBody()?.source()?.buffer?.snapshot()?.utf8(),
                            ErrorStripeData::class.java
                        )

                        var msgStr = dataError.error?.message

                        if (!TextUtils.isEmpty(msgStr))
                            AppGlobal.alertError(
                                context,
                                msgStr
                            )
                    }
                }
            }
        })
        return paymentMethodData
    }
}