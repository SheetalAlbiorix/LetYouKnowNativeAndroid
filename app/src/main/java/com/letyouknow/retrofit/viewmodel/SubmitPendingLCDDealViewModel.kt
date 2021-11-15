package com.letyouknow.retrofit.viewmodel

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.letyouknow.model.SubmitPendingUcdData
import com.letyouknow.retrofit.repository.SubmitPendingLCDDealRepository

class SubmitPendingLCDDealViewModel : ViewModel() {
    var liveData: MutableLiveData<SubmitPendingUcdData>? = null

    fun pendingDeal(
        context: Activity,
        request: HashMap<String, Any>
    ): LiveData<SubmitPendingUcdData>? {
        liveData = SubmitPendingLCDDealRepository.pendinLCDDealApiCall(
            context,
            request
        )
        return liveData
    }
}