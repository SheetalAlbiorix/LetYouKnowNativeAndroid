package com.letyouknow.retrofit.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.letyouknow.model.PromoCodeData
import com.letyouknow.retrofit.repository.PromoCodeRepository

class PromoCodeViewModel : ViewModel() {
    var liveData: MutableLiveData<PromoCodeData>? = null

    fun getPromoCode(
        context: Context,
        promoCode: String?,
        dealerId: String?
    ): LiveData<PromoCodeData>? {
        liveData = PromoCodeRepository.getPromoCodeApiCall(context, promoCode, dealerId)
        return liveData
    }
}