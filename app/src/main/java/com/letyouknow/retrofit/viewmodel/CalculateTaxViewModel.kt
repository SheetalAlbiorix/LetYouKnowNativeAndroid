package com.letyouknow.retrofit.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.letyouknow.model.CalculateTaxData
import com.letyouknow.retrofit.repository.CalculateTaxRepository

class CalculateTaxViewModel : ViewModel() {
    var taxData: MutableLiveData<CalculateTaxData>? = null

    fun getCalculateTax(
        context: Context,
        priceBid: Double?,
        promoCodeDiscount: Double?,
        lykDollars: Double?,
        abbrev: String?
    ): LiveData<CalculateTaxData>? {
        taxData = CalculateTaxRepository.getCalculateTaxApiCall(
            context,
            priceBid,
            promoCodeDiscount,
            lykDollars,
            abbrev
        )
        return taxData
    }
}