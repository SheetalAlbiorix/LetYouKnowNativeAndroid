package com.letyouknow.retrofit.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.letyouknow.model.FindLCDDeaData
import com.letyouknow.retrofit.repository.FindLCDDealRepository

class FindLCDDealViewModel : ViewModel() {
    var liveData: MutableLiveData<FindLCDDeaData>? = null

    fun findDeal(
        context: Context,
        request: HashMap<String, Any>
    ): LiveData<FindLCDDeaData>? {
        liveData = FindLCDDealRepository.findLCDDealApiCall(
            context,
            request
        )
        return liveData
    }
}