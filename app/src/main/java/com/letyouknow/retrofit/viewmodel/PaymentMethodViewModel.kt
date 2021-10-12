package com.letyouknow.retrofit.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.letyouknow.model.CardStripeData
import com.letyouknow.retrofit.repository.PaymentMethodRepository

class PaymentMethodViewModel : ViewModel() {
    var liveData: MutableLiveData<CardStripeData>? = null

    fun callPayment(
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
    ): LiveData<CardStripeData>? {
        liveData = PaymentMethodRepository.paymentMethodApiCall(
            context,
            type, cardnumber, cardcvc, cardexp_month, cardexp_year,
            billing_detailsaddresspostal_code, guid, muid, sid, time_on_page, key
        )
        return liveData
    }
}