package com.letyouknow.retrofit.viewmodel

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.letyouknow.model.PromotionData
import com.letyouknow.retrofit.repository.PromotionGeneralRepository

class PromotionViewModel : ViewModel() {
    var data: MutableLiveData<PromotionData>? = null

    fun getPromoCode(
        context: Activity,
    ): LiveData<PromotionData>? {
        data = PromotionGeneralRepository.promotionApiCall(context)
        return data
    }
}