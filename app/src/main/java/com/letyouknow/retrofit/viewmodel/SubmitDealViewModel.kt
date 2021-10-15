package com.letyouknow.retrofit.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.letyouknow.model.SubmitDealLCDData
import com.letyouknow.retrofit.repository.SubmitDealRepository

class SubmitDealViewModel : ViewModel() {
    var liveData: MutableLiveData<SubmitDealLCDData>? = null

    fun submitDealCall(
        context: Context,
        request: HashMap<String, Any>
    ): LiveData<SubmitDealLCDData>? {
        liveData = SubmitDealRepository.submitDealApiCall(
            context,
            request
        )
        return liveData
    }
}