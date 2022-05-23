package com.letyouknow.retrofit.viewmodel

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.letyouknow.model.ActiveMatchingData
import com.letyouknow.retrofit.repository.ActiveMatchingDealRepository

class ActiveMatchingDealViewModel : ViewModel() {
    var data: MutableLiveData<ActiveMatchingData>? = null

    fun matchingDeal(
        context: Activity,
        request: String
    ): LiveData<ActiveMatchingData>? {
        data = ActiveMatchingDealRepository.matchingDealApiCall(context, request)
        return data
    }
}