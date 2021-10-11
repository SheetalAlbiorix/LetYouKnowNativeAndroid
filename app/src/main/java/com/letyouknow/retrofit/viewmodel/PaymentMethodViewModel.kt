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
        request: HashMap<String, Any>
    ): LiveData<CardStripeData>? {
        liveData = PaymentMethodRepository.paymentMethodApiCall(
            context,
            request
        )
        return liveData
    }
}