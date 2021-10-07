package com.letyouknow.retrofit.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.letyouknow.model.SubmitPendingUcdData
import com.letyouknow.retrofit.repository.SubmitPendingUCDDealRepository

class SubmitPendingUCDDealViewModel : ViewModel() {
    var liveData: MutableLiveData<SubmitPendingUcdData>? = null

    fun pendingDeal(
        context: Context,
        request: HashMap<String, Any>
    ): LiveData<SubmitPendingUcdData>? {
        liveData = SubmitPendingUCDDealRepository.pendingUCDDealApiCall(
            context,
            request
        )
        return liveData
    }
}