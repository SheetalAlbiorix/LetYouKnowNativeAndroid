package com.letyouknow.retrofit.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.letyouknow.model.SubmitDealLCDData
import com.letyouknow.retrofit.repository.SubmitDealUCDRepository

class SubmitDealUCDViewModel : ViewModel() {
    var liveData: MutableLiveData<SubmitDealLCDData>? = null

    fun submitDealLCDCall(
        context: Context,
        request: HashMap<String, Any>
    ): LiveData<SubmitDealLCDData>? {
        liveData = SubmitDealUCDRepository.submitDealUCDApiCall(
            context,
            request
        )
        return liveData
    }
}