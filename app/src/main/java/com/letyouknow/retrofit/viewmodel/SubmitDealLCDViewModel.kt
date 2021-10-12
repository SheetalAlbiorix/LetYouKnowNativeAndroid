package com.letyouknow.retrofit.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.letyouknow.retrofit.SubmitDealLCDData
import com.letyouknow.retrofit.repository.SubmitDealLCDRepository

class SubmitDealLCDViewModel : ViewModel() {
    var liveData: MutableLiveData<SubmitDealLCDData>? = null

    fun submitDealLCDCall(
        context: Context,
        request: HashMap<String, Any>
    ): LiveData<SubmitDealLCDData>? {
        liveData = SubmitDealLCDRepository.submitDealLCDApiCall(
            context,
            request
        )
        return liveData
    }
}